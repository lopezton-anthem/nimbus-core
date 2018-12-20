/**
 *  Copyright 2016-2018 the original author or authors.
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
package com.antheminc.oss.nimbus.oauth2.service;

import java.util.List;

import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;

import com.antheminc.oss.nimbus.context.BeanResolverStrategy;
import com.antheminc.oss.nimbus.domain.cmd.Command;
import com.antheminc.oss.nimbus.domain.cmd.CommandBuilder;
import com.antheminc.oss.nimbus.domain.cmd.CommandMessage;
import com.antheminc.oss.nimbus.domain.cmd.exec.CommandExecution.MultiOutput;
import com.antheminc.oss.nimbus.domain.cmd.exec.CommandExecutorGateway;

/**
 * @author Jayant Chaudhuri
 *
 */
public class DefaultClientDetailsService implements ClientDetailsService {
	
	private CommandExecutorGateway commandExecutorGateway;
	
	public DefaultClientDetailsService(BeanResolverStrategy beanResolver) {
		this.commandExecutorGateway = beanResolver.get(CommandExecutorGateway.class);
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.oauth2.provider.ClientDetailsService#loadClientByClientId(java.lang.String)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
		StringBuilder clientSearchURL = new StringBuilder();
		clientSearchURL.append("/anthem/gbd/p/oauth_client_details/_search?fn=query&where=oauth_client_details.clientId.eq('")
					  .append(clientId).append("')");
		Command cmd = CommandBuilder.withUri(clientSearchURL.toString()).getCommand();
		CommandMessage cmdMsg = new CommandMessage();
		cmdMsg.setCommand(cmd);
		MultiOutput multiOp = commandExecutorGateway.execute(cmdMsg);
		List<ClientDetails> clientDetails = (List<ClientDetails>)multiOp.getSingleResult();
		if(clientDetails == null || clientDetails.size() == 0)
			throw new ClientRegistrationException("No clients configured with the Client ID:"+clientId);
		if(clientDetails.size() > 1)
			throw new ClientRegistrationException("Multiple clients configured with the Client ID:"+clientId);
		return clientDetails.get(0);
	}

}
