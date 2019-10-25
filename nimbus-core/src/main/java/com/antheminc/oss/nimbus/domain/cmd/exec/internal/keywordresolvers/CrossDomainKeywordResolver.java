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
import java.util.LinkedList;

import com.antheminc.oss.nimbus.domain.cmd.Action;
import com.antheminc.oss.nimbus.domain.cmd.Behavior;
import com.antheminc.oss.nimbus.domain.cmd.Command;
import com.antheminc.oss.nimbus.domain.cmd.CommandBuilder;
import com.antheminc.oss.nimbus.domain.cmd.CommandElement.Type;
import com.antheminc.oss.nimbus.domain.cmd.CommandMessage;
import com.antheminc.oss.nimbus.domain.cmd.exec.CommandExecution.MultiOutput;
import com.antheminc.oss.nimbus.domain.cmd.exec.CommandExecutorGateway;
import com.antheminc.oss.nimbus.domain.model.state.EntityState.Param;
import com.antheminc.oss.nimbus.support.JustLogit;

/**
 * 
 * @author Soham Chakravarti
 * @author Tony Lopez
 *
 */
public abstract class CrossDomainKeywordResolver extends KeywordResolver {

	protected static final JustLogit logit = new JustLogit(CrossDomainKeywordResolver.class);
	
	protected final CommandExecutorGateway executorGateway;
	
	public CrossDomainKeywordResolver(String keyword, CommandExecutorGateway executorGateway) {
		super(keyword);
		this.executorGateway = executorGateway;
	}

	protected Object mapCrossDomain(Param<?> commandParam, String pathToResolve) {
		Command rootCmd = commandParam.getRootExecution().getRootCommand();
		CommandBuilder cmdBuilder = CommandBuilder.withPlatformRelativePath(rootCmd, Type.AppAlias, pathToResolve);
		cmdBuilder.setAction(Action._get);
		cmdBuilder.setBehaviors(new LinkedList<>(Arrays.asList(Behavior.$state)));
		Command cmd = cmdBuilder.getCommand();
		CommandMessage cmdMsg = new CommandMessage(cmd, null);		
		MultiOutput output = executorGateway.execute(cmdMsg);
		Object response = output.getSingleResult();
		
		if (response == null) {
			logit.error(() -> new StringBuffer().append(" Param (using paramPath) [").append(pathToResolve).append("] not found from param: ").append(commandParam).toString());
			return null;
		}
		
		logit.debug(() -> new StringBuffer().append(" Param (using paramPath) [").append(pathToResolve).append("] has been resolved to ").append(response).toString());
		return response;
	}
}
