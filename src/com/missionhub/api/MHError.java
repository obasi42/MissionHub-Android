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
	
	public static void onFlurryError(Throwable e) {
		try {
			if (e == null) return;
			
			if (e instanceof MHError) {
				FlurryAgent.onError(((MHError) e).getCode(), e.getMessage(), e.getClass().getName());
			} else {
				if (e.getMessage() != null) {
					FlurryAgent.onError("UNKNOWN", e.getMessage(), e.getClass().getName());
				}
			}
		} catch (Exception e2) {}
	}
}