package com.compliancemonkey.rose.worker.strategies.aws.s3;

import com.compliancemonkey.rose.account.AccountService;
import com.compliancemonkey.rose.audit.events.AuditCompleteEvent;
import com.compliancemonkey.rose.audit.events.AuditUpdateEvent;
import com.compliancemonkey.rose.audit.models.Audit;
import com.compliancemonkey.rose.audit.models.Audit.CloudService;
import com.compliancemonkey.rose.audit.models.Audit.Status;
import com.compliancemonkey.rose.worker.compliance.ComplianceStrategy;
import com.compliancemonkey.rose.worker.strategies.ServiceWorkerStrategy;
import com.compliancemonkey.rose.worker.AwsService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Component
public class S3WorkerStrategy implements ServiceWorkerStrategy {

	private AwsService awsService;
	private AccountService accountService;
	private ApplicationEventPublisher eventPublisher;

	@Autowired
	public S3WorkerStrategy(AwsService awsService, AccountService accountService, ApplicationEventPublisher eventPublisher) {
		this.awsService = awsService;
		this.accountService = accountService;
		this.eventPublisher = eventPublisher;
	}

	@Override
	public void execute(Audit audit, List<ComplianceStrategy> complianceStrategies) {
		final StaticCredentialsProvider awsCredentialsProvider = accountService.getAwsCredentialsProvider(audit.getAccountId());
		if (awsCredentialsProvider == null) {
			eventPublisher.publishEvent(new AuditUpdateEvent(audit.getAuditId(), Status.FAILED));
			return;
		}

		eventPublisher.publishEvent(new AuditUpdateEvent(audit.getAuditId(), Status.IN_PROGRESS));

		final S3Client s3Client = awsService.buildS3Client(awsCredentialsProvider);

		final List<String> bucketsInCompliance = new ArrayList<>();
		final List<String> bucketsOutOfCompliance = new ArrayList<>();

		try {
			final ListBucketsResponse listBucketsResponse = s3Client.listBuckets();
			listBucketsResponse.buckets().forEach(x -> {
				boolean isCompliant = true;
				for (ComplianceStrategy complianceStrategy : complianceStrategies) {
					if (!complianceStrategy.isCompliant(s3Client, x.name())) {
						isCompliant = false;
					}
				}

				if (isCompliant) {
					bucketsInCompliance.add(x.name());
				} else {
					bucketsOutOfCompliance.add(x.name());
				}
			});
		} catch (S3Exception ex) {
			// AWS Credentials Invalid
			eventPublisher.publishEvent(new AuditUpdateEvent(audit.getAuditId(), Status.FAILED));
			return;
		}

		eventPublisher.publishEvent(new AuditCompleteEvent(audit.getAuditId(), new S3AuditReport(bucketsInCompliance, bucketsOutOfCompliance)));
	}

	@Override
	public boolean supportsService(CloudService cloudService) {
		return cloudService == CloudService.AWS_S3;
	}
}
