package com.compliancemonkey.rose.worker;

import com.compliancemonkey.rose.audit.AuditService;
import com.compliancemonkey.rose.audit.events.AuditUpdateEvent;
import com.compliancemonkey.rose.audit.models.Audit;
import com.compliancemonkey.rose.audit.models.Audit.CloudService;
import com.compliancemonkey.rose.audit.models.Audit.Status;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class ServiceWorker {

	private AuditService auditService;
	private ApplicationEventPublisher eventPublisher;
	private final Map<CloudService, ServiceWorkerStrategy> strategyMap;

	@Autowired
	public ServiceWorker(AuditService auditService, ApplicationEventPublisher eventPublisher, List<ServiceWorkerStrategy> serviceWorkerStrategyList) {
		this.auditService = auditService;
		this.eventPublisher = eventPublisher;

		strategyMap = new HashMap<>();
		serviceWorkerStrategyList.forEach(strategy -> {
			Arrays.stream(CloudService.values()).forEach(cloudService -> {
				if (strategy.supportsService(cloudService)) {
					strategyMap.put(cloudService, strategy);
				}
			});
		});
	}

	public void execute(int auditId, CloudService cloudService) {
		final ServiceWorkerStrategy workerStrategy = strategyMap.get(cloudService);
		if (workerStrategy == null) {
			eventPublisher.publishEvent(new AuditUpdateEvent(auditId, Status.FAILED));
			return;
		}

		final Audit audit = auditService.getAudit(auditId);
		if (audit == null) {
			eventPublisher.publishEvent(new AuditUpdateEvent(auditId, Status.FAILED));
			return;
		}

		eventPublisher.publishEvent(new AuditUpdateEvent(auditId, Status.IN_PROGRESS));
		workerStrategy.execute(audit);
	}
}
