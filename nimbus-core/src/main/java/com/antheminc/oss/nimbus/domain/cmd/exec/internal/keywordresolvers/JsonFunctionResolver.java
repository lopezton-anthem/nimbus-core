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
package com.antheminc.oss.nimbus.domain.cmd.exec.internal.keywordresolvers;

import org.apache.commons.lang3.StringUtils;

import com.antheminc.oss.nimbus.domain.cmd.CommandMessageConverter;
import com.antheminc.oss.nimbus.domain.cmd.exec.CommandExecutorGateway;
import com.antheminc.oss.nimbus.domain.defn.Constants;
import com.antheminc.oss.nimbus.domain.model.state.EntityState.Param;

/**
 * 
 * @author Soham Chakravarti
 * @author Tony Lopez
 *
 */
public class JsonFunctionResolver extends FunctionResolver {

	private static final String NULL_STRING = "null";

	private final CommandMessageConverter converter;

	public JsonFunctionResolver(String keyword, CommandExecutorGateway executorGateway,
			CommandMessageConverter converter) {
		super(keyword, executorGateway);
		this.converter = converter;
	}

	@Override
	public String execute(Param<?> param, String[] args) {
		String paramPath = args[0];
		Object state;
		if (StringUtils.startsWithIgnoreCase(paramPath, Constants.SEGMENT_PLATFORM_MARKER.code)) {
			state = mapCrossDomain(param, paramPath);
		} else {
			Param<?> p = param.findParamByPath(paramPath);
			if (null == p) {
				p = param.getParentModel().findParamByPath(paramPath);
			}
			if (null == p) {
				logit.error(() -> new StringBuffer().append(" Param (using paramPath) ").append(paramPath)
						.append(" not found from param reference: ").append(param).toString());
				return NULL_STRING;
			}
			state = p.getLeafState();
		}
		String json = converter.toJson(state);
		return String.valueOf(json);
	}
}
