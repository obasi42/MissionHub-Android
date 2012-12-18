package com.missionhub.model.gson;

import com.missionhub.api.ApiException;

public class GErrors {

	public String[] errors;

	public ApiException getException() {
		final StringBuffer sb = new StringBuffer();
		if (errors != null) {
			for (final String error : errors) {
				sb.append(error + "\n");
			}
		}
		return new ApiException(sb.toString().trim());
	}

}