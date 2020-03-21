package com.compliancemonkey.rose.account;

import com.compliancemonkey.rose.account.models.AwsCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AccountRepository {

	@Value("${AWS_ACCESS_KEY_ID}")
	private String accessKeyId;

	@Value("${AWS_SECRET_ACCESS_KEY}")
	private String secretAccessKey;

	@Value("${AWS_SESSION_TOKEN}")
	private String sessionToken;

	public AwsCredentials getAwsCredentials(int accountId) {
		return new AwsCredentials(accessKeyId, secretAccessKey, sessionToken);
	}
}
