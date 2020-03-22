package com.compliancemonkey.rose.worker.compliance.aws.s3;

import com.compliancemonkey.rose.audit.models.Audit.CloudService;
import com.compliancemonkey.rose.audit.models.Audit.ComplianceStrategyIdentifier;
import com.compliancemonkey.rose.audit.models.ComplianceReport;
import com.compliancemonkey.rose.audit.models.StrategyReport;
import com.compliancemonkey.rose.worker.compliance.ComplianceStrategy;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetBucketEncryptionRequest;
import software.amazon.awssdk.services.s3.model.GetBucketEncryptionResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Component
public class S3EncryptionComplianceStrategy implements ComplianceStrategy {

	@Override
	public boolean supportsService(CloudService cloudService) {
		return CloudService.AWS_S3 == cloudService;
	}

	@Override
	public ComplianceStrategyIdentifier complianceIdentifier() {
		return ComplianceStrategyIdentifier.AWS_S3_ENCRYPTION;
	}

	@Override
	public void execute(SdkClient sdkClient, String entityIdentifier, ComplianceReport complianceReport) {
		final S3Client s3Client = (S3Client) sdkClient;
		final GetBucketEncryptionRequest bucketEncryptionRequest = GetBucketEncryptionRequest.builder().bucket(entityIdentifier).build();
		try {
			final GetBucketEncryptionResponse bucketEncryptionResponse = s3Client.getBucketEncryption(bucketEncryptionRequest);
			complianceReport.addStrategyReport(new StrategyReport(bucketEncryptionResponse.serverSideEncryptionConfiguration().hasRules(), complianceIdentifier()));
		} catch (S3Exception s3exception) {
			// software.amazon.awssdk.services.s3.model.S3Exception: The server side encryption configuration was not found
			complianceReport.addStrategyReport(new StrategyReport(false, complianceIdentifier()));
		}
	}
}
