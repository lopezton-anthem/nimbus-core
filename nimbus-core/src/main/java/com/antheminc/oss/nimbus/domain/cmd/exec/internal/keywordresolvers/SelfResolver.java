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

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.antheminc.oss.nimbus.domain.cmd.CommandElement.Type;
import com.antheminc.oss.nimbus.domain.model.state.EntityState.Param;
import com.antheminc.oss.nimbus.domain.session.SessionProvider;
import com.antheminc.oss.nimbus.entity.client.user.ClientUser;

/**
 * 
 * @author Soham Chakravarti
 * @author Tony Lopez
 *
 */
public class SelfResolver extends KeywordResolver {

	private final SessionProvider sessionProvider;
	
	public SelfResolver(String keyword, SessionProvider sessionProvider) {
		super(keyword);
		this.sessionProvider = sessionProvider;
	}
	
	@Override
	public String resolve(Param<?> param, String pathToResolve) {
		if(StringUtils.endsWith(pathToResolve, "loginId"))
			return Optional.ofNullable(sessionProvider.getLoggedInUser()).orElseGet(() -> new ClientUser()).getLoginId();
		if(StringUtils.endsWith(pathToResolve, "id")) {
			Long id = Optional.ofNullable(sessionProvider.getLoggedInUser()).orElseGet(() -> new ClientUser()).getId();
			return String.valueOf(id);
		}
		
		return param.getRootExecution().getRootCommand().getElementSafely(Type.ClientAlias).getAlias();
	}
	
}
