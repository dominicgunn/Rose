package com.compliancemonkey.rose.worker.aws.s3;

import com.compliancemonkey.rose.account.AccountService;
import com.compliancemonkey.rose.audit.AuditService;
import com.compliancemonkey.rose.audit.events.AuditCompleteEvent;
import com.compliancemonkey.rose.audit.events.AuditUpdateEvent;
import com.compliancemonkey.rose.audit.models.Audit;
import com.compliancemonkey.rose.audit.models.Audit.CloudService;
import com.compliancemonkey.rose.audit.models.Audit.Status;
import com.compliancemonkey.rose.worker.ServiceWorkerStrategy;
import com.compliancemonkey.rose.worker.aws.AwsService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetBucketTaggingRequest;
import software.amazon.awssdk.services.s3.model.GetBucketTaggingResponse;
import software.amazon.awssdk.services.s3.model.ListBucketsRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.Tag;

@Component
public class S3WorkerStrategy implements ServiceWorkerStrategy {

	private AwsService awsService;
	private AuditService auditService;
	private AccountService accountService;
	private ApplicationEventPublisher eventPublisher;

	@Autowired
	public S3WorkerStrategy(AwsService awsService, AuditService auditService, AccountService accountService,
							ApplicationEventPublisher eventPublisher) {
		this.awsService = awsService;
		this.auditService = auditService;
		this.accountService = accountService;
		this.eventPublisher = eventPublisher;
	}

	@Override
	public void execute(int auditId) {
		final Audit audit = auditService.getAudit(auditId);
		if (audit == null) {
			eventPublisher.publishEvent(new AuditUpdateEvent(auditId, Status.FAILED));
			return;
		}

		final StaticCredentialsProvider awsCredentialsProvider = accountService.getAwsCredentialsProvider(audit.getAccountId());
		if (awsCredentialsProvider == null) {
			eventPublisher.publishEvent(new AuditUpdateEvent(auditId, Status.FAILED));
			return;
		}

		eventPublisher.publishEvent(new AuditUpdateEvent(auditId, Status.IN_PROGRESS));

		final S3Client s3Client = awsService.buildS3Client(awsCredentialsProvider);

		final List<String> bucketsInCompliance = new ArrayList<>();
		final List<String> bucketsOutOfCompliance = new ArrayList<>();

		try {
			final ListBucketsResponse listBucketsResponse = s3Client.listBuckets();
			listBucketsResponse.buckets().forEach(x -> {
				final GetBucketTaggingRequest bucketTaggingRequest = GetBucketTaggingRequest.builder().bucket(x.name()).build();
				try {
					final GetBucketTaggingResponse bucketTaggingResponse = s3Client.getBucketTagging(bucketTaggingRequest);

					boolean inCompliance = false;
					for (Tag tag : bucketTaggingResponse.tagSet()) {
						if ("Team".equalsIgnoreCase(tag.key())) {
							inCompliance = true;
							break;
						}
					}

					if (inCompliance) {
						bucketsInCompliance.add(x.name());
					} else {
						bucketsOutOfCompliance.add(x.name());
					}
				} catch (S3Exception s3exception) {
					bucketsOutOfCompliance.add(x.name());
				}
			});
		} catch (S3Exception ex) {
			// AWS Credentials Invalid
			eventPublisher.publishEvent(new AuditUpdateEvent(auditId, Status.FAILED));
			return;
		}

		eventPublisher.publishEvent(new AuditCompleteEvent(auditId, new S3AuditReport(bucketsInCompliance, bucketsOutOfCompliance)));
	}

	@Override
	public boolean supportsService(CloudService cloudService) {
		return cloudService == CloudService.AWS_S3;
	}
}
