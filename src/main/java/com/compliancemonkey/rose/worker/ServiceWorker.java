package com.compliancemonkey.rose.worker;

import com.compliancemonkey.rose.audit.events.AuditUpdateEvent;
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

	private ApplicationEventPublisher eventPublisher;
	private final Map<CloudService, ServiceWorkerStrategy> strategyMap;

	@Autowired
	public ServiceWorker(ApplicationEventPublisher eventPublisher, List<ServiceWorkerStrategy> serviceWorkerStrategyList) {
		this.strategyMap = new HashMap<>();
		this.eventPublisher = eventPublisher;

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

		eventPublisher.publishEvent(new AuditUpdateEvent(auditId, Status.IN_PROGRESS));
		workerStrategy.execute(auditId);
	}
}
