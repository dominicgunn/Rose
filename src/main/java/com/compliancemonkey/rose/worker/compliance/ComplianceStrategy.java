package com.compliancemonkey.rose.worker.compliance;

import com.compliancemonkey.rose.audit.models.Audit.CloudService;
import com.compliancemonkey.rose.audit.models.Audit.ComplianceStrategyIdentifier;
import com.compliancemonkey.rose.audit.models.ComplianceReport;
import software.amazon.awssdk.core.SdkClient;

public interface ComplianceStrategy {

	boolean supportsService(CloudService cloudService);
	ComplianceStrategyIdentifier complianceIdentifier();
	void execute(SdkClient sdkClient, String entityIdentifier, ComplianceReport complianceReport);
}
