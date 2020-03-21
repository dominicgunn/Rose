package com.compliancemonkey.rose.audit.events;

import com.compliancemonkey.rose.audit.models.Audit.Status;
import org.springframework.context.ApplicationEvent;

public class AuditUpdateEvent extends ApplicationEvent {

	private final Status auditStatus;

	public AuditUpdateEvent(int auditId, Status auditStatus) {
		super(auditId);
		this.auditStatus = auditStatus;
	}

	public int getAuditId() {
		return (int) getSource();
	}

	public Status getAuditStatus() {
		return auditStatus;
	}
}

