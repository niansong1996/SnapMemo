package org.sensation.snapmemo.server.Utility;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

public class Request {
	public RequestType type;
	public InputStream is;
	public HttpExchange exchange;
	
	public Request(HttpExchange exchange){
		this.exchange = exchange;
		this.is = exchange.getRequestBody();
		Map<String,List<String>> map = exchange.getRequestHeaders();
		String type = map.get("Request-Type").get(0);
		switch(type){
		case "Resolve-Image": this.type = RequestType.ResolveImage;break;
		default : this.type = RequestType.ResolveImage;break;
		}
	}
	
	
	
}
