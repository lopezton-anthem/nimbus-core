package com.test.nimbus.authserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

import com.antheminc.oss.nimbus.oauth2.DefaultOAuth2Config;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableConfigurationProperties
@EnableWebSecurity
@Import(value= {DefaultOAuth2Config.class})
@Configuration
@EnableAuthorizationServer
@EnableResourceServer
public class AuthServerApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(AuthServerApplication.class, args);
	}
}
