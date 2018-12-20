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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import com.antheminc.oss.nimbus.context.BeanResolverStrategy;
import com.antheminc.oss.nimbus.oauth2.service.DefaultClientDetailsService;
import com.antheminc.oss.nimbus.oauth2.service.DefaultUserDetailsService;

/**
 * 
 * @author Jayant Chaudhuri
 *
 */
@Configuration
public class DefaultOAuth2Config {
	
	@Value("${auth-server.jwt.token.signingKey}")
	private String jwtTokenSigningKey;
	
	@Value("${auth-server.jwt.token.verifierKey}")
	private String jwtTokenVerifierKey;
	
	@Bean
	@DependsOn("default.tokenServices")
	public ResourceServerConfiguration resourceServerConfiguration(BeanResolverStrategy beanResolver) throws Exception{
		return new ResourceServerConfiguration(beanResolver);
	}
	
	@Bean
	@DependsOn(value={"default.tokenServices","default.clientDetailsService","authenticationProviderList"})
	public AuthServerConfiguration authServerConfiguration(BeanResolverStrategy beanResolver) throws Exception{
		return new AuthServerConfiguration(beanResolver);
	}
	
	@Bean
	@DependsOn(value={"default.tokenServices","default.clientDetailsService","authenticationProviderList"})
	public AuthServerWebSecurityConfiguration authServerWebSecurityConfiguration(BeanResolverStrategy beanResolver) throws Exception{
		return new AuthServerWebSecurityConfiguration();
	}
	
	
	
	@Bean("default.tokenServices")
	@Primary
	public DefaultTokenServices defaultTokenServices() throws Exception{
		JwtAccessTokenConverter tokenConverter = new JwtAccessTokenConverter();
		tokenConverter.setSigningKey(jwtTokenSigningKey);
		tokenConverter.setVerifierKey(jwtTokenVerifierKey);
		tokenConverter.afterPropertiesSet();
		DefaultTokenServices tokenServices = new DefaultTokenServices();
		tokenServices.setTokenEnhancer(tokenConverter);
		tokenServices.setTokenStore(new JwtTokenStore(tokenConverter));
		tokenServices.setSupportRefreshToken(true);
		return tokenServices;
	}
	
	@Bean("default.clientDetailsService")
	public ClientDetailsService defaultClientDetailsService(BeanResolverStrategy beanResolver) {
		return new DefaultClientDetailsService(beanResolver);
	}
	
	@Bean("default.userDetailsService")
	public DefaultUserDetailsService defaultUserDetailsService(BeanResolverStrategy beanResolver) {
		return new DefaultUserDetailsService(beanResolver);
	}
	
	@Bean
	@DependsOn("default.userDetailsService")
	public ResourceServer resourceServer(BeanResolverStrategy beanResolver) {
		return new ResourceServer(beanResolver);
	}
	
}
