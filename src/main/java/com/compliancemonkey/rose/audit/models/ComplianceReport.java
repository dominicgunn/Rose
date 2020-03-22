package com.compliancemonkey.rose.audit.models;

import java.util.ArrayList;
import java.util.List;

public class ComplianceReport {

	private String entityIdentifier;
	private List<StrategyReport> strategyReports;

	public ComplianceReport(String entityIdentifier) {
		this.entityIdentifier = entityIdentifier;
		this.strategyReports = new ArrayList<>();
	}

	public void addStrategyReport(StrategyReport strategyReport) {
		strategyReports.add(strategyReport);
	}

	public String getEntityIdentifier() {
		return entityIdentifier;
	}

	public List<StrategyReport> getStrategyReports() {
		return strategyReports;
	}

	public boolean isCompliant() {
		for (StrategyReport strategyReport : strategyReports) {
			if (!strategyReport.isCompliant()) {
				return false;
			}
		}
		return true;
	}
}
