package com.compliancemonkey.rose.worker.compliance.aws.ec2;

import com.compliancemonkey.rose.audit.models.Audit.CloudService;
import com.compliancemonkey.rose.audit.models.Audit.ComplianceStrategyIdentifier;
import com.compliancemonkey.rose.audit.models.ComplianceReport;
import com.compliancemonkey.rose.audit.models.StrategyReport;
import com.compliancemonkey.rose.worker.compliance.ComplianceStrategy;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.Tag;

@Component
public class Ec2TagComplianceStrategy implements ComplianceStrategy<Instance> {

	@Override
	public boolean supportsService(CloudService cloudService) {
		return CloudService.AWS_EC2.equals(cloudService);
	}

	@Override
	public ComplianceStrategyIdentifier complianceIdentifier() {
		return ComplianceStrategyIdentifier.AWS_EC2_TAGS;
	}

	@Override
	public void execute(SdkClient sdkClient, String entityIdentifier, Instance entity, ComplianceReport complianceReport) {
		for (Tag tag : entity.tags()) {
			if ("Team".equalsIgnoreCase(tag.key())) {
				complianceReport.addStrategyReport(new StrategyReport(true, complianceIdentifier()));
				return;
			}
		}
		complianceReport.addStrategyReport(new StrategyReport(false, complianceIdentifier()));
	}
}
