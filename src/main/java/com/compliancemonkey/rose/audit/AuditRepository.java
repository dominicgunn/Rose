package com.compliancemonkey.rose.audit;

import com.compliancemonkey.rose.audit.models.Audit;
import com.compliancemonkey.rose.audit.models.Audit.CloudService;
import com.compliancemonkey.rose.audit.models.AuditReport;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Component;

@Component
public class AuditRepository {

	private AtomicInteger auditIdGenerator = new AtomicInteger();

	private final Map<Integer, Audit> auditMap = new ConcurrentHashMap<>();

	public Audit save(int accountId, CloudService cloudService) {
		final Audit audit = new Audit(auditIdGenerator.incrementAndGet(), accountId, cloudService);
		auditMap.put(audit.getAuditId(), audit);
		return audit;
	}

	public Audit update(int auditId, Audit.Status status, AuditReport auditReport) {
		final Audit audit = auditMap.get(auditId);
		if (audit != null) {
			audit.setStatus(status);
			if (auditReport != null) {
				audit.setAuditReport(auditReport);
			}
			auditMap.put(audit.getAuditId(), audit);
		}
		return audit;
	}

	public Audit get(int auditId) {
		return auditMap.get(auditId);
	}
}
