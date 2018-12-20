package com.antheminc.oss.nimbusauthserver;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.antheminc.oss.nimbus.app.extension.config.DefaultCoreConfiguration;
import com.antheminc.oss.nimbus.domain.cmd.exec.CommandExecutorGateway;
import com.antheminc.oss.nimbus.entity.client.user.ClientUser;
import com.antheminc.oss.nimbus.entity.person.Name;
import com.antheminc.oss.nimbus.oauth2.model.DefaultClientDetails;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestApplication.class,DefaultCoreConfiguration.class})
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class NimbusAuthServerApplicationTests {
	
	@Autowired
	CommandExecutorGateway commandExecutorGateway;
	
	@Autowired
	protected MongoOperations mongo;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Before
	public void setup() {
		DefaultClientDetails clientDetails = new DefaultClientDetails();
		clientDetails.setId(1L);
		clientDetails.setClientId("nimbus");
		clientDetails.setClientSecret("nimbus");
		clientDetails.setSecretRequired(true);
		
		Set<String> scope = new HashSet<String>();
		scope.add("read");
		scope.add("write");
		clientDetails.setScope(scope);
		
		Set<String> authorizedGrantTypes = new HashSet<String>();
		authorizedGrantTypes.add("password");
		clientDetails.setAuthorizedGrantTypes(authorizedGrantTypes);
		clientDetails.setAuthorities(new ArrayList<>());
		clientDetails.setAccessTokenValiditySeconds(1000);
		clientDetails.setRefreshTokenValiditySeconds(1000);
		
		mongo.insert(clientDetails,"oauth_client_details");
		
		ClientUser clientUser = new ClientUser();
		clientUser.setId(1L);
		clientUser.setDisplayName("Test User");
		clientUser.setLoginId("testLogin");
		
		Name nm = new Name();
		nm.setFirstName("Test");
		nm.setLastModifiedBy("User");
		clientUser.setName(nm);
		
		mongo.insert(clientUser,"clientuser");
	}

	@Test
	public void test_auth_server() throws Exception{
		MockHttpServletResponse response = 
			mockMvc.perform(
					post("/auth-server/oauth/token")
					.param("grant_type", "password")
					.param("username", "testLogin")
					.param("password", "testLogin")
					.header("Authorization", "Basic bmltYnVzOm5pbWJ1cw==")
			).andReturn().getResponse();
		
		Assert.assertEquals(200, response.getStatus());
	}
}
