package com.compliancemonkey.rose.audit.listeners;

import com.compliancemonkey.rose.audit.AuditService;
import com.compliancemonkey.rose.audit.events.QueueAuditEvent;
import com.compliancemonkey.rose.audit.models.Audit;
import com.compliancemonkey.rose.audit.models.Audit.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class QueueAuditEventListener {

	@Autowired
	private AuditService auditService;

	@EventListener
	public void handleEvent(QueueAuditEvent queueAuditEvent) {
		final Audit audit = auditService.getAudit(queueAuditEvent.getAuditId());
		auditService.updateAuditStatus(audit.getAuditId(), Status.QUEUED);
	}
}
