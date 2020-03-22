package com.compliancemonkey.rose.worker.compliance;

import com.compliancemonkey.rose.audit.models.Audit.CloudService;
import software.amazon.awssdk.core.SdkClient;

public interface ComplianceStrategy {

	boolean isCompliant(SdkClient sdkClient, String entityIdentifier);
	boolean supportsService(CloudService cloudService);
}
