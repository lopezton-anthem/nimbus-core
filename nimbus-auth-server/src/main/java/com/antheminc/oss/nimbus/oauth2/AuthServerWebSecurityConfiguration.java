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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 
 * @author Jayant Chaudhuri
 *
 */
@EnableWebSecurity
@Configuration
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class AuthServerWebSecurityConfiguration extends WebSecurityConfigurerAdapter{
	
	@Autowired
	@Qualifier("authenticationProviderList")
	private List<AuthenticationProvider> authenticationProviderList;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/oauth/authorize", "/oauth/confirm_access", "/oauth/token","/login").permitAll().and().authorizeRequests()
        .anyRequest().authenticated()
        .and()
        .formLogin()
        .permitAll()
        .and()
        .logout()                                    
        .permitAll();
		http.csrf().disable();
	}
	
	
	@Override
    public void configure(AuthenticationManagerBuilder authenticationMgr) throws Exception {
		for(AuthenticationProvider ap: authenticationProviderList) {
			authenticationMgr = authenticationMgr.authenticationProvider(ap);
		}
    }
	
}
