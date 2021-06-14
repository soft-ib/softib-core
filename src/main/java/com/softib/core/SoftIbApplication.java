package com.softib.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.softib.core.entities.User;
import com.softib.core.entities.codes.Role;
import com.softib.core.services.UserServiceImpl;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class SoftIbApplication {

	public static void main(String[] args) {
		SpringApplication.run(SoftIbApplication.class, args);
	}
	
	 @Bean
	 ApplicationRunner init(UserServiceImpl userService) {
	        return args -> {
	            Stream.of(Role.values()).forEach(role -> {
	                User user = new User();
	                user.setEmail(role.toString().toLowerCase()+"@gmail.com");
	                user.setPassword(role.toString().toLowerCase());
	                user.setRole(role);
	                userService.registerNewUserAccount(user);
	            });
	           
	        };
	    }
	
	@Bean
	public Docket swaggerConfiguration() {
		return new Docket(DocumentationType.SWAGGER_2)
			      .apiInfo(apiInfo())
			      .securityContexts(Arrays.asList(securityContext()))
			      .securitySchemes(Arrays.asList(apiKey()))
				.select()
				.paths(PathSelectors.ant("/**"))
				.apis(RequestHandlerSelectors.basePackage("com.softib.core"))
			
				.build();
	}
	
	private ApiKey apiKey() { 
	    return new ApiKey("JWT", "Authorization", "header"); 
	}
	private SecurityContext securityContext() { 
	    return SecurityContext.builder().securityReferences(defaultAuth()).build(); 
	} 

	private List<SecurityReference> defaultAuth() { 
	    AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything"); 
	    AuthorizationScope[] authorizationScopes = new AuthorizationScope[1]; 
	    authorizationScopes[0] = authorizationScope; 
	    return Arrays.asList(new SecurityReference("JWT", authorizationScopes)); 
	}
	private ApiInfo apiInfo() {
	    return new ApiInfo(
	      "REST API",
	      "Soft IB Core API.",
	      "1.0",
	      "Terms of service",
	      new Contact("Dhia saadlaui", "https://github.com/dhiasaadlaui/softib", "dhiasaadlaui@gmail.com"),
	      "License of API",
	      "API license URL",
	      Collections.emptyList());
	}
}