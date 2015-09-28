package com.kcthota.http;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import com.kcthota.enums.RequestType;

public class Request {

	@Getter
	@Setter
	private String url;

	@Getter
	@Setter
	private RequestType type = RequestType.GET;

	@Getter
	@Setter
	private String payload;

	@Getter
	@Setter
	private Map<String, String> headers =new HashMap<String, String>();

	@Getter
	@Setter
	private Response response;
	
	@Setter
	@Getter
	private String expression;
	
	@Getter
	@Setter
	private Object expressionResult;
	

	public Request() {
		
	}
	
	public Request(String url) {
		this.url = url;
	}

	public Request(String url, RequestType type) {
		this.url = url;
		this.type = type;
	}

	public Request(String url, RequestType type, String payload) {
		this.url = url;
		this.type = type;
		this.payload = payload;
	}

	public Request execute() {
		Executor executor = new Executor();
		executor.execute(this);
		return this;
	}
}
