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
import software.amazon.awssdk.services.s3.model.GetBucketAclRequest;
import software.amazon.awssdk.services.s3.model.GetBucketAclResponse;
import software.amazon.awssdk.services.s3.model.Grant;
import software.amazon.awssdk.services.s3.model.Grantee;
import software.amazon.awssdk.services.s3.model.Permission;

@RunWith(MockitoJUnitRunner.class)
public class S3AclComplianceStrategyTest {

	private static final String GROUPS_USERS = "http://acs.amazonaws.com/groups/global/AllUsers";
	private static final String BUCKET_NAME = "testBucketName";

	@Mock
	private S3Client s3Client;

	@InjectMocks
	private S3AclComplianceStrategy s3AclComplianceStrategy;

	@Captor
	private ArgumentCaptor<GetBucketAclRequest> getBucketAclRequestArgumentCaptor;

	private ComplianceReport complianceReport;

	@Before
	public void setUp() {
		complianceReport = new ComplianceReport(BUCKET_NAME);
	}

	@Test
	public void testS3ServiceSupport() {
		assertTrue(s3AclComplianceStrategy.supportsService(CloudService.AWS_S3));
	}

	@Test
	public void testComplianceIfBucketIsNotPubliclyAccessible() {
		final GetBucketAclResponse bucketAclResponse = GetBucketAclResponse.builder().build();

		Mockito.lenient().when(s3Client.getBucketAcl(getBucketAclRequestArgumentCaptor.capture())).thenReturn(bucketAclResponse);
		s3AclComplianceStrategy.execute(s3Client, BUCKET_NAME, complianceReport);

		assertTrue(complianceReport.isCompliant());

		final GetBucketAclRequest bucketAclRequest = getBucketAclRequestArgumentCaptor.getValue();
		assertEquals(bucketAclRequest.bucket(), BUCKET_NAME);
	}

	@Test
	public void testNonComplianceIfBucketIsPubliclyAccessible() {
		final Grantee grantee = Grantee.builder().uri(GROUPS_USERS).build();
		final Grant grant = Grant.builder().grantee(grantee).permission(Permission.READ).build();
		final GetBucketAclResponse bucketAclResponse = GetBucketAclResponse.builder().grants(grant).build();

		Mockito.lenient().when(s3Client.getBucketAcl(getBucketAclRequestArgumentCaptor.capture())).thenReturn(bucketAclResponse);
		s3AclComplianceStrategy.execute(s3Client, BUCKET_NAME, complianceReport);

		assertFalse(complianceReport.isCompliant());

		final GetBucketAclRequest bucketAclRequest = getBucketAclRequestArgumentCaptor.getValue();
		assertEquals(bucketAclRequest.bucket(), BUCKET_NAME);
	}
}
