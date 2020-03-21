package com.compliancemonkey.rose.account;

import com.compliancemonkey.rose.account.models.AwsCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

@Service
public class AccountService {

	private AccountRepository accountRepository;

	@Autowired
	public AccountService(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	public StaticCredentialsProvider getAwsCredentialsProvider(int accountId) {
		final AwsCredentials awsCredentials = accountRepository.getAwsCredentials(accountId);
		if (awsCredentials.getAccessKeyId() == null) {
			return null;
		}

		final AwsSessionCredentials sessionCredentials = AwsSessionCredentials.create(
				awsCredentials.getAccessKeyId(),
				awsCredentials.getSecretAccessKey(),
				awsCredentials.getAwsSessionToken()
		);

		return StaticCredentialsProvider.create(sessionCredentials);
	}
}
