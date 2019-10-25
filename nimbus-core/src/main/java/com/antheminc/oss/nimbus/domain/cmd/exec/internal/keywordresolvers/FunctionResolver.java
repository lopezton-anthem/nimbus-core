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

import com.antheminc.oss.nimbus.domain.cmd.exec.CommandExecutorGateway;
import com.antheminc.oss.nimbus.domain.model.state.EntityState.Param;

/**
 * 
 * @author Tony Lopez
 *
 */
public abstract class FunctionResolver extends CrossDomainKeywordResolver {

	public static final String LEFT_DELIMITER = "(";
	public static final String RIGHT_DELIMITER = ")";
	public static final String ARGUMENT_DELIMITER = ",";
	
	public FunctionResolver(String fnName, CommandExecutorGateway executorGateway) {
		super(fnName + LEFT_DELIMITER, executorGateway);
	}

	@Override
	public String resolve(Param<?> param, String pathToResolve) {
		String sArguments = StringUtils.substringBetween(pathToResolve, keyword, RIGHT_DELIMITER);
		return execute(param, sArguments.split(ARGUMENT_DELIMITER));
	}

	public abstract String execute(Param<?> param, String[] args);
}
