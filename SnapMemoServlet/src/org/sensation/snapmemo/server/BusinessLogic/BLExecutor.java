package org.sensation.snapmemo.server.BusinessLogic;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.sensation.snapmemo.server.Utility.IntStringWrapper;
import org.sensation.snapmemo.server.Utility.RequestType;

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
		case ResolveImage:result = main.resolveImage(is);break;
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
	public IntStringWrapper execute2(RequestType type, InputStream is) throws IOException{
		IntStringWrapper result = null;
		switch(type){
		case ResolveImage:result = main.resolveImage(is);break;
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
