package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


public class HttpRequestHandler {
	private final String IPAddress = "139.129.40.103";
//	private final String IPAddress = "127.0.0.1";
	private final int port = 5678;
	private final int maxConn = 10;
	HttpServer server;
	public HttpRequestHandler(DataIO data,HTMLHelper html){
		try {
			server = HttpServer.create(new InetSocketAddress(IPAddress,port),maxConn);
			server.createContext("/SPhare",new HandlerA(data,html));
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
		String responseString = "";
		exchange.sendResponseHeaders(200, responseString.length());     
		OutputStream os = exchange.getResponseBody();     
		os.write(responseString.getBytes());     
		os.close();  
	}
	
}
class HandlerA implements HttpHandler{
	DataService data;
	HTMLHelper html;
	HandlerA(DataIO data,HTMLHelper html){
		this.data = data;
		this.html = html;
	}
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		System.out.println("get a request");
//		String result = exchange.getRequestURI().getRawQuery();
		Map<String,List<String>> map = exchange.getRequestHeaders();
		String type = map.get("Request-Type").get(0);
		switch(type){
		case "Get-Group-Info": getGroupInfo(exchange);break;
		case "Add-Free-Time": setFreeTime(exchange);break;
		case "Delete-Free-Time": deleteFreeTime(exchange);break;
		default : System.out.println("bad request");invalidRequest(exchange);break;
		}
	}
	public void getGroupInfo(HttpExchange exchange) throws IOException{
		InputStream stream = exchange.getRequestBody();
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String groupID = reader.readLine();
		if(groupID==null) invalidRequest(exchange);
		String result = data.getGroupJSON(groupID);
		response(exchange,result,200);
	}
	public void setFreeTime(HttpExchange exchange) throws IOException{
		InputStream stream = exchange.getRequestBody();
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String result = "";
		String tmp = "";
		do{
			result += tmp;
			tmp = reader.readLine();
		}while(tmp!=null);
		boolean success = data.setFreeTime(result);
		if(success) response(exchange,"success",200);
		else invalidRequest(exchange);
	}
	private void deleteFreeTime(HttpExchange exchange) throws IOException{
		InputStream stream = exchange.getRequestBody();
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String result = "";
		String tmp = "";
		do{
			result += tmp;
			tmp = reader.readLine();
		}while(tmp!=null);
		boolean success = data.deleteFreeTime(result);
		if(success) response(exchange,"success",200);
		else invalidRequest(exchange);
	}
	public void invalidRequest(HttpExchange exchange) throws IOException{
		response(exchange,"bad request",400);
	}
	public void response(HttpExchange exchange,String responseString,int code) throws IOException{
		exchange.sendResponseHeaders(code, responseString.length());     
		OutputStream os = exchange.getResponseBody();     
		os.write(responseString.getBytes());     
		os.close();  
	}

}