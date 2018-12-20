/**
 * 
 */
package com.test.nimbus.authserver.config;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * @author Jayant Chaudhuri
 *
 */
public class TestAuthenticationProvider implements AuthenticationProvider {

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		return new UsernamePasswordAuthenticationToken(authentication.getPrincipal(),authentication.getCredentials(),authentication.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return true;
	}

}
