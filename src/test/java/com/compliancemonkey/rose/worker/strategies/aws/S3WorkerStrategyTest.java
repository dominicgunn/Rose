package com.compliancemonkey.rose.worker.strategies.aws;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import com.compliancemonkey.rose.account.AccountService;
import com.compliancemonkey.rose.audit.AuditService;
import com.compliancemonkey.rose.audit.events.AuditCompleteEvent;
import com.compliancemonkey.rose.audit.events.AuditUpdateEvent;
import com.compliancemonkey.rose.audit.models.Audit;
import com.compliancemonkey.rose.audit.models.Audit.CloudService;
import com.compliancemonkey.rose.audit.models.Audit.Status;
import com.compliancemonkey.rose.audit.models.AuditReport;
import com.compliancemonkey.rose.worker.strategies.aws.s3.S3WorkerStrategy;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.GetBucketTaggingRequest;
import software.amazon.awssdk.services.s3.model.GetBucketTaggingResponse;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.Tag;

@RunWith(MockitoJUnitRunner.class)
public class S3WorkerStrategyTest {

	private static final int AUDIT_ID = 1;
	private static final int ACCOUNT_ID = 2;

	private static final String BUCKET_NAME = "testBucketName";
	private static final String COMPLIANCE_TAG = "Team";

	private static final CloudService CLOUD_SERVICE = CloudService.AWS_S3;

	@Mock
	private AwsService awsService;

	@Mock
	private AuditService auditService;

	@Mock
	private AccountService accountService;

	@Mock
	private ApplicationEventPublisher eventPublisher;

	@Captor
	private ArgumentCaptor<ApplicationEvent> auditUpdateEventArgumentCaptor;

	@Captor
	private ArgumentCaptor<GetBucketTaggingRequest> getBucketTaggingRequestArgumentCaptor;

	@InjectMocks
	private S3WorkerStrategy s3WorkerStrategy;

	private Tag tag;
	private Bucket bucket;
	private S3Client s3Client;
	private StaticCredentialsProvider credentialsProvider;

	@Before
	public void setUp() throws Exception {
		s3Client = Mockito.mock(S3Client.class);
		tag = Tag.builder().key(COMPLIANCE_TAG).build();
		bucket = Bucket.builder().name(BUCKET_NAME).build();
		credentialsProvider = StaticCredentialsProvider.create(AwsBasicCredentials.create("accessKeyId", "secretAccessKey"));

		Mockito.lenient().when(auditService.getAudit(AUDIT_ID)).thenReturn(new Audit(AUDIT_ID, ACCOUNT_ID, CLOUD_SERVICE));
		Mockito.lenient().when(accountService.getAwsCredentialsProvider(ACCOUNT_ID)).thenReturn(credentialsProvider);
		Mockito.lenient().when(awsService.buildS3Client(credentialsProvider)).thenReturn(s3Client);

		final ListBucketsResponse listBucketsResponse = ListBucketsResponse.builder().buckets(bucket).build();
		Mockito.lenient().when(s3Client.listBuckets()).thenReturn(listBucketsResponse);

		final GetBucketTaggingResponse bucketTaggingResponse = GetBucketTaggingResponse.builder().tagSet(tag).build();
		Mockito.lenient().when(s3Client.getBucketTagging(getBucketTaggingRequestArgumentCaptor.capture())).thenReturn(bucketTaggingResponse);
	}

	@Test
	public void testStrategyFailsIfAuditMissing() {
		Mockito.when(auditService.getAudit(AUDIT_ID)).thenReturn(null);
		s3WorkerStrategy.execute(AUDIT_ID);

		Mockito.verify(eventPublisher, Mockito.atLeastOnce()).publishEvent(auditUpdateEventArgumentCaptor.capture());
		final AuditUpdateEvent auditUpdateEvent = (AuditUpdateEvent) auditUpdateEventArgumentCaptor.getValue();
		Assertions.assertEquals(auditUpdateEvent.getAuditStatus(), Status.FAILED);
	}

	@Test
	public void testStrategyIfAWSCredentialsMissing() {
		Mockito.when(accountService.getAwsCredentialsProvider(ACCOUNT_ID)).thenReturn(null);
		s3WorkerStrategy.execute(AUDIT_ID);

		Mockito.verify(eventPublisher, Mockito.only()).publishEvent(auditUpdateEventArgumentCaptor.capture());
		final AuditUpdateEvent auditUpdateEvent = (AuditUpdateEvent) auditUpdateEventArgumentCaptor.getValue();
		Assertions.assertEquals(auditUpdateEvent.getAuditStatus(), Status.FAILED);
	}

	@Test
	public void testStrategyIdentifiesBucketsNotInCompliance() {
		final String outOfComplianceTagName = String.format("%s-OutOfCompliance", COMPLIANCE_TAG);

		tag = Tag.builder().key(outOfComplianceTagName).build();
		final GetBucketTaggingResponse bucketTaggingResponse = GetBucketTaggingResponse.builder().tagSet(tag).build();
		Mockito.lenient().when(s3Client.getBucketTagging(getBucketTaggingRequestArgumentCaptor.capture())).thenReturn(bucketTaggingResponse);

		s3WorkerStrategy.execute(AUDIT_ID);

		Mockito.verify(eventPublisher, Mockito.times(2)).publishEvent(auditUpdateEventArgumentCaptor.capture());
		final AuditUpdateEvent auditUpdateEvent = (AuditUpdateEvent) auditUpdateEventArgumentCaptor.getAllValues().get(0);
		Assertions.assertEquals(auditUpdateEvent.getAuditStatus(), Status.IN_PROGRESS);

		final AuditCompleteEvent auditCompleteEvent = (AuditCompleteEvent) auditUpdateEventArgumentCaptor.getAllValues().get(1);
		final AuditReport auditReport = auditCompleteEvent.getAuditReport();

		assertThat(auditReport.getObjectIdsOutOfCompliance(), contains(BUCKET_NAME));
	}

	@Test
	public void testStrategyIdentifiesBucketsInCompliance() {
		s3WorkerStrategy.execute(AUDIT_ID);

		Mockito.verify(eventPublisher, Mockito.times(2)).publishEvent(auditUpdateEventArgumentCaptor.capture());
		final AuditUpdateEvent auditUpdateEvent = (AuditUpdateEvent) auditUpdateEventArgumentCaptor.getAllValues().get(0);
		Assertions.assertEquals(auditUpdateEvent.getAuditStatus(), Status.IN_PROGRESS);

		final AuditCompleteEvent auditCompleteEvent = (AuditCompleteEvent) auditUpdateEventArgumentCaptor.getAllValues().get(1);
		final AuditReport auditReport = auditCompleteEvent.getAuditReport();

		assertThat(auditReport.getObjectIdsInCompliance(), contains(BUCKET_NAME));
	}
}
