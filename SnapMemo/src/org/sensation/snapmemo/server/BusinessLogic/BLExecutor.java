package org.sensation.snapmemo.server.BusinessLogic;

import org.sensation.snapmemo.server.Utility.Request;
import org.sensation.snapmemo.server.Utility.RequestQueue;

public class BLExecutor implements Runnable{

	MainExecutor main;
	UserExecutor user;
	public BLExecutor(){
		this.main = new MainExecutor();
		this.user = new UserExecutor();
	}
	private void execute(Request request){
		if(request==null) return;
		switch(request.type){
		case ResolveImage: main.resolveImage(request);break;
		default : System.err.println("invalid request in executor!");break;
		}
	}
	

	@Override
	public void run() {
		Request request = null;
		while(true){
			synchronized(this){
				if(!RequestQueue.isEmpty()){
					request = RequestQueue.get();
				}
			}
			execute(request);
			request = null;
		}
	}
}
