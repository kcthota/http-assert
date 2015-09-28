package com.kcthota.http;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.databind.JsonNode;

public class Response {

	@Getter
	@Setter
	private int statusCode;

	@Getter
	@Setter
	private String payload;

	@Getter
	private final Map<String, String> headers = new HashMap<String, String>();

	@Getter
	@Setter
	private JsonNode jsonPayload;

	public void addHeader(String key, String value) {
		headers.put(key, value);
	}
}
