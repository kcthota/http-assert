package com.kcthota.http;

import java.io.IOException;
import java.util.List;
import java.util.function.BiConsumer;

import lombok.NonNull;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kcthota.helpers.ExprEvalHelper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;

@Slf4j
public class Executor {

	private static ObjectMapper mapper = new ObjectMapper();
	private static ExprEvalHelper evalHelper=new ExprEvalHelper();
	
	public void execute(@NonNull Request request) {

		try {
			HttpResponse<String> httpResponse = getHttpRequest(request)
					.asString();
			request.setResponse(processHttpResponse(httpResponse));
			evalHelper.eval(request);
			
		} catch (UnirestException e) {
			log.error(e.getMessage(), e);
		}
	}
	
	public JsonNode execute(String json) throws JsonParseException, JsonMappingException, IOException {
		Request request = mapper.readValue(json, Request.class);
		execute(request);
		return mapper.valueToTree(request);
	}

	private Response processHttpResponse(HttpResponse<String> httpResponse) {
		val response = new Response();
		response.setStatusCode(httpResponse.getStatus());
		response.setPayload(httpResponse.getBody());
		// process headers to map
		httpResponse.getHeaders().forEach(
				new BiConsumer<String, List<String>>() {
					public void accept(String key, List<String> values) {
						if (values.size() > 0) {
							response.addHeader(key, values.get(0));
						}
					}
				});

		// attempt to convert body to jsonNode
		try {
			response.setJsonPayload(mapper.readTree(httpResponse.getBody()));
		} catch (Exception e) {
			// ignore any exceptions if the body is not in json
			log.debug("Body not in json format");
		}
		
		return response;
	}

	private HttpRequest getHttpRequest(Request request) {
		HttpRequest httpRequest = null;
		switch (request.getType()) {
		case GET:
			httpRequest = Unirest.get(request.getUrl());
			break;
		case POST:
			httpRequest = Unirest.post(request.getUrl());
			setPayload(request, httpRequest);
			break;
		case PUT:
			httpRequest = Unirest.put(request.getUrl());
			setPayload(request, httpRequest);
			break;
		case PATCH:
			httpRequest = Unirest.patch(request.getUrl());
			setPayload(request, httpRequest);
			break;
		case DELETE:
			httpRequest = Unirest.delete(request.getUrl());
			setPayload(request, httpRequest);
			break;
		case HEAD:
			httpRequest = Unirest.head(request.getUrl());
			break;
		default:
			log.warn("Not a supported HTTP Request Method: "+request.getType());
		}
		
		return httpRequest;
	}
	
	private void setPayload(Request request, HttpRequest httpRequest) {
		String payload = request.getPayload();
		if(payload!=null) {
			((HttpRequestWithBody) httpRequest).body(request.getPayload());
		}
	}
	
	
}