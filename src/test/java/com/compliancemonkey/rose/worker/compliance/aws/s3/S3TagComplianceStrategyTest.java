package com.compliancemonkey.rose.worker.compliance.aws.s3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.compliancemonkey.rose.audit.models.Audit.CloudService;
import com.compliancemonkey.rose.audit.models.ComplianceReport;
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
import software.amazon.awssdk.services.s3.model.GetBucketTaggingRequest;
import software.amazon.awssdk.services.s3.model.GetBucketTaggingResponse;
import software.amazon.awssdk.services.s3.model.Tag;

@RunWith(MockitoJUnitRunner.class)
public class S3TagComplianceStrategyTest {

	private static final String BUCKET_NAME = "testBucketName";

	private static final String COMPLIANT_TAG = "Team";
	private static final String NON_COMPLIANT_TAG = "Owner";

	@Mock
	private S3Client s3Client;

	@InjectMocks
	private S3TagComplianceStrategy s3TagComplianceStrategy;

	@Captor
	private ArgumentCaptor<GetBucketTaggingRequest> getBucketTaggingRequestArgumentCaptor;

	private ComplianceReport complianceReport;

	@Before
	public void setUp() {
		complianceReport = new ComplianceReport(BUCKET_NAME);
	}

	@Test
	public void testS3ServiceSupport() {
		assertTrue(s3TagComplianceStrategy.supportsService(CloudService.AWS_S3));
	}

	@Test
	public void testComplianceIfTeamTagOnBucket() {
		final Tag tag = Tag.builder().key(COMPLIANT_TAG).build();
		final GetBucketTaggingResponse bucketTaggingResponse = GetBucketTaggingResponse.builder().tagSet(tag).build();
		Mockito.lenient().when(s3Client.getBucketTagging(getBucketTaggingRequestArgumentCaptor.capture())).thenReturn(bucketTaggingResponse);

		s3TagComplianceStrategy.execute(s3Client, BUCKET_NAME, complianceReport);
		assertTrue(complianceReport.isCompliant());

		final GetBucketTaggingRequest bucketTaggingRequest = getBucketTaggingRequestArgumentCaptor.getValue();
		assertEquals(bucketTaggingRequest.bucket(), BUCKET_NAME);
	}

	@Test
	public void testOutOfComplianceIfTeamMissingOnBucket() {
		final Tag tag = Tag.builder().key(NON_COMPLIANT_TAG).build();
		final GetBucketTaggingResponse bucketTaggingResponse = GetBucketTaggingResponse.builder().tagSet(tag).build();
		Mockito.lenient().when(s3Client.getBucketTagging(getBucketTaggingRequestArgumentCaptor.capture())).thenReturn(bucketTaggingResponse);

		s3TagComplianceStrategy.execute(s3Client, BUCKET_NAME, complianceReport);
		assertFalse(complianceReport.isCompliant());

		final GetBucketTaggingRequest bucketTaggingRequest = getBucketTaggingRequestArgumentCaptor.getValue();
		assertEquals(bucketTaggingRequest.bucket(), BUCKET_NAME);
	}
}
