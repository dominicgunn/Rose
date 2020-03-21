package com.compliancemonkey.rose.audit.events;

import com.compliancemonkey.rose.audit.models.AuditReport;
import org.springframework.context.ApplicationEvent;

public class AuditCompleteEvent extends ApplicationEvent  {
	private final AuditReport auditReport;

	public AuditCompleteEvent(int auditId, AuditReport auditReport) {
		super(auditId);
		this.auditReport = auditReport;
	}

	public int getAuditId() {
		return (int) getSource();
	}

	public AuditReport getAuditReport() {
		return auditReport;
	}
}
