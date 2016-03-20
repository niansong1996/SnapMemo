package org.sensation.snapmemo.servlet;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.sensation.snapmemo.server.Data.MySessionFactory;
import org.sensation.snapmemo.server.Utility.ResponseCodeInterpreter;

import com.sun.net.httpserver.HttpServer;

public class HttpRequestHandler {
//	private final String IPAddress = "139.129.40.103";
	private final String IPAddress = "127.0.0.1";
//	private final String IPAddress = "172.25.183.163";
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
	public static void main(String[] args){
		MySessionFactory f = new MySessionFactory();
		ResponseCodeInterpreter interpreter = new ResponseCodeInterpreter();
		HttpRequestHandler handler = new HttpRequestHandler();
		handler.server.start();
	}
}
