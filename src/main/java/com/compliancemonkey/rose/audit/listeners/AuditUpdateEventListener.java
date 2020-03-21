package com.compliancemonkey.rose.audit.listeners;

import com.compliancemonkey.rose.audit.AuditService;
import com.compliancemonkey.rose.audit.events.AuditUpdateEvent;
import com.compliancemonkey.rose.audit.models.Audit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AuditUpdateEventListener {

	private AuditService auditService;

	@Autowired
	public AuditUpdateEventListener(AuditService auditService) {
		this.auditService = auditService;
	}

	@Async
	@EventListener
	public void handleEvent(AuditUpdateEvent auditUpdateEvent) {
		final Audit audit = auditService.getAudit(auditUpdateEvent.getAuditId());
		auditService.updateAuditStatus(audit.getAuditId(), auditUpdateEvent.getAuditStatus());
	}
}
