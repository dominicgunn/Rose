package com.compliancemonkey.rose.audit.listeners;

import com.compliancemonkey.rose.audit.AuditService;
import com.compliancemonkey.rose.audit.events.QueueAuditEvent;
import com.compliancemonkey.rose.audit.models.Audit;
import com.compliancemonkey.rose.audit.models.Audit.Status;
import com.compliancemonkey.rose.worker.ServiceWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class QueueAuditEventListener {

	private AuditService auditService;
	private ServiceWorker serviceWorker;

	@Autowired
	public QueueAuditEventListener(AuditService auditService, ServiceWorker serviceWorker) {
		this.auditService = auditService;
		this.serviceWorker = serviceWorker;
	}

	@Async
	@EventListener
	public void handleEvent(QueueAuditEvent queueAuditEvent) throws InterruptedException {
		Thread.sleep(2500);
		final Audit audit = auditService.getAudit(queueAuditEvent.getAuditId());
		auditService.updateAuditStatus(audit.getAuditId(), Status.QUEUED);
		serviceWorker.execute(queueAuditEvent.getAuditId(), audit.getCloudService());
	}
}
