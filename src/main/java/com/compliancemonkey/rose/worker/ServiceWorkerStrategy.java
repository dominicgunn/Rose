package com.compliancemonkey.rose.worker;

import com.compliancemonkey.rose.audit.models.Audit;
import com.compliancemonkey.rose.audit.models.Audit.CloudService;

public interface ServiceWorkerStrategy {

	void execute(Audit audit);
	boolean supportsService(CloudService cloudService);
}
