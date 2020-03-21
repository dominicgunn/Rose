package com.compliancemonkey.rose.worker.aws;

import com.compliancemonkey.rose.audit.models.Audit.CloudService;
import com.compliancemonkey.rose.worker.ServiceWorkerStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class S3WorkerStrategy implements ServiceWorkerStrategy {

	private ApplicationEventPublisher eventPublisher;

	@Autowired
	public S3WorkerStrategy(ApplicationEventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	@Override
	public void execute(int auditId) {

	}

	@Override
	public boolean supportsService(CloudService cloudService) {
		return cloudService == CloudService.AWS_S3;
	}
}
