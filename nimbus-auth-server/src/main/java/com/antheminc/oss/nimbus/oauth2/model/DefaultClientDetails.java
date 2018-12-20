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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

import com.antheminc.oss.nimbus.domain.defn.Domain;
import com.antheminc.oss.nimbus.domain.defn.Domain.ListenerType;
import com.antheminc.oss.nimbus.domain.defn.Repo;
import com.antheminc.oss.nimbus.domain.defn.Repo.Database;
import com.antheminc.oss.nimbus.entity.AbstractEntity.IdLong;

import lombok.Getter;
import lombok.Setter;


@Domain(value="oauth_client_details", includeListeners={ListenerType.persistence})
@Repo(Database.rep_mongodb)
@Getter @Setter
public class DefaultClientDetails extends IdLong implements ClientDetails{
	private static final long serialVersionUID = 1L;
	private String clientId;
	private Set<String> resourceIds;
	private boolean secretRequired;
	private String clientSecret;
	private boolean scoped;
	private Set<String> scope;
	private Set<String> authorizedGrantTypes;
	private Set<String> registeredRedirectUri;
	private List<GrantedAuthority> authorities;
	private Integer accessTokenValiditySeconds;
	private Integer refreshTokenValiditySeconds;
	private Map<String, Object> additionalInformation;	

	@Override
	public boolean isAutoApprove(String scope) {
		return false;
	}
}
