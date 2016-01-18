package server;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


public class HttpRequestHandler {
	HttpServer server;
	public HttpRequestHandler(){
		HttpServer server;
		try {
			server = HttpServer.create(new InetSocketAddress("127.0.0.1",5678),0);
			server.createContext("/",new HandlerA());
			server.setExecutor(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void startHandle(){
		server.start();
	}
}

class HandlerA implements HttpHandler{
	@Override
	public void handle(HttpExchange arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}

}