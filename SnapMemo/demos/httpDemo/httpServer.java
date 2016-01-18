package httpDemo;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class httpServer {
	public static void main(String[] args){
		try {
			HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1",5678),0);
			server.createContext("/test",new MyHandler());
//			server.createContext("/",new MyHandler2());
			server.setExecutor(new MyExecutor());
			System.out.println(server.getAddress());
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
class MyHandler implements HttpHandler{

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		System.out.println("handle"); 
		String responseString = "<font color='#ff0000'>Hello! This a HttpServer!</font>";
		exchange.sendResponseHeaders(200, responseString.length());     
        OutputStream os = exchange.getResponseBody();     
        os.write(responseString.getBytes());     
        os.close(); 
	}
	
}
class MyHandler2 implements HttpHandler{

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		System.out.println("handle2");
	}
	
}
class MyExecutor implements Executor{

	@Override
	public void execute(Runnable command) {
		command.run();
	}
	
}