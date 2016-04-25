package org.sensation.snapmemo.server.BusinessLogic;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.sensation.snapmemo.server.Utility.IntStringWrapper;
import org.sensation.snapmemo.server.Utility.RequestType;

import com.sun.net.httpserver.HttpExchange;

public class BLExecutor{

	MainExecutor main;
	UserExecutor user;
	MemoExecutor memo;
	public BLExecutor(){
		this.main = new MainExecutor();
		this.user = new UserExecutor();
		this.memo = new MemoExecutor();
	}
	public IntStringWrapper execute(RequestType type, HttpServletRequest request) throws IOException{

		InputStream is = request.getInputStream();
		IntStringWrapper result = null;
		switch(type){
		case ResolveImage:
			String key = request.getHeader("Touch-Location");
			System.out.println("key is :"+key);
			if(key!=null){
				String[] locs = key.split(",");
				int touchX = Integer.parseInt(locs[0]);
				int touchY = Integer.parseInt(locs[1]);
				result = main.resolveImage(is,touchX,touchY);
			}else{
				result = main.resolveImage(is,-1,-1);
			}
			break;
		case GetMemoList:result = memo.GetMemoList(is);break;
		case DeleteMemo:result = memo.DeleteMemo(is);break;
		case ModifyMemo:result = memo.ModifyMemo(is);break;
		case SignIn:result = user.SignIn(is);break;
		case GetLogo:result = user.GetLogo(is);break;
		case GetUserInfo:result = user.GetUserInfo(is);break;
		case SignUp:result = user.SignUp(is);break;
		case SaveMemo:result = memo.SaveMemo(is);
		}
		return result;
	}
	public IntStringWrapper execute2(RequestType type, HttpExchange exchange) throws IOException{
		InputStream is = exchange.getRequestBody();
		IntStringWrapper result = null;
		switch(type){
		case ResolveImage:result = main.resolveImage(is,-1,-1);break;
		case GetMemoList:result = memo.GetMemoList(is);break;
		case DeleteMemo:result = memo.DeleteMemo(is);break;
		case ModifyMemo:result = memo.ModifyMemo(is);break;
		case SignIn:result = user.SignIn(is);break;
		case GetLogo:result = user.GetLogo(is);break;
		case GetUserInfo:result = user.GetUserInfo(is);break;
		case SignUp:result = user.SignUp(is);break;
		case SaveMemo:result = memo.SaveMemo(is);
		}
		return result;
	}
}
