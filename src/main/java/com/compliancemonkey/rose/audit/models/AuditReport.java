package com.compliancemonkey.rose.audit.models;

import java.util.List;

public class AuditReport {

	private int entities;
	private int entitiesInCompliance;
	private List<AuditComplianceReport> complianceReports;

	public AuditReport(List<AuditComplianceReport> complianceReports) {
		this.complianceReports = complianceReports;

		entities = complianceReports.size();
		for (AuditComplianceReport complianceReport : complianceReports) {
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

	public List<AuditComplianceReport> getComplianceReports() {
		return complianceReports;
	}
}
