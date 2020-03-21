package com.compliancemonkey.rose.audit;

import com.compliancemonkey.rose.audit.events.QueueAuditEvent;
import com.compliancemonkey.rose.audit.models.Audit;
import com.compliancemonkey.rose.audit.models.Audit.CloudService;
import com.compliancemonkey.rose.audit.models.Audit.Status;
import com.compliancemonkey.rose.audit.models.AuditReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

	private ApplicationEventPublisher eventPublisher;
	private AuditRepository auditRepository;

	@Autowired
	public AuditService(ApplicationEventPublisher eventPublisher, AuditRepository auditRepository) {
		this.eventPublisher = eventPublisher;
		this.auditRepository = auditRepository;
	}

	public Audit createAudit(int accountId, CloudService auditCloudService) {
		final Audit audit = auditRepository.save(accountId, auditCloudService);
		eventPublisher.publishEvent(new QueueAuditEvent(audit.getAuditId()));
		return audit;
	}

	public Audit updateAuditStatus(int auditId, Status auditStatus) {
		return updateAuditStatus(auditId, auditStatus, null);
	}

	public Audit updateAuditStatus(int auditId, Status auditStatus, AuditReport auditReport) {
		return auditRepository.update(auditId, auditStatus, auditReport);
	}

	public Audit getAudit(int auditId) {
		return auditRepository.get(auditId);
	}
}
