package com.compliancemonkey.rose.worker.aws.s3;

import com.compliancemonkey.rose.audit.models.AuditReport;
import java.util.List;

public class S3AuditReport extends AuditReport {

	public S3AuditReport(List<String> objectIdsInCompliance, List<String> objectIdsOutOfCompliance) {
		super(objectIdsInCompliance, objectIdsOutOfCompliance);
	}
}
