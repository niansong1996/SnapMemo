package org.sensation.snapmemo.server.Network;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

public class HttpRequestHandler {
//	private final String IPAddress = "139.129.40.103";
//	private final String IPAddress = "127.0.0.1";
	private final String IPAddress = "172.25.187.48";
	private final int port = 5678;
	private final int maxConn = 10;
	private HttpServer server;
	public HttpRequestHandler(){
		try {
			server = HttpServer.create(new InetSocketAddress(IPAddress,port),maxConn);
			server.createContext("/SnapMemo",new HandlerForSM());
			server.setExecutor(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void startHandle(){
		server.start();
	}
}
