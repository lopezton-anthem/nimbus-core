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

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.antheminc.oss.nimbus.app.extension.config.DefaultClientUserDetails;
import com.antheminc.oss.nimbus.context.BeanResolverStrategy;
import com.antheminc.oss.nimbus.domain.cmd.Command;
import com.antheminc.oss.nimbus.domain.cmd.CommandBuilder;
import com.antheminc.oss.nimbus.domain.cmd.CommandMessage;
import com.antheminc.oss.nimbus.domain.cmd.exec.CommandExecution.MultiOutput;
import com.antheminc.oss.nimbus.domain.cmd.exec.CommandExecutorGateway;
import com.antheminc.oss.nimbus.entity.client.user.ClientUser;

/**
 * @author Jayant Chaudhuri
 *
 */
public class DefaultUserDetailsService implements UserDetailsService {
	
	private CommandExecutorGateway commandExecutorGateway;
	
	public DefaultUserDetailsService(BeanResolverStrategy beanResolver) {
		this.commandExecutorGateway = beanResolver.get(CommandExecutorGateway.class);
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		StringBuilder clientSearchURL = new StringBuilder();
		clientSearchURL.append("/anthem/gbd/p/clientuser/_search?fn=query&where=clientuser.loginId.eq('")
					  .append(username).append("')");
		Command cmd = CommandBuilder.withUri(clientSearchURL.toString()).getCommand();
		CommandMessage cmdMsg = new CommandMessage();
		cmdMsg.setCommand(cmd);
		MultiOutput multiOp = commandExecutorGateway.execute(cmdMsg);
		List<ClientUser> clientUserDetails = (List<ClientUser>)multiOp.getSingleResult();
		if(clientUserDetails == null || clientUserDetails.size() == 0)
			throw new UsernameNotFoundException("No users configured with the User ID:"+username);
		if(clientUserDetails.size() > 1)
			throw new UsernameNotFoundException("Multiple users configured with the User ID:"+username);
		return new DefaultClientUserDetails(clientUserDetails.get(0));		
	}

}
