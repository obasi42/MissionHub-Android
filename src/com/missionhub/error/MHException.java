package com.missionhub.error;

import com.flurry.android.FlurryAgent;
import com.missionhub.api.GError;
import com.missionhub.auth.User;

public class MHException extends Exception{
	private static final long serialVersionUID = 1L;
	
	private String code;
	private String title;
	
	public MHException(String title, String message, String code) {
		super(message);
		this.setTitle(title);
		this.code = code;
	}
	
	public MHException(String message, String code) {
		super(message);
		this.code = code;
	}
	
	public MHException(GError error) {
		super(error.getError().getMessage());
		this.code = error.getError().getCode();
		this.setTitle(error.getError().getTitle());
	}
	
	public MHException(String message, String code, StackTraceElement[] stackTrace) {
		this(message, code);
		this.setStackTrace(stackTrace);
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
	
	public static void onFlurryError(Throwable e, String title) {
		try {
			if (e == null) return;
			
			String userId = "";
			try {
				userId = String.valueOf(User.getContact().getPerson().getId());
			} catch (Exception e2) {}
			
			
			if (e instanceof MHException) {
				if (userId != null && !userId.equals("")) {
					FlurryAgent.onError(((MHException) e).getCode(), e.getMessage() + " || UserID: " + userId, e.getClass().getName());
				} else {
					FlurryAgent.onError(((MHException) e).getCode(), e.getMessage(), e.getClass().getName());
				}
			} else {
				if (e.getMessage() != null) {
					if (userId != null && !userId.equals("")) {
						FlurryAgent.onError(title, e.getMessage() + " || UserID: " + userId, e.getClass().getName());
					} else {
						FlurryAgent.onError(title, e.getMessage(), e.getClass().getName());
					}
				}
			}
		} catch (Exception e2) {}
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}