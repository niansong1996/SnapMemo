package server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


public class HttpRequestHandler {
	private final String IPAddress = "127.0.0.1";
	private final int port = 5678;
	private final int maxConn = 10;
	private DataIO data;
	HttpServer server;
	public HttpRequestHandler(DataIO data){
		this.data = data;
		try {
			server = HttpServer.create(new InetSocketAddress(IPAddress,port),maxConn);
			server.createContext("/",new HandlerA(data));
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
	DataIO data;
	HandlerA(DataIO data){
		this.data = data;
	}
	@Override
	public void handle(HttpExchange exchange) throws IOException {
//		System.out.println("handle"); 
//		for(List<String> ls : exchange.getRequestHeaders().values()){
//			for(String s : ls) System.out.println(s);
//		}
//		String responseString = data.getAll();
		String responseString = "<table border=1 cellspacing=0 cellpadding=0 bordercolordark=\"#000000\" bordercolorlight=\"#ffffff\"><tr><td colspan=2 rowspan=2>1</td><td>2</td><td>3</td></tr><tr> <td>4</td><td>5</td></tr><tr><td>6</td><td>7</td><td>8</td><td>9</td></tr><tr><td>10</td><td>11</td><td>12</td><td>13</td></tr><tr><td>14</td><td>15</td><td>16</td><td>17</td></tr></table>";
		exchange.sendResponseHeaders(200, responseString.length());     
        OutputStream os = exchange.getResponseBody();     
        os.write(responseString.getBytes());     
        os.close(); 
	}

}