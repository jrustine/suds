package net.curmudgeon.suds.spring;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
//@EnableDynamoDBRepositories(basePackages = "net.curmudgeon.suds.repository")
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
}