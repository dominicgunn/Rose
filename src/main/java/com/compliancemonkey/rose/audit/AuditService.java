package com.compliancemonkey.rose.audit;

import com.compliancemonkey.rose.audit.events.QueueAuditEvent;
import com.compliancemonkey.rose.audit.models.Audit;
import com.compliancemonkey.rose.audit.models.Audit.CloudService;
import com.compliancemonkey.rose.audit.models.Audit.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Autowired
	private AuditRepository auditRepository;

	public Audit createAudit(int accountId, CloudService auditCloudService) {
		final Audit audit = auditRepository.save(accountId, auditCloudService);
		eventPublisher.publishEvent(new QueueAuditEvent(audit.getAuditId()));
		return audit;
	}

	public Audit updateAuditStatus(int auditId, Status auditStatus) {
		return auditRepository.update(auditId, auditStatus);
	}

	public Audit getAudit(int auditId) {
		return auditRepository.get(auditId);
	}
}
