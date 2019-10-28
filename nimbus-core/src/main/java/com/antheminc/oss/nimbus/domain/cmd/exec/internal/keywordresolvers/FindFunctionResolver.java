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

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.drools.core.util.StringUtils;

import com.antheminc.oss.nimbus.FrameworkRuntimeException;
import com.antheminc.oss.nimbus.domain.cmd.exec.CommandExecutorGateway;
import com.antheminc.oss.nimbus.domain.defn.Constants;
import com.antheminc.oss.nimbus.domain.model.state.EntityState.Param;

/**
 * <p> The primary logic for the Command Query DSL function:
 * {@code <!find(pathToFind)!>} <p> {@code find} can be used in a Command Query
 * DSL statement to retrieve a parameter relative to the given parameter that
 * matches the given pattern, {@code pathToFind}. {@code pathToFind} can be a
 * single or nested path to an expected parameter and must not be null or empty.
 * <p> If a parameter is not found, an {@link FrameworkRuntimeException} will be
 * thrown.
 * 
 * <p><b>Example</b></p> Consider the following domain definition:
 * 
 * <pre>
 * public class SampleTraversalEntity extends IdLong {
 * 
 * 	private InnerA p1;
 * 
 * 	public static class InnerA {
 * 		private String a1;
 * 	}
 * }
 * </pre>
 * 
 * <p> Then a {@code @Config} statement like
 * {@code @Config(url = "<!find(/a1)!>")}, executed from the context of a
 * {@code SampleTraversalEntity} param would resolve to {@code "/p1/a1"}.</p>
 * 
 * @author Tony Lopez
 *
 */
public class FindFunctionResolver extends FunctionResolver {

	public FindFunctionResolver(String fnName, CommandExecutorGateway executorGateway) {
		super(fnName, executorGateway);
	}

	@Override
	public String execute(Param<?> param, String[] args) {
		if (ArrayUtils.isEmpty(args) || StringUtils.isEmpty(args[0])) {
			throw new NullPointerException();
		}

		String sPathArr = args[0];
		if (sPathArr.startsWith(Constants.SEPARATOR_URI.code)) {
			sPathArr = sPathArr.substring(1);
		}
		String[] pathArr = sPathArr.split(Constants.SEPARATOR_URI.code);
		return find(param, pathArr, false);
	}

	private String find(Param<?> param, String[] pathArr, boolean found) {
		final String currTopParamPathSegment = pathArr[0];
		Param<?> currTopParam = param.findParamByPath(currTopParamPathSegment);

		if (null == currTopParam) {
			// if we're already processing a found path, and no param was
			// found... exit
			if (found) {
				return null;
			}

			// search children
			List<Param<?>> nestedParams;
			if (param.isNested() && null != (nestedParams = param.findIfNested().getParams())) {
				for (Param<?> nestedParam : nestedParams) {
					String childResult = find(nestedParam, pathArr, false);
					if (null != childResult) {
						return childResult;
					}
				}
			}

			return null;
		}

		// matched leaf node, return
		if (pathArr.length == 1) {
			// remove core domain from path
			int startPos = currTopParam.getRootDomain().getPath().length();
			return currTopParam.getPath().substring(startPos);
		}

		// nested find
		String[] nextPathArr = Arrays.copyOfRange(pathArr, 1, pathArr.length);
		return find(currTopParam, nextPathArr, true);
	}

	@Override
	public String resolve(Param<?> param, String pathToResolve) {
		String result = super.resolve(param, pathToResolve);
		if (null == result) {
			throw new FrameworkRuntimeException("Unable to find \"" + pathToResolve + "\" in " + param);
		}
		return result;
	}
}
