package com.compliancemonkey.rose.worker.strategies;

import com.compliancemonkey.rose.audit.models.Audit;
import com.compliancemonkey.rose.audit.models.Audit.CloudService;
import com.compliancemonkey.rose.worker.compliance.ComplianceStrategy;
import java.util.List;

public interface ServiceWorkerStrategy<T> {

	void execute(Audit audit, List<ComplianceStrategy<T>> complianceStrategies);
	boolean supportsService(CloudService cloudService);
}
