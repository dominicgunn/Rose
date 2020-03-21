package com.compliancemonkey.rose.audit.listeners;

import com.compliancemonkey.rose.audit.AuditService;
import com.compliancemonkey.rose.audit.events.AuditCompleteEvent;
import com.compliancemonkey.rose.audit.events.AuditUpdateEvent;
import com.compliancemonkey.rose.audit.models.Audit;
import com.compliancemonkey.rose.audit.models.Audit.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AuditCompleteEventListener {

	private AuditService auditService;

	@Autowired
	public AuditCompleteEventListener(AuditService auditService) {
		this.auditService = auditService;
	}

	@Async
	@EventListener
	public void handleEvent(AuditCompleteEvent auditCompleteEvent) {
		final Audit audit = auditService.getAudit(auditCompleteEvent.getAuditId());
		auditService.updateAuditStatus(audit.getAuditId(), Status.COMPLETED, auditCompleteEvent.getAuditReport());
	}
}