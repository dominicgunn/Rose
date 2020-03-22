package com.compliancemonkey.rose.audit.models;

import java.util.List;

public class AuditReport {

	private int entities;
	private int entitiesInCompliance;
	private List<ComplianceReport> complianceReports;

	public AuditReport(List<ComplianceReport> complianceReports) {
		this.complianceReports = complianceReports;

		entities = complianceReports.size();
		for (ComplianceReport complianceReport : complianceReports) {
			if (complianceReport.isCompliant()) {
				entitiesInCompliance++;
			}
		}
	}

	public int getEntities() {
		return entities;
	}

	public int getEntitiesInCompliance() {
		return entitiesInCompliance;
	}

	public int getEntitiesOutOfCompliance() {
		return (entities - entitiesInCompliance);
	}

	public String getEntityPercentageInCompliance() {
		return String.format("%.2f", (((float) entitiesInCompliance) / ((float) entities) * 100));
	}

	public List<ComplianceReport> getComplianceReports() {
		return complianceReports;
	}
}
