package org.sensation.snapmemo.server.Utility;

import com.sun.net.httpserver.HttpExchange;

public class Response {
	public HttpExchange exchange;
	public String responseString;
	public int code;
	public Response(HttpExchange exchange, String responseString, int code) {
		this.exchange = exchange;
		this.responseString = responseString;
		this.code = code;
	}
	
}
