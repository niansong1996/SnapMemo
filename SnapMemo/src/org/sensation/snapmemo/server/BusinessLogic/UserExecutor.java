package org.sensation.snapmemo.server.BusinessLogic;

import java.net.HttpURLConnection;

import org.sensation.snapmemo.server.Data.UserData;
import org.sensation.snapmemo.server.PO.UserPO;
import org.sensation.snapmemo.server.Utility.Request;
import org.sensation.snapmemo.server.Utility.Response;
import org.sensation.snapmemo.server.Utility.ResponseQueue;
import org.sensation.snapmemo.server.Utility.UtilityTools;

import net.sf.json.JSONObject;

public class UserExecutor {
	private UserData data;
	UserExecutor(){
		this.data = new UserData();
	}
	public void SignIn(Request request){
		String userName = this.getAttribute(request, "userName");
		UserPO user = data.findUserByName(userName);
		if(user==null){
			this.sendBadResponse(request);
			return;
		}
		if(user.getPassword().equals(this.getAttribute(request, "password")))
			ResponseQueue.put(new Response(request.exchange,this.getResponseJSON(user),HttpURLConnection.HTTP_OK));
		else
			ResponseQueue.put(new Response(request.exchange,HttpURLConnection.HTTP_FORBIDDEN));
	}
	public void GetLogo(Request request){
		UserPO user = this.getUser(request);
		byte[] logo = UtilityTools.location2Img(user.getLogoLocation());
		ResponseQueue.put(new Response(request.exchange,HttpURLConnection.HTTP_OK,logo));
	}
	public void GetUserInfo(Request request){
		this.sendBadResponse(request);
	}
	public void SignUp(Request request){
	}
	
	private void sendBadResponse(Request request){
		ResponseQueue.put(new Response(request.exchange,HttpURLConnection.HTTP_NOT_FOUND));
	}
	private UserPO getUser(Request request){
		String userID = this.getAttribute(request, "userID");
		UserPO user = data.findUserByID(userID);
		return user;
	}
	private String getAttribute(Request request, String attribute){
		String userID = JSONObject.fromObject(
				UtilityTools.Stream2String(request.is)
				).getString(attribute);
		return userID;
	}
	private String getResponseJSON(UserPO user){
		JSONObject tmp = new JSONObject();
		tmp.accumulate("userID", user.getID());
		return tmp.toString();
	}
}
