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
package com.antheminc.oss.nimbus.oauth2;

import java.security.Principal;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.antheminc.oss.nimbus.context.BeanResolverStrategy;

/**
 * 
 * @author Jayant Chaudhuri
 *
 */
@RestController
public class ResourceServer {
	
	private UserDetailsService userDetailsService;
	
	public ResourceServer(BeanResolverStrategy beanResolver) {
		this.userDetailsService = beanResolver.get(UserDetailsService.class, "userDetailsService");
	}
	
	@RequestMapping(value="${auth-server.userInfoUri}")
	@ResponseBody
	public UserDetails user(Principal principal) {
		OAuth2Authentication auth = (OAuth2Authentication)principal;
		String loginId = (String)auth.getUserAuthentication().getPrincipal();
		return userDetailsService.loadUserByUsername(loginId);
	}
	
}
