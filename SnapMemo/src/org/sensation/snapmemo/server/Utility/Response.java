package org.sensation.snapmemo.server.Utility;

import java.io.UnsupportedEncodingException;

import com.sun.net.httpserver.HttpExchange;

public class Response {
	public HttpExchange exchange;
	public byte[] responseByteArray;
	public int code;
	public Response(HttpExchange exchange, String responseString, int code) {
		this.exchange = exchange;
		try {
			this.responseByteArray = responseString.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		this.code = code;
	}
	public Response(HttpExchange exchange,int code){
		this.exchange = exchange;
		try {
			this.responseByteArray = ResponseCodeInterpreter.getExplain(code).getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		this.code = code;
	}
	public Response(HttpExchange exchange,int code, byte[] byteArray){
		this.exchange = exchange;
		this.responseByteArray = byteArray;
		this.code = code;
	}
	
}
