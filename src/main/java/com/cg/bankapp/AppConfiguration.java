package com.cg.bankapp;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * This is a configuration class for setting up Swagger 2 with the Bank Server
 * REST API.
 */
@Configuration
@EnableSwagger2
public class AppConfiguration {
	@Value("${basePackage}")
	private String basePackage;

	/**
	 * This method sets up Swagger 2 with the specified API base package.
	 * 
	 * @return a Docket object for configuring Swagger 2
	 */
	@Bean
	public Docket swaggerConfig() {
		return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.basePackage(basePackage))
				.build().apiInfo(getApiMetaData());
	}

	/**
	 * This method returns the API metadata for Swagger 2.
	 * 
	 * @return an ApiInfo object containing the API metadata
	 */
	private ApiInfo getApiMetaData() {
		return new ApiInfo("Bank Server Application", "Bank Server REST API", "1.0",
				"Bank Server Application with simple functionalities like getting the balance of the account, doing routine transactions as withdraw, deposit, fund transfer.",
				new Contact("Basudev Tyagi", "www.capgemini.com", "basudev.tyagi@capgemini.com"), "API License",
				"https://capgemini.com", Collections.emptyList());
	}
}
