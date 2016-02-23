package com.kcthota.test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.Rule;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.kcthota.enums.RequestType;
import com.kcthota.http.Executor;
import com.kcthota.http.Request;
import com.kcthota.http.Response;

public class BasicTest {

	private static final int port = 8080;

	private static final String url = "http://localhost:" + port;

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(port);

	@Test
	public void simpleGetJsonTest() {
		String endPoint = "/" + UUID.randomUUID().toString();
		String body = "{\"a\":\"b\"}";

		stubFor(get(urlEqualTo(endPoint)).willReturn(
				aResponse().withStatus(200)
						.withHeader("Content-Type", "application/json")
						.withBody(body)));

		Request request = new Request(url + endPoint);

		request.setExpression("${response/statusCode} == 200");
		request.execute();

		Response response = request.getResponse();
		assertThat(response.getStatusCode()).isEqualTo(200);
		assertThat(response.getPayload()).isEqualTo(body);

		assertThat(request.getExpressionResult()).isEqualTo(true);
	}

	@Test
	public void simpleGetHtmlTest() {
		String endPoint = "/" + UUID.randomUUID().toString();
		String body = "ok";

		stubFor(get(urlEqualTo(endPoint))
				.willReturn(
						aResponse().withStatus(200)
								.withHeader("Content-Type", "text/html")
								.withBody(body)));

		Request request = new Request(url + endPoint);

		request.setExpression("'${response/payload}' == 'ok'");
		request.execute();

		Response response = request.getResponse();
		assertThat(response.getStatusCode()).isEqualTo(200);
		assertThat(response.getPayload()).isEqualTo(body);

		assertThat(request.getExpressionResult()).isEqualTo(true);
	}

	@Test
	public void simplePOSTJsonTest() {
		String endPoint = "/" + UUID.randomUUID().toString();
		String body = "{\"a\":\"b\"}";

		stubFor(post(urlEqualTo(endPoint))
				.willReturn(
						aResponse().withStatus(200)
								.withHeader("Content-Type", "application/json")
								.withHeader("customheader", "somevalue")
								.withBody(body)));

		Request request = new Request(url + endPoint, RequestType.POST, body);

		request.setExpression("${response/statusCode} == 200 && '${response/headers/customheader/0}' === 'somevalue'");
		request.execute();

		Response response = request.getResponse();
		assertThat(response.getStatusCode()).isEqualTo(200);
		assertThat(response.getPayload()).isEqualTo(body);

		assertThat(request.getExpressionResult()).isEqualTo(true);
	}

	@Test
	public void cookieTest() {
		String endPoint = "/" + UUID.randomUUID().toString();
		String body = "{\"a\":\"b\"}";

		stubFor(post(urlEqualTo(endPoint))
				.willReturn(
						aResponse()
								.withStatus(200)
								.withHeader("Content-Type", "application/json")
								.withHeader("Set-Cookie",
										"username=John Doe1; expires=Sat, 01-Jan-2018 00:00:00 GMT; path=/")
								.withHeader("Set-Cookie",
										"username2=John Doe2; expires=Sat, 01-Jan-2018 00:00:00 GMT; path=/")
								.withBody(body)));

		Request request = new Request(url + endPoint, RequestType.POST, body);

		request.setExpression("${response/statusCode} == 200 && '${response/cookies/username}' == 'John Doe1'");
		request.execute();

		Response response = request.getResponse();
		assertThat(response.getStatusCode()).isEqualTo(200);
		assertThat(response.getPayload()).isEqualTo(body);

		assertThat(request.getExpressionResult()).isEqualTo(true);
	}

	@Test
	public void ExecGetWithJsonTest() {
		String endPoint = "/" + UUID.randomUUID().toString();
		String body = "{\"a\":\"b\"}";

		stubFor(get(urlEqualTo(endPoint)).willReturn(
				aResponse().withStatus(200)
						.withHeader("Content-Type", "application/json")
						.withBody(body)));

		String jsonRequestPayload = "{\"url\":\"" + url + endPoint
				+ "\", \"type\":\"GET\"}";
		Executor executor = new Executor();
		JsonNode jsonResponse = null;
		try {
			jsonResponse = executor.execute(jsonRequestPayload);
		} catch (Exception e) {

		}

		assertThat(jsonResponse.at("/response/statusCode").intValue())
				.isEqualTo(200);

		assertThat(jsonResponse.at("/response/payload").textValue()).isEqualTo(
				body);

		assertThat(jsonResponse.at("/expressionResult").isNull()).isTrue();
	}

}
