package com.compliancemonkey.rose.worker.compliance.aws.s3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.compliancemonkey.rose.audit.models.Audit.CloudService;
import com.compliancemonkey.rose.audit.models.ComplianceReport;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetBucketEncryptionRequest;
import software.amazon.awssdk.services.s3.model.GetBucketEncryptionResponse;
import software.amazon.awssdk.services.s3.model.ServerSideEncryptionConfiguration;
import software.amazon.awssdk.services.s3.model.ServerSideEncryptionRule;

@RunWith(MockitoJUnitRunner.class)
public class S3EncryptionComplianceStrategyTest {

	private static final String BUCKET_NAME = "testBucketName";

	@Mock
	private S3Client s3Client;

	@InjectMocks
	private S3EncryptionComplianceStrategy s3EncryptionComplianceStrategy;

	@Captor
	private ArgumentCaptor<GetBucketEncryptionRequest> getBucketEncryptionRequestArgumentCaptor;

	private ComplianceReport complianceReport;

	@Before
	public void setUp() {
		complianceReport = new ComplianceReport(BUCKET_NAME);
	}

	@Test
	public void testS3ServiceSupport() {
		assertTrue(s3EncryptionComplianceStrategy.supportsService(CloudService.AWS_S3));
	}

	@Test
	public void testComplianceIfBucketIsEncrypted() {
		final ServerSideEncryptionRule serverSideEncryptionRule = ServerSideEncryptionRule.builder().build();
		final ServerSideEncryptionConfiguration encryptionConfiguration = ServerSideEncryptionConfiguration.builder()
				.rules(Collections.singletonList(serverSideEncryptionRule))
				.build();

		final GetBucketEncryptionResponse encryptionResponse = GetBucketEncryptionResponse.builder()
				.serverSideEncryptionConfiguration(encryptionConfiguration)
				.build();

		Mockito.lenient().when(s3Client.getBucketEncryption(getBucketEncryptionRequestArgumentCaptor.capture())).thenReturn(encryptionResponse);
		s3EncryptionComplianceStrategy.execute(s3Client, BUCKET_NAME, null, complianceReport);

		assertTrue(complianceReport.isCompliant());

		final GetBucketEncryptionRequest encryptionRequest = getBucketEncryptionRequestArgumentCaptor.getValue();
		assertEquals(encryptionRequest.bucket(), BUCKET_NAME);
	}

	@Test
	public void testComplianceIfBucketIsNotEncrypted() {
		final ServerSideEncryptionConfiguration encryptionConfiguration = ServerSideEncryptionConfiguration.builder()
				.build();

		final GetBucketEncryptionResponse encryptionResponse = GetBucketEncryptionResponse.builder()
				.serverSideEncryptionConfiguration(encryptionConfiguration)
				.build();

		Mockito.lenient().when(s3Client.getBucketEncryption(getBucketEncryptionRequestArgumentCaptor.capture())).thenReturn(encryptionResponse);
		s3EncryptionComplianceStrategy.execute(s3Client, BUCKET_NAME, null, complianceReport);

		assertFalse(complianceReport.isCompliant());

		final GetBucketEncryptionRequest encryptionRequest = getBucketEncryptionRequestArgumentCaptor.getValue();
		assertEquals(encryptionRequest.bucket(), BUCKET_NAME);
	}
}
