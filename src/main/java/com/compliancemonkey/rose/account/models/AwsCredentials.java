package com.compliancemonkey.rose.account.models;

public class AwsCredentials {

	private final String accessKeyId;
	private final String secretAccessKey;
	private final String awsSessionToken;

	public AwsCredentials(String accessKeyId, String secretAccessKey, String awsSessionToken) {
		this.accessKeyId = accessKeyId;
		this.secretAccessKey = secretAccessKey;
		this.awsSessionToken = awsSessionToken;
	}

	public String getAccessKeyId() {
		return accessKeyId;
	}

	public String getSecretAccessKey() {
		return secretAccessKey;
	}

	public String getAwsSessionToken() {
		return awsSessionToken;
	}
}
