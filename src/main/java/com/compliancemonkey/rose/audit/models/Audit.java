package com.compliancemonkey.rose.audit.models;

public class Audit {

	public enum Status {
		NOT_STARTED, QUEUED, IN_PROGRESS, FAILED, COMPLETED
	}

	public enum CloudService {
		AWS_S3, AWS_EC2
	}

	private final int auditId;
	private final int accountId;
	private final CloudService cloudService;
	private Status status = Status.NOT_STARTED;

	public Audit(int auditId, int accountId, CloudService cloudService) {
		this.auditId = auditId;
		this.accountId = accountId;
		this.cloudService = cloudService;
	}

	public int getAuditId() {
		return auditId;
	}

	public int getAccountId() {
		return accountId;
	}

	public CloudService getCloudService() {
		return cloudService;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
}
