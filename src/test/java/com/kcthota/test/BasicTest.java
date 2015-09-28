package com.kcthota.test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.kcthota.http.Request;

public class BasicTest {
	
	private static final int port = 8080;
	
	private static final String url = "http://localhost:"+port;
	
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(port);

	@Test
	public void simpleGetTest() {
		
		stubFor(get(urlEqualTo("/simpleGet"))
	            .willReturn(aResponse()
	                .withStatus(200)
	                .withHeader("Content-Type", "application/json")
	                .withBody("{\"a\":\"b\"}")));
		
		Request request = new Request(url+"/simpleGet");
		
		request.setAssertExpression("${response/statusCode} == 200");
		request.execute();
	}

}
