package com.compliancemonkey.rose.worker.compliance;

import com.compliancemonkey.rose.audit.models.Audit.CloudService;
import com.compliancemonkey.rose.audit.models.Audit.ComplianceStrategyIdentifier;
import com.compliancemonkey.rose.audit.models.ComplianceReport;
import software.amazon.awssdk.core.SdkClient;

public interface ComplianceStrategy<T> {

	boolean supportsService(CloudService cloudService);
	ComplianceStrategyIdentifier complianceIdentifier();
	void execute(SdkClient sdkClient, String entityIdentifier, T entity, ComplianceReport complianceReport);
}
