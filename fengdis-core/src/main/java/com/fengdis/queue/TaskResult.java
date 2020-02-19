package com.fengdis.queue;

public class TaskResult {

	private String id;
	private String content;
	private String inputParams = "";
	private String taskId;
	private String queueName;

	public TaskResult() {

	}

	public TaskResult(String content) {
		this.content = content;
	}

	public TaskResult(String content, String inputParams) {
		this.content = content;
		this.inputParams = inputParams;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getInputParams() {
		return inputParams;
	}

	public void setInputParams(String inputParams) {
		this.inputParams = inputParams;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}
}
