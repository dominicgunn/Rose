package com.compliancemonkey.rose.worker.compliance.aws.ec2;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.compliancemonkey.rose.audit.models.Audit.CloudService;
import com.compliancemonkey.rose.audit.models.ComplianceReport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.Tag;

@RunWith(MockitoJUnitRunner.class)
public class Ec2TagComplianceStrategyTest {

	private static final String BUCKET_NAME = "testBucketName";

	private static final String COMPLIANT_TAG = "Team";
	private static final String NON_COMPLIANT_TAG = "Owner";

	@InjectMocks
	private Ec2TagComplianceStrategy ec2TagComplianceStrategy;

	private ComplianceReport complianceReport;

	@Before
	public void setUp() {
		complianceReport = new ComplianceReport(BUCKET_NAME);
	}

	@Test
	public void testEC2ServiceSupport() {
		assertTrue(ec2TagComplianceStrategy.supportsService(CloudService.AWS_EC2));
	}

	@Test
	public void testComplianceIfCompliantTagIsAvailable() {
		final Instance instance = Instance.builder().tags(Tag.builder().key(COMPLIANT_TAG).build()).build();
		ec2TagComplianceStrategy.execute(null, null, instance, complianceReport);
		assertTrue(complianceReport.isCompliant());
	}

	@Test
	public void testNonComplianceIfCompliantTagIsNotAvailable() {
		final Instance instance = Instance.builder().tags(Tag.builder().key(NON_COMPLIANT_TAG).build()).build();
		ec2TagComplianceStrategy.execute(null, null, instance, complianceReport);
		assertFalse(complianceReport.isCompliant());
	}
}
