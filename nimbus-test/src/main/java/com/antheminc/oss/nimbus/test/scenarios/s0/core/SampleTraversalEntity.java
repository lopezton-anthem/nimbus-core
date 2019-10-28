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
package com.antheminc.oss.nimbus.test.scenarios.s0.core;

import com.antheminc.oss.nimbus.domain.defn.Domain;
import com.antheminc.oss.nimbus.domain.defn.Domain.ListenerType;
import com.antheminc.oss.nimbus.domain.defn.Model;
import com.antheminc.oss.nimbus.domain.defn.Repo;
import com.antheminc.oss.nimbus.domain.defn.Repo.Database;
import com.antheminc.oss.nimbus.entity.AbstractEntity.IdLong;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>This entity is intended for testing traversal related methods.
 * 
 * @author Tony Lopez
 *
 */
@Domain(value = "sample_traversal", includeListeners = { ListenerType.persistence })
@Repo(Database.rep_mongodb)
@Getter
@Setter
public class SampleTraversalEntity extends IdLong {

	private static final long serialVersionUID = 1L;

	private String p1;

	private String duplicateVariable;
	
	private InnerA2 p2;

	private InnerA p3;

	@Model
	@Getter
	@Setter
	public static class InnerA {

		private String a1;

		private String duplicateVariable;
		
		private InnerB a2;
	}
	
	@Model
	@Getter
	@Setter
	public static class InnerA2 {

		private String a1;
		
		private InnerC a2;
	}
	
	@Model
	@Getter
	@Setter
	public static class InnerB {

		private String b1;
	}
	
	@Model
	@Getter
	@Setter
	public static class InnerC {

		private String c1;
	}
}
