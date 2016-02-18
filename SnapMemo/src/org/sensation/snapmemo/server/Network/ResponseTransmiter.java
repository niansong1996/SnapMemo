package org.sensation.snapmemo.server.Network;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.sensation.snapmemo.server.Utility.Response;
import org.sensation.snapmemo.server.Utility.ResponseQueue;
import org.sensation.snapmemo.server.Utility.UtilityTools;

import com.sun.net.httpserver.HttpExchange;

public class ResponseTransmiter implements Runnable{

	public void run() {
		Response response = null;
		while(true){
			synchronized(this){
				if(!ResponseQueue.isEmpty())
					response = ResponseQueue.get();
			}
				response(response);
				response = null;
		}
	}
	public void response(Response response){
		if(response==null) return;
		HttpExchange exchange = response.exchange;
		String responseString = UtilityTools.byte2String(response.responseByteArray);
		System.out.println("response json is : "+responseString);
		int code = response.code;
		try {
			exchange.sendResponseHeaders(code, response.responseByteArray.length);
			OutputStream os = exchange.getResponseBody();     
			os.write(response.responseByteArray);     
			os.close();  
		} catch (IOException e) {
			e.printStackTrace();
		}     
	}

}
