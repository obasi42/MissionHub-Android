package com.missionhub.util;

public abstract class SSEFEvent {
	
	public static enum Status {
		STARTED, SUCCESS, ERROR, FINISHED
	}
	
	public Status status;
	public Object data;
	
	public SSEFEvent (Status status) {
		this(status,  null);
	}
	
	public SSEFEvent(Status status, Object data) {
		this.status = status;
		this.data = data;
	}
	
}