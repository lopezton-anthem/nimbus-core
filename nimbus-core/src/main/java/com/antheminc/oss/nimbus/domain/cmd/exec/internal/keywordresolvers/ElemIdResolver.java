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

import com.antheminc.oss.nimbus.domain.model.state.EntityState.Param;

/**
 * 
 * @author Soham Chakravarti
 * @author Tony Lopez
 *
 */
public class ElemIdResolver extends KeywordResolver {
	
	public ElemIdResolver(String keyword) {
		super(keyword);
	}

	@Override
	public String resolve(Param<?> param, String pathToResolve) {
		return mapColElem(param, pathToResolve);
	}
	
	protected String mapColElem(Param<?> param, String pathToResolve) {
		// check if command param is colElem
		if(param.isCollectionElem())
			return param.findIfCollectionElem().getElemId();
		
		// otherwise, if mapped, check if mapsTo param is colElem
		if(param.isMapped())
			return mapColElem(param.findIfMapped().getMapsTo(), pathToResolve);
		
		// throw ex ..or.. blank??
		return "";
	}
}
