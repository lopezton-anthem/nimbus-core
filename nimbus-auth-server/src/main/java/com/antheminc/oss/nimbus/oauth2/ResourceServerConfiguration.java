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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

import com.antheminc.oss.nimbus.context.BeanResolverStrategy;

/**
 * This class provides the resource server configuration details
 * @author Jayant Chaudhuri
 *
 */

@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
	private ResourceServerTokenServices resourceServerTokenServices;
	
	@Value("${auth-server.userInfoUri}")
	private String userInfoUri;	
	
	
	public ResourceServerConfiguration(BeanResolverStrategy beanResolver) {
		this.resourceServerTokenServices = beanResolver.get(ResourceServerTokenServices.class, "tokenServices");
	}


	
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.tokenServices(resourceServerTokenServices);
	}
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.antMatcher(userInfoUri).authorizeRequests().anyRequest().authenticated();
	}
	
}
