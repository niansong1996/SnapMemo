package org.sensation.snapmemo.server.Network;

import java.io.IOException;

import org.sensation.snapmemo.server.Utility.Request;
import org.sensation.snapmemo.server.Utility.RequestQueue;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class HandlerForSM implements HttpHandler{
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		System.out.println("get a request");
		Request request = new Request(exchange);
		RequestQueue.put(request);
	}
}
