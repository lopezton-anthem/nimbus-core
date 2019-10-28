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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.antheminc.oss.nimbus.FrameworkRuntimeException;
import com.antheminc.oss.nimbus.InvalidConfigException;
import com.antheminc.oss.nimbus.domain.cmd.Command;
import com.antheminc.oss.nimbus.domain.cmd.CommandBuilder;
import com.antheminc.oss.nimbus.domain.cmd.exec.CommandPathVariableResolver;
import com.antheminc.oss.nimbus.domain.cmd.exec.ExecutionContextLoader;
import com.antheminc.oss.nimbus.domain.model.state.EntityState.Param;
import com.antheminc.oss.nimbus.domain.model.state.QuadModel;
import com.antheminc.oss.nimbus.entity.AbstractEntity.IdLong;
import com.antheminc.oss.nimbus.test.FrameworkIntegrationTestScenariosApplication;

/**
 * @author Tony Lopez
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FrameworkIntegrationTestScenariosApplication.class)
@ActiveProfiles("test")
public class FindFunctionResolverTest {

	private Command rootCmd;
	private QuadModel<?, ? extends IdLong> quadModel;

	@Autowired
	private ExecutionContextLoader executionContextLoader;
	
	@Autowired
	private CommandPathVariableResolver resolver;
	
	@Autowired
	private MongoTemplate mt;

	@After
	public void after() {
		mt.getDb().drop();
	}
	
	@SuppressWarnings("unchecked")
	@Before
	public void before() {
		rootCmd = CommandBuilder.withUri("/hooli/thebox/p/sample_traversal/_new").getCommand();
		quadModel = (QuadModel<?, ? extends IdLong>) executionContextLoader.load(rootCmd).getQuadModel();
		assertNotNull(quadModel);
	}

	@Test
	public void testFindFirst() {
		Param<?> param = quadModel.getCore().getAssociatedParam();
		String actual = resolver.resolve(param, "<!find(/duplicateVariable)!>");
		assertThat(actual).isEqualTo("/duplicateVariable");
	}
	
	@Test
	public void testFindInNestedModel() {
		Param<?> param = quadModel.getCore().getAssociatedParam();
		String actual = resolver.resolve(param, "<!find(/a1)!>");
		assertThat(actual).isEqualTo("/p2/a1");
	}

	@Test
	public void testFindLeaf() {
		Param<?> param = quadModel.getCore().getAssociatedParam();
		String actual = resolver.resolve(param, "<!find(/p1)!>");
		assertThat(actual).isEqualTo("/p1");
	}

	@Test(expected = FrameworkRuntimeException.class)
	public void testFindNotFound() {
		Param<?> param = quadModel.getCore().getAssociatedParam();
		resolver.resolve(param, "<!find(/undeclaredVariable)!>");
	}

	@Test
	public void testFindWithNestedPath() {
		Param<?> param = quadModel.getCore().getAssociatedParam();
		String actual = resolver.resolve(param, "<!find(/a2/b1)!>");
		assertThat(actual).isEqualTo("/p3/a2/b1");
	}
	
	@Test
	public void testFindFromRelativeParam() {
		Param<?> param = quadModel.getCore().getAssociatedParam().findParamByPath("/p2");
		String actual = resolver.resolve(param, "<!find(/c1)!>");
		assertThat(actual).isEqualTo("/p2/a2/c1");
	}
	
	@Test(expected = InvalidConfigException.class)
	public void testFindWithNoArgument() {
		Param<?> param = quadModel.getCore().getAssociatedParam();
		resolver.resolve(param, "<!find()!>");
	}
}
