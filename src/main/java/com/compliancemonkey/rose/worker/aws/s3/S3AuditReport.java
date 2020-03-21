package com.compliancemonkey.rose.worker.aws.s3;

import com.compliancemonkey.rose.audit.models.AuditReport;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class S3AuditReport extends AuditReport {

	public S3AuditReport(List<String> objectIdsInCompliance, List<String> objectIdsOutOfCompliance) {
		super(objectIdsInCompliance, objectIdsOutOfCompliance);
	}

	@Override
	@JsonProperty("bucketsInCompliance")
	public List<String> getObjectIdsInCompliance() {
		return super.getObjectIdsInCompliance();
	}

	@Override
	@JsonProperty("bucketsOutOfCompliance")
	public List<String> getObjectIdsOutOfCompliance() {
		return super.getObjectIdsOutOfCompliance();
	}
}
