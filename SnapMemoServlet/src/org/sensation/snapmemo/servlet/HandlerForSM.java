package org.sensation.snapmemo.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.sensation.snapmemo.server.BusinessLogic.BLExecutor;
import org.sensation.snapmemo.server.Utility.IntStringWrapper;
import org.sensation.snapmemo.server.Utility.RequestType;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class HandlerForSM implements HttpHandler{
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		System.out.println("get a request");
		Map<String,List<String>> map = exchange.getRequestHeaders();
		String type = map.get("Request-Type").get(0);
		RequestType requestType = RequestType.valueOf(type.replace("-", ""));
		BLExecutor exe = new BLExecutor();
		IntStringWrapper result = exe.execute2(requestType,exchange.getRequestBody());
		exchange.sendResponseHeaders(result.getCode(), result.getInfo().getBytes().length);
		OutputStream os = exchange.getResponseBody();     
		os.write(result.getInfo().getBytes());     
		os.close();  
	}
}
