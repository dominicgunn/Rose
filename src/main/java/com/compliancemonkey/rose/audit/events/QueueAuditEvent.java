package com.compliancemonkey.rose.audit.events;

import org.springframework.context.ApplicationEvent;

public class QueueAuditEvent extends ApplicationEvent {

	public QueueAuditEvent(int auditId) {
		super(auditId);
	}

	public int getAuditId() {
		return (int) getSource();
	}
}
