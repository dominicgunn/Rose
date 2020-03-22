package com.compliancemonkey.rose.worker.compliance;

import com.compliancemonkey.rose.audit.models.Audit.CloudService;
import com.compliancemonkey.rose.audit.models.Audit.ComplianceStrategyIdentifier;
import com.compliancemonkey.rose.audit.models.AuditComplianceReport;
import software.amazon.awssdk.core.SdkClient;

public interface ComplianceStrategy {

	boolean supportsService(CloudService cloudService);
	ComplianceStrategyIdentifier complianceIdentifier();
	AuditComplianceReport verifyCompliance(SdkClient sdkClient, String entityIdentifier);
}
