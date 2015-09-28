package com.kcthota.helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kcthota.JSONQuery.Query;
import com.kcthota.JSONQuery.exceptions.MissingNodeException;
import com.kcthota.http.Request;

@Slf4j
public class ExprEvalHelper {
	
	private static ScriptEngineManager manager = new ScriptEngineManager();
    private static ScriptEngine engine = manager.getEngineByName("nashorn");
    
	private static ObjectMapper mapper = new ObjectMapper();
    
	private static Pattern pattern = Pattern.compile("\\$\\{([^}]*)\\}");
	
	public void eval(Request request) {
		String assertExpression = request.getAssertExpression();
		log.debug("Assert Expression before formatting: "+assertExpression);
		if(assertExpression==null) {
			return;
		}
		JsonNode jsonNode = mapper.valueToTree(request);
		String formattedExpression = replaceTokens(assertExpression, jsonNode);
		
		log.debug("Assert Expression after formatting: "+formattedExpression);
		try {
			request.setAssertResult(engine.eval(formattedExpression));
		} catch (ScriptException e) {
			log.warn(e.getMessage(), e);
		}
	}
	
	private String replaceTokens(String expression, JsonNode jsonNode) {
		if (expression == null) {
			return expression;
		}
		Query query=Query.q(jsonNode);
		Matcher matcher = pattern.matcher(expression);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			String token = matcher.group(1);
			try {
			String tokenValue = query.value(token).asText();
			matcher.appendReplacement(sb, tokenValue == null ? token
					: tokenValue);
			} catch(MissingNodeException e) {
				continue;
			}
		}
		matcher.appendTail(sb);
		return sb.toString();
	}
}
