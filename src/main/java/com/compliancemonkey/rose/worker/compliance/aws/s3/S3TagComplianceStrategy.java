package com.compliancemonkey.rose.worker.compliance.aws.s3;

import com.compliancemonkey.rose.audit.models.Audit.CloudService;
import com.compliancemonkey.rose.worker.compliance.ComplianceStrategy;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetBucketTaggingRequest;
import software.amazon.awssdk.services.s3.model.GetBucketTaggingResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.Tag;

@Component
public class S3TagComplianceStrategy implements ComplianceStrategy {

	@Override
	public boolean isCompliant(SdkClient sdkClient, String entityIdentifier) {
		final S3Client s3Client = (S3Client) sdkClient;
		final GetBucketTaggingRequest bucketTaggingRequest = GetBucketTaggingRequest.builder().bucket(entityIdentifier).build();
		try {
			final GetBucketTaggingResponse bucketTaggingResponse = s3Client.getBucketTagging(bucketTaggingRequest);
			for (Tag tag : bucketTaggingResponse.tagSet()) {
				if ("Team".equalsIgnoreCase(tag.key())) {
					return true;
				}
			}
			return false;
		} catch (S3Exception s3exception) {
			return false;
		}
	}

	@Override
	public boolean supportsService(CloudService cloudService) {
		return CloudService.AWS_S3 == cloudService;
	}
}
