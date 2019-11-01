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
package com.antheminc.oss.nimbus.domain.model.state.repo;

import java.util.List;

import com.antheminc.oss.nimbus.domain.cmd.Command;
import com.antheminc.oss.nimbus.domain.cmd.CommandBuilder;
import com.antheminc.oss.nimbus.domain.cmd.CommandMessage;
import com.antheminc.oss.nimbus.domain.cmd.exec.CommandExecution.MultiOutput;
import com.antheminc.oss.nimbus.domain.cmd.exec.CommandExecutorGateway;
import com.antheminc.oss.nimbus.domain.model.state.EntityState.Param;
import com.antheminc.oss.nimbus.domain.model.state.repo.db.SearchCriteria.QuerySearchCriteria;
import com.antheminc.oss.nimbus.entity.DomainEntityLock;
import com.antheminc.oss.nimbus.entity.LockEntity;

import lombok.RequiredArgsConstructor;

/**
 * @author Sandeep Mantha
 *
 */
@RequiredArgsConstructor
public class DefaultDomainEntityLockService extends AbstractLockService implements DomainEntityLockService {

	private final ModelRepository modelRepository;

	private final CommandExecutorGateway commandExecutorGateway;

	@Override
	public LockEntity getLock(Param<?> p) {
		final String lockDomainAlias = "lock";
		final String key = p.getRootDomain().getConfig().getAlias() + ":" + p.getRootDomain().getIdParam().getState();
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append(lockDomainAlias).append(".domain.eq(").append(key).append(")");
		QuerySearchCriteria sc = new QuerySearchCriteria();
		sc.setWhere(sc.toString());
		sc.setFetch("1");
		return (LockEntity) modelRepository._search(DomainEntityLock.class, lockDomainAlias, () -> sc);
	}

	@Override
	public DomainEntityLock createLockInternal(Param<?> p) {
		// TODO Auto-generated method stub
		// mongo insert
		return null;
	}

	@Override
	void removeLockInternal(Param<?> p) {
		// TODO Auto-generated method stub
	}

}