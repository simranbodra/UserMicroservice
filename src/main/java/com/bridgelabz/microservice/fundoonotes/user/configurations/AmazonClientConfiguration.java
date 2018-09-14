package com.bridgelabz.microservice.fundoonotes.user.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;

@Configuration
public class AmazonClientConfiguration {

	@Value("${s3AccessKey}")
	String accessKeyID;

	@Value("${s3SecretAccessKey}")
	String secretAccessKey;

	@Bean
	public AWSCredentials getAmazonClient() {
		AWSCredentials awsCredentials = null;
		try {
			awsCredentials = new AWSCredentials() {
				@Override
				public String getAWSSecretKey() {
					return secretAccessKey;
				}

				@Override
				public String getAWSAccessKeyId() {
					return accessKeyID;
				}
			};
		} catch (AmazonClientException exception) {
			throw new AmazonClientException("cannot load aws credentials, please check credentials detils", exception);
		}
		return awsCredentials;
	}
}
