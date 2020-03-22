package com.compliancemonkey.rose.audit.models;

import com.compliancemonkey.rose.audit.models.Audit.ComplianceStrategyIdentifier;

public class AuditComplianceReport {

	private String entityIdentifier;
	private ComplianceStrategyIdentifier complianceStrategyIdentifier;
	private boolean compliant;

	public AuditComplianceReport(String entityIdentifier, ComplianceStrategyIdentifier complianceStrategyIdentifier, boolean compliant) {
		this.entityIdentifier = entityIdentifier;
		this.complianceStrategyIdentifier = complianceStrategyIdentifier;
		this.compliant = compliant;
	}

	public String getEntityIdentifier() {
		return entityIdentifier;
	}

	public ComplianceStrategyIdentifier getCloudComplianceStrategy() {
		return complianceStrategyIdentifier;
	}

	public boolean isCompliant() {
		return compliant;
	}
}
