package com.compliancemonkey.rose.audit.models;

import com.compliancemonkey.rose.audit.models.Audit.ComplianceStrategyIdentifier;

public class StrategyReport {

	private boolean compliant;
	private ComplianceStrategyIdentifier complianceStrategyIdentifier;

	public StrategyReport(boolean compliant, ComplianceStrategyIdentifier complianceStrategyIdentifier) {
		this.compliant = compliant;
		this.complianceStrategyIdentifier = complianceStrategyIdentifier;
	}

	public boolean isCompliant() {
		return compliant;
	}

	public ComplianceStrategyIdentifier getComplianceStrategyIdentifier() {
		return complianceStrategyIdentifier;
	}
}
