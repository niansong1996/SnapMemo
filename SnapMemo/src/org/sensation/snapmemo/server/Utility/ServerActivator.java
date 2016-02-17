package org.sensation.snapmemo.server.Utility;

import org.sensation.snapmemo.server.BusinessLogic.BLController;
import org.sensation.snapmemo.server.Data.MemoData;
import org.sensation.snapmemo.server.Data.UserData;
import org.sensation.snapmemo.server.Network.NetworkController;

public class ServerActivator {
	public static void main(String args[]){
		ServerActivator activator = new ServerActivator();
		activator.activate();
	}
	
	MemoData memoData;
	UserData userData;
	NetworkController networkController;
	BLController businessLogicController;
	public ServerActivator(){
		memoData = new MemoData();
		userData = new UserData();
		networkController = new NetworkController();
		businessLogicController = new BLController();
	}
	public void activate(){
		RequestQueue requestQ = new RequestQueue();
		ResponseQueue responseQ = new ResponseQueue();
		networkController.startListen();
		businessLogicController.startService();
		System.out.println("Server is ready.");
	}
}
