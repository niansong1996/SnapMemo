package server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import common.TimeOccupancyPO;


public class HttpRequestHandler {
	private final String IPAddress = "139.129.40.103";
//	private final String IPAddress = "127.0.0.1";
	private final int port = 5678;
	private final int maxConn = 10;
	HttpServer server;
	public HttpRequestHandler(DataIO data,HTMLHelper html){
		try {
			server = HttpServer.create(new InetSocketAddress(IPAddress,port),maxConn);
			server.createContext("/SPhare",new TestHandler(data,html));
			server.setExecutor(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void startHandle(){
		server.start();
	}
}
class TestHandler implements HttpHandler{
	DataIO data;
	HTMLHelper html;
	TestHandler(DataIO data,HTMLHelper html){
		this.data = data;
		this.html = html;
	}
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		Map<String, List<String>> map = exchange.getRequestHeaders();
		for(String s : map.keySet()){
			System.out.println(s);
		}
		response(exchange);
	}
	public void response(HttpExchange exchange) throws IOException{
		String responseString = data.getAll();
		exchange.sendResponseHeaders(200, responseString.length());     
		OutputStream os = exchange.getResponseBody();     
		os.write(responseString.getBytes());     
		os.close();  
	}
	
}
class HandlerA implements HttpHandler{
	DataIO data;
	HTMLHelper html;
	HandlerA(DataIO data,HTMLHelper html){
		this.data = data;
		this.html = html;
	}
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String result = exchange.getRequestURI().getRawQuery();
		if(result!=null){
			System.out.println("do split");
			String[] temp = result.split("&");
			Map<String,String> map = new HashMap<String,String>();
			for(String s: temp){
				String t[] = s.split("=");
				map.put(t[0], t[1]);
			}
			if(map.get("type").equals("add"))  data.add(
					new TimeOccupancyPO(map.get("name"),
							Integer.parseInt(map.get("severness")),map.get("begin"),
							map.get("end"),map.get("day"))
					);
		}
		response(exchange);
	}
	public void response(HttpExchange exchange) throws IOException{
		String responseString = html.getResponseHtml();
		exchange.sendResponseHeaders(200, responseString.length());     
		OutputStream os = exchange.getResponseBody();     
		os.write(responseString.getBytes());     
		os.close();  
	}

}