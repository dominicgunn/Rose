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
import software.amazon.awssdk.services.s3.model.GetBucketAclRequest;
import software.amazon.awssdk.services.s3.model.GetBucketAclResponse;
import software.amazon.awssdk.services.s3.model.Grant;
import software.amazon.awssdk.services.s3.model.Permission;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Component
public class S3AclComplianceStrategy implements ComplianceStrategy<Bucket>  {

	private static final String GROUPS_USERS = "http://acs.amazonaws.com/groups/global/AllUsers";

	@Override
	public boolean supportsService(CloudService cloudService) {
		return CloudService.AWS_S3 == cloudService;
	}

	@Override
	public ComplianceStrategyIdentifier complianceIdentifier() {
		return ComplianceStrategyIdentifier.AWS_S3_ACL;
	}

	@Override
	public void execute(SdkClient sdkClient, String entityIdentifier, Bucket bucket, ComplianceReport complianceReport) {
		final S3Client s3Client = (S3Client) sdkClient;
		final GetBucketAclRequest bucketAclRequest = GetBucketAclRequest.builder().bucket(entityIdentifier).build();
		try {
			final GetBucketAclResponse bucketAclResponse = s3Client.getBucketAcl(bucketAclRequest);
			for (Grant grant : bucketAclResponse.grants()) {
				if (Permission.READ == grant.permission() && GROUPS_USERS.equalsIgnoreCase(grant.grantee().uri())) {
					complianceReport.addStrategyReport(new StrategyReport(false, complianceIdentifier()));
					return;
				}
			}
			complianceReport.addStrategyReport(new StrategyReport(true, complianceIdentifier()));
		} catch (S3Exception s3exception) {
			complianceReport.addStrategyReport(new StrategyReport(true, complianceIdentifier()));
		}
	}
}
