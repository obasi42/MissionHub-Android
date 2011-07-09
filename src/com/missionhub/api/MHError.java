package com.missionhub.api;

import com.flurry.android.FlurryAgent;

public class MHError extends Exception{
	private static final long serialVersionUID = 1L;
	
	public MHError(String message, String code) {
		super(message);
		this.code = code;
	}
	
	public MHError(GError error) {
		super(error.getError().getMessage());
		this.code = error.getError().getCode();
	}
	
	private String code;
	
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
			
			
			if (e instanceof MHError) {
				if (userId != null && !userId.equals("")) {
					FlurryAgent.onError(((MHError) e).getCode(), e.getMessage() + " || UserID: " + userId, e.getClass().getName());
				} else {
					FlurryAgent.onError(((MHError) e).getCode(), e.getMessage(), e.getClass().getName());
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
}