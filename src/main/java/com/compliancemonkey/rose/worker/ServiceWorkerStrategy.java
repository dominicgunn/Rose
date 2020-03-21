package com.compliancemonkey.rose.worker;

import com.compliancemonkey.rose.audit.models.Audit.CloudService;

public interface ServiceWorkerStrategy {

	void execute(int auditId);
	boolean supportsService(CloudService cloudService);
}
