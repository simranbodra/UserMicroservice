package com.bridgelabz.microservice.fundoonotes.user.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

@Configuration
public class SQSConfiguration {

	@Autowired
	private AmazonClientConfiguration awsCredentials;

	@Bean
	public AmazonSQS createSQSClient() {

		AmazonSQS sqs = AmazonSQSClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(awsCredentials.getAmazonClient()))
				.withRegion(Regions.US_EAST_2).build();
		return sqs;

	}

	@Bean
	public SQSConnectionFactory sqsConnectionFactory() {

		SQSConnectionFactory sqsConnectionFactory = new SQSConnectionFactory(new ProviderConfiguration(),
				AmazonSQSClientBuilder.standard().withRegion(Regions.US_EAST_2)
						.withCredentials(new AWSCredentialsProvider() {

							@Override
							public void refresh() {
								// TODO Auto-generated method stub
							}

							@Override
							public AWSCredentials getCredentials() {

								return awsCredentials.getAmazonClient();
							}
						}));

		return sqsConnectionFactory;
	}

}
