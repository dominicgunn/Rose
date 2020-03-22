package com.compliancemonkey.rose.worker.strategies.aws;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Service
public class AwsService {

	public S3Client buildS3Client(StaticCredentialsProvider awsCredentialsProvider) {
		return S3Client.builder()
				.region(Region.US_WEST_1)
				.credentialsProvider(awsCredentialsProvider).build();
	}
}
