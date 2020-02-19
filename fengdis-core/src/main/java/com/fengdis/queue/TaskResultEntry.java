package com.fengdis.queue;

import java.util.concurrent.Future;

public class TaskResultEntry {
	private String key;
	private Future<TaskResult> futureResult;

	public TaskResultEntry() {
	}

	public TaskResultEntry(String key, Future<TaskResult> futureResult) {
		this.key = key;
		this.futureResult = futureResult;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Future<TaskResult> getFutureResult() {
		return futureResult;
	}

	public void setFutureResult(Future<TaskResult> futureResult) {
		this.futureResult = futureResult;
	}

}
