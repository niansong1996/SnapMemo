package org.sensation.snapmemo.server.Network;

import org.sensation.snapmemo.server.BusinessLogicService.NetworkService;

public class NetworkController implements NetworkService{
	HttpRequestHandler handler;
	public NetworkController(){
		handler = new HttpRequestHandler();
	}
	public void startListen(){
		handler.startHandle();
		Thread thread1 = new Thread(new ResponseTransmiter());
		thread1.start();
	}
}
