package com.compliancemonkey.rose.audit;

import com.compliancemonkey.rose.audit.models.Audit;
import com.compliancemonkey.rose.audit.models.Audit.CloudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class AuditController {

	private AuditService auditService;

	@Autowired
	public AuditController(AuditService auditService) {
		this.auditService = auditService;
	}

	@GetMapping("/audit/{auditId}")
	public Mono<Audit> getAuditForService(@PathVariable("auditId") int auditId) {
		return Mono.just(auditService.getAudit(auditId));
	}

	@PostMapping("/audit/{accountId}/service/{service}")
	public Mono<Audit> beginAudit(@PathVariable("accountId") int accountId, @PathVariable("service") CloudService auditCloudService) {
		return Mono.just(auditService.createAudit(accountId, auditCloudService));
	}
}
