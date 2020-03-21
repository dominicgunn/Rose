package com.compliancemonkey.rose.audit.models;

import java.util.List;

public class AuditReport {

	private List<String> objectIdsInCompliance;
	private List<String> objectIdsOutOfCompliance;

	public AuditReport(List<String> objectIdsInCompliance, List<String> objectIdsOutOfCompliance) {
		this.objectIdsInCompliance = objectIdsInCompliance;
		this.objectIdsOutOfCompliance = objectIdsOutOfCompliance;
	}

	public List<String> getObjectIdsInCompliance() {
		return objectIdsInCompliance;
	}

	public List<String> getObjectIdsOutOfCompliance() {
		return objectIdsOutOfCompliance;
	}
}
