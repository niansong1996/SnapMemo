package org.sensation.snapmemo.server.BusinessLogic;

public class BLController {
	public void startService(){
		Thread thread1 = new Thread(new BLExecutor());
		thread1.start();
	}
}
