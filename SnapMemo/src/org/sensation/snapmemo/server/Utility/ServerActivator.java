package org.sensation.snapmemo.server.Utility;

import org.sensation.snapmemo.server.BusinessLogic.BLController;
import org.sensation.snapmemo.server.Data.MySessionFactory;
import org.sensation.snapmemo.server.Network.NetworkController;

public class ServerActivator {
	public static void main(String args[]){
		ServerActivator activator = new ServerActivator();
		activator.activate();
	}
	NetworkController networkController;
	BLController businessLogicController;
	public ServerActivator(){
		networkController = new NetworkController();
		businessLogicController = new BLController();
	}
	@SuppressWarnings("unused")
	public void activate(){
		ResponseCodeInterpreter interpreter = new ResponseCodeInterpreter();
		MySessionFactory sessionFactory = new MySessionFactory();
		RequestQueue requestQ = new RequestQueue();
		ResponseQueue responseQ = new ResponseQueue();
		networkController.startListen();
		businessLogicController.startService();
		System.out.println("Server is ready.");
	}
}
