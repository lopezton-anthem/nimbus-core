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
package com.antheminc.oss.nimbus.oauth2.model;

import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.antheminc.oss.nimbus.entity.client.user.ClientUser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Jayant Chaudhuri
 *
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class DefaultClientUserDetails implements UserDetails {
	
	private static final long serialVersionUID = 1L;
	private ClientUser clientUser;
	
	/* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetails#getAuthorities()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return CollectionUtils.EMPTY_COLLECTION;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetails#getPassword()
	 */
	@Override
	public String getPassword() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetails#getUsername()
	 */
	@Override
	public String getUsername() {
		return clientUser.getLoginId();
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetails#isAccountNonExpired()
	 */
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetails#isAccountNonLocked()
	 */
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetails#isCredentialsNonExpired()
	 */
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetails#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return true;
	}

}
