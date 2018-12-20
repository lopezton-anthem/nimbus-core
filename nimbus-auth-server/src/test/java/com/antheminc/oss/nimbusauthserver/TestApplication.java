package com.antheminc.oss.nimbusauthserver;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;

import com.antheminc.oss.nimbus.app.extension.config.DefaultCoreConfiguration;
import com.antheminc.oss.nimbus.context.BeanResolverStrategy;
import com.antheminc.oss.nimbus.domain.session.SessionProvider;
import com.antheminc.oss.nimbus.test.domain.session.TestSessionProvider;

@SpringBootApplication(scanBasePackageClasses = DefaultCoreConfiguration.class)
public class TestApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestApplication.class, args);
	}

	@Bean
	public SessionProvider sessionProvider(BeanResolverStrategy beanResolver) {
		return new TestSessionProvider();
	}
	
	@Bean
	public List<AuthenticationProvider> authenticationProviderList() {
		List<AuthenticationProvider> providers = new ArrayList<AuthenticationProvider>();
		providers.add(testAuthenticationProvider());
		return providers;
	}

	@Bean
	public TestAuthenticationProvider testAuthenticationProvider() {
		return new TestAuthenticationProvider();
	}
	
	

}

