package com.compliancemonkey.rose.worker;

import com.compliancemonkey.rose.audit.AuditService;
import com.compliancemonkey.rose.audit.events.AuditUpdateEvent;
import com.compliancemonkey.rose.audit.models.Audit;
import com.compliancemonkey.rose.audit.models.Audit.CloudService;
import com.compliancemonkey.rose.audit.models.Audit.Status;
import com.compliancemonkey.rose.worker.compliance.ComplianceStrategy;
import com.compliancemonkey.rose.worker.strategies.ServiceWorkerStrategy;
import java.util.ArrayList;
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

	private final Map<CloudService, ServiceWorkerStrategy> serviceStrategyMap = new HashMap<>();
	private final Map<CloudService, List<ComplianceStrategy>> serviceComplianceMap = new HashMap<>();

	@Autowired
	public ServiceWorker(AuditService auditService, ApplicationEventPublisher eventPublisher,
						 List<ServiceWorkerStrategy> serviceStrategies, List<ComplianceStrategy> complianceStrategies) {
		this.auditService = auditService;
		this.eventPublisher = eventPublisher;

		initializeStrategyMap(serviceStrategies);
		initializeComplianceMap(complianceStrategies);
	}

	private void initializeStrategyMap(List<ServiceWorkerStrategy> serviceStrategies) {
		serviceStrategies.forEach(strategy -> {
			Arrays.stream(CloudService.values()).forEach(cloudService -> {
				if (strategy.supportsService(cloudService)) {
					serviceStrategyMap.put(cloudService, strategy);
				}
			});
		});
	}

	private void initializeComplianceMap(List<ComplianceStrategy> complianceStrategies) {
		complianceStrategies.forEach(strategy -> {
			Arrays.stream(CloudService.values()).forEach(cloudService -> {
				if (strategy.supportsService(cloudService)) {
					serviceComplianceMap.computeIfAbsent(cloudService, k -> new ArrayList<>()).add(strategy);
				}
			});
		});
	}

	public void execute(int auditId, CloudService cloudService) {
		final ServiceWorkerStrategy workerStrategy = serviceStrategyMap.get(cloudService);
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
		workerStrategy.execute(audit, serviceComplianceMap.get(cloudService));
	}
}
