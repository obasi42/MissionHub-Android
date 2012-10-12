package com.missionhub.network;

import java.io.IOException;

public class HttpException extends IOException {
	public HttpException(final String message) {
		super(message);
	}

	private static final long serialVersionUID = 1L;
}