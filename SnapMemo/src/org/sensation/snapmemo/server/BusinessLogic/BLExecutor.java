package org.sensation.snapmemo.server.BusinessLogic;

import org.sensation.snapmemo.server.Utility.Request;
import org.sensation.snapmemo.server.Utility.RequestQueue;

public class BLExecutor implements Runnable{

	MainExecutor main;
	UserExecutor user;
	MemoExecutor memo;
	public BLExecutor(){
		this.main = new MainExecutor();
		this.user = new UserExecutor();
		this.memo = new MemoExecutor();
	}
	private void execute(Request request){
		if(request==null) return;
		switch(request.type){
		case ResolveImage: main.resolveImage(request);break;
		case GetMemoList: memo.GetMemoList(request);break;
		case DeleteMemo: memo.DeleteMemo(request);break;
		case ModifyMemo: memo.ModifyMemo(request);break;
		case SignIn: user.SignIn(request);break;
		case GetLogo: user.GetLogo(request);break;
		case GetUserInfo: user.GetUserInfo(request);break;
		case SignUp: user.SignUp(request);break;
		case SaveMemo: memo.SaveMemo(request);
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
