package com.compliancemonkey.rose.worker.compliance.aws.s3;

import com.compliancemonkey.rose.audit.models.Audit.CloudService;
import com.compliancemonkey.rose.audit.models.Audit.ComplianceStrategyIdentifier;
import com.compliancemonkey.rose.audit.models.ComplianceReport;
import com.compliancemonkey.rose.audit.models.StrategyReport;
import com.compliancemonkey.rose.worker.compliance.ComplianceStrategy;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.GetBucketTaggingRequest;
import software.amazon.awssdk.services.s3.model.GetBucketTaggingResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.Tag;

@Component
public class S3TagComplianceStrategy implements ComplianceStrategy<Bucket> {

	@Override
	public void execute(SdkClient sdkClient, String entityIdentifier, Bucket bucket, ComplianceReport complianceReport) {
		final S3Client s3Client = (S3Client) sdkClient;
		final GetBucketTaggingRequest bucketTaggingRequest = GetBucketTaggingRequest.builder().bucket(entityIdentifier).build();
		try {
			final GetBucketTaggingResponse bucketTaggingResponse = s3Client.getBucketTagging(bucketTaggingRequest);
			for (Tag tag : bucketTaggingResponse.tagSet()) {
				if ("Team".equalsIgnoreCase(tag.key())) {
					complianceReport.addStrategyReport(new StrategyReport(true, complianceIdentifier()));
					return;
				}
			}
			complianceReport.addStrategyReport(new StrategyReport(false, complianceIdentifier()));
		} catch (S3Exception s3exception) {
			complianceReport.addStrategyReport(new StrategyReport(false, complianceIdentifier()));
		}
	}

	@Override
	public boolean supportsService(CloudService cloudService) {
		return CloudService.AWS_S3 == cloudService;
	}

	@Override
	public ComplianceStrategyIdentifier complianceIdentifier() {
		return ComplianceStrategyIdentifier.AWS_S3_TAGS;
	}
}
