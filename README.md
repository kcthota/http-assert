# http-assert
Execute HTTP calls and evaluate varieties of expressions on top of HTTP responses.

*Supported Methods: GET, POST, PUT, DELETE, HEAD, PATCH*

##Examples:

**HTTP Response Status**
```
Request request = new Request("http://localhost:8080/getUrl");

request.setExpression("${response/statusCode} == 200");

request.execute();

```

**Response body**

```
Request request = new Request(url + endPoint);

request.setExpression("'${response/payload}' == 'ok'");

request.execute();

```

**Response headers**

```
Request request = new Request(url + endPoint, RequestType.POST, body);

request.setExpression("${response/statusCode} == 200 && '${response/headers/customheader}' === 'somevalue'");

request.execute();

```
		



