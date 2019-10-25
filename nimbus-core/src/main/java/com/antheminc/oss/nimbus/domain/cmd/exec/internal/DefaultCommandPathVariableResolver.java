/**
 *  Copyright 2016-2019 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.antheminc.oss.nimbus.domain.cmd.exec.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertyResolver;

import com.antheminc.oss.nimbus.InvalidConfigException;
import com.antheminc.oss.nimbus.context.BeanResolverStrategy;
import com.antheminc.oss.nimbus.domain.cmd.CommandMessageConverter;
import com.antheminc.oss.nimbus.domain.cmd.exec.CommandExecutorGateway;
import com.antheminc.oss.nimbus.domain.cmd.exec.CommandPathVariableResolver;
import com.antheminc.oss.nimbus.domain.cmd.exec.ParamPathExpressionParser;
import com.antheminc.oss.nimbus.domain.cmd.exec.internal.keywordresolvers.ElemIdResolver;
import com.antheminc.oss.nimbus.domain.cmd.exec.internal.keywordresolvers.EnvResolver;
import com.antheminc.oss.nimbus.domain.cmd.exec.internal.keywordresolvers.JsonFunctionResolver;
import com.antheminc.oss.nimbus.domain.cmd.exec.internal.keywordresolvers.KeywordResolver;
import com.antheminc.oss.nimbus.domain.cmd.exec.internal.keywordresolvers.PlatformMarkerResolver;
import com.antheminc.oss.nimbus.domain.cmd.exec.internal.keywordresolvers.RefIdResolver;
import com.antheminc.oss.nimbus.domain.cmd.exec.internal.keywordresolvers.SelfResolver;
import com.antheminc.oss.nimbus.domain.cmd.exec.internal.keywordresolvers.ThisResolver;
import com.antheminc.oss.nimbus.domain.defn.Constants;
import com.antheminc.oss.nimbus.domain.model.state.EntityState.Param;
import com.antheminc.oss.nimbus.domain.session.SessionProvider;
import com.antheminc.oss.nimbus.support.EnableLoggingInterceptor;
import com.antheminc.oss.nimbus.support.JustLogit;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * @author Soham Chakravarti
 * @author Tony Lopez
 *
 */
@EnableLoggingInterceptor
@Getter(value = AccessLevel.PROTECTED)
public class DefaultCommandPathVariableResolver implements CommandPathVariableResolver {

	private static final String NULL_STRING = "null";
	private static final String NULL_STRING_REGEX = "\\s*\"null\"\\s*";
	private static final Pattern NULL_STRING_PATTERN = Pattern.compile(NULL_STRING_REGEX);

	private final CommandMessageConverter converter;
	private final Environment environment;
	private final CommandExecutorGateway executorGateway;
	private final PropertyResolver propertyResolver;
	private final SessionProvider sessionProvider;
	private final List<KeywordResolver> keywordResolvers = new ArrayList<>();
	private final JsonFunctionResolver jsonFunctionResolver;

	protected final JustLogit logit = new JustLogit(DefaultCommandPathVariableResolver.class);

	public DefaultCommandPathVariableResolver(BeanResolverStrategy beanResolver, PropertyResolver propertyResolver) {
		this.converter = beanResolver.get(CommandMessageConverter.class);
		this.propertyResolver = propertyResolver;
		this.sessionProvider = beanResolver.get(SessionProvider.class);
		this.environment = beanResolver.get(Environment.class);
		this.executorGateway = beanResolver.get(CommandExecutorGateway.class);

		registerDefaultKeywordResolvers();
		this.jsonFunctionResolver = new JsonFunctionResolver(Constants.MARKER_FN_JSON.code, executorGateway, converter);
	}

	public void registerDefaultKeywordResolvers() {
		registerResolver(new SelfResolver(Constants.MARKER_SESSION_SELF.code, sessionProvider));
		registerResolver(new EnvResolver(Constants.MARKER_ENV.code, environment));
		registerResolver(new ThisResolver(Constants.MARKER_COMMAND_PARAM_CURRENT_SELF.code));
		registerResolver(new RefIdResolver(Constants.MARKER_REF_ID.code));
		registerResolver(new ElemIdResolver(Constants.MARKER_ELEM_ID.code));
		registerResolver(new PlatformMarkerResolver(Constants.SEGMENT_PLATFORM_MARKER.code, executorGateway));
	}

	public void registerResolver(KeywordResolver resolver) {
		this.keywordResolvers.add(resolver);
	}

	@Override
	public String resolve(Param<?> param, String urlToResolve) {
		if (StringUtils.trimToNull(urlToResolve) == null)
			return urlToResolve;

		// resolve property place-holders first
		try {
			String resolvedPlaceHolders = getPropertyResolver().resolveRequiredPlaceholders(urlToResolve);
			return resolveInternal(param, resolvedPlaceHolders);
		} catch (RuntimeException ex) {
			throw new InvalidConfigException(
					"Failed to resolve with property place-holders for param: " + param + " with url: " + urlToResolve,
					ex);
		}
	}

	protected String map(Param<?> param, String pathToResolve) {
		// handle recursive
		if (ParamPathExpressionParser.containsPrefixSuffix(pathToResolve)) {
			String recursedPath = resolveInternal(param, pathToResolve);
			pathToResolve = recursedPath;
		}

		for (KeywordResolver keywordResolver : keywordResolvers) {
			if (keywordResolver.shouldApply(pathToResolve)) {
				return keywordResolver.resolve(param, pathToResolve);
			}
		}

		return mapQuad(param, pathToResolve);
	}

	protected String mapQuad(Param<?> param, String pathToResolve) {
		if (jsonFunctionResolver.shouldApply(pathToResolve)) {
			return jsonFunctionResolver.resolve(param, pathToResolve);
		}

		Param<?> p = param.findParamByPath(pathToResolve);
		if (null == p) {
			p = param.getParentModel().findParamByPath(pathToResolve);
		}
		if (null == p) {
			logit.error(() -> new StringBuffer().append(" Param (using paramPath) ").append(pathToResolve)
					.append(" not found from param reference: ").append(param).toString());
			return NULL_STRING;
		}
		return String.valueOf(p.getState());
	}

	protected String resolveInternal(Param<?> param, String urlToResolve) {
		Map<Integer, String> entries = ParamPathExpressionParser.parse(urlToResolve);
		if (MapUtils.isEmpty(entries))
			return urlToResolve;

		String out = urlToResolve;
		for (Integer i : entries.keySet()) {
			String key = entries.get(i);

			// look for relative path to passed in param's parent model
			String pathToResolve = ParamPathExpressionParser.stripPrefixSuffix(key);

			String val = map(param, pathToResolve);

			out = StringUtils.replace(out, key, val, 1);
		}

		Matcher m = NULL_STRING_PATTERN.matcher(out);
		out = m.replaceAll(NULL_STRING); // replaces all json="null" (including
											// leading/trailing spaces) to
											// json=null
		return out;
	}
}
