package net.curmudgeon.suds.spring;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.curmudgeon.suds.repository.CustomerRepository;
import net.curmudgeon.suds.repository.CustomerRepositoryImpl;
import net.curmudgeon.suds.repository.GroomerRepository;
import net.curmudgeon.suds.repository.GroomerRepositoryImpl;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/*
 * Copyright (C) 2022 Jay Rustine
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
@Configuration
public class DynamoDbConfig {

	@Value("${amazon.dynamodb.endpoint}")
	private String amazonDynamoDBEndpoint;

	@Value("${amazon.aws.accesskey}")
	private String amazonAWSAccessKey;

	@Value("${amazon.aws.secretkey}")
	private String amazonAWSSecretKey;
	
	@Bean
	public DynamoDbEnhancedClient dynamoDbEnhancedClient() {
		DynamoDbEnhancedClient dynamoDbEnhancedClient = 
			    DynamoDbEnhancedClient.builder()
			                          .dynamoDbClient(dynamoDbClient())
			                          .build();
		
		return dynamoDbEnhancedClient;
	}
	
	
	@Bean
	public DynamoDbClient dynamoDbClient() {
		DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
				.endpointOverride(URI.create(amazonDynamoDBEndpoint))
				.region(Region.US_EAST_1)
				.credentialsProvider(StaticCredentialsProvider.create(amazonAWSCredentials()))
				.build();
		
		return dynamoDbClient;
	}

	@Bean
	public AwsBasicCredentials amazonAWSCredentials() {
		return AwsBasicCredentials.create(amazonAWSAccessKey, amazonAWSSecretKey);
	}
	
	@Bean
	public CustomerRepository customerRepository() {
		CustomerRepository customerRepository = new CustomerRepositoryImpl(dynamoDbEnhancedClient());
		return customerRepository;
	}
	
	@Bean
	public GroomerRepository groomerRepository() {
		GroomerRepository groomerRepository = new GroomerRepositoryImpl(dynamoDbEnhancedClient());
		return groomerRepository;
	}
}