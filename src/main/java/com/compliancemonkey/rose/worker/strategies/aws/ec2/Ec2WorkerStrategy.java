package com.compliancemonkey.rose.worker.strategies.aws.ec2;

import com.compliancemonkey.rose.account.AccountService;
import com.compliancemonkey.rose.audit.events.AuditCompleteEvent;
import com.compliancemonkey.rose.audit.events.AuditUpdateEvent;
import com.compliancemonkey.rose.audit.models.Audit;
import com.compliancemonkey.rose.audit.models.Audit.CloudService;
import com.compliancemonkey.rose.audit.models.Audit.Status;
import com.compliancemonkey.rose.audit.models.AuditReport;
import com.compliancemonkey.rose.audit.models.ComplianceReport;
import com.compliancemonkey.rose.worker.AwsService;
import com.compliancemonkey.rose.worker.compliance.ComplianceStrategy;
import com.compliancemonkey.rose.worker.strategies.ServiceWorkerStrategy;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.Reservation;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
public class Ec2WorkerStrategy implements ServiceWorkerStrategy<Instance> {

	private AwsService awsService;
	private AccountService accountService;
	private ApplicationEventPublisher eventPublisher;

	@Autowired
	public Ec2WorkerStrategy(AwsService awsService, AccountService accountService, ApplicationEventPublisher eventPublisher) {
		this.awsService = awsService;
		this.accountService = accountService;
		this.eventPublisher = eventPublisher;
	}

	@Override
	public void execute(Audit audit, List<ComplianceStrategy<Instance>> complianceStrategies) {
		final StaticCredentialsProvider awsCredentialsProvider = accountService.getAwsCredentialsProvider(audit.getAccountId());
		if (awsCredentialsProvider == null) {
			eventPublisher.publishEvent(new AuditUpdateEvent(audit.getAuditId(), Status.FAILED));
			return;
		}

		eventPublisher.publishEvent(new AuditUpdateEvent(audit.getAuditId(), Status.IN_PROGRESS));

		final Ec2Client ec2Client = awsService.buildEc2Client(awsCredentialsProvider);
		final List<ComplianceReport> complianceReports = new ArrayList<>();

		String nextToken = null;
		do {
			final DescribeInstancesRequest describeInstancesRequest = DescribeInstancesRequest.builder().nextToken(nextToken).build();
			try {
				final DescribeInstancesResponse describeInstancesResponse = ec2Client.describeInstances(describeInstancesRequest);
				for (Reservation reservation : describeInstancesResponse.reservations()) {
					for (Instance instance : reservation.instances()) {
						final ComplianceReport complianceReport = new ComplianceReport(instance.instanceId());
						for (ComplianceStrategy<Instance> complianceStrategy : complianceStrategies) {
							complianceStrategy.execute(ec2Client, instance.instanceId(), instance, complianceReport);
						}
						complianceReports.add(complianceReport);
					}
				}
				nextToken = describeInstancesResponse.nextToken();
			} catch (Ec2Exception ex) {
				// AWS Credentials Invalid
				eventPublisher.publishEvent(new AuditUpdateEvent(audit.getAuditId(), Status.FAILED));
				return;
			}
		} while (nextToken != null);

		eventPublisher.publishEvent(new AuditCompleteEvent(audit.getAuditId(), new AuditReport(complianceReports)));
	}

	@Override
	public boolean supportsService(CloudService cloudService) {
		return CloudService.AWS_EC2.equals(cloudService);
	}
}
