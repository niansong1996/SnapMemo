package org.sensation.snapmemo.server.BusinessLogic;

import java.net.HttpURLConnection;

import org.sensation.snapmemo.server.Data.MySessionFactory;
import org.sensation.snapmemo.server.Data.UserData;
import org.sensation.snapmemo.server.PO.UserPO;
import org.sensation.snapmemo.server.Utility.Request;
import org.sensation.snapmemo.server.Utility.Response;
import org.sensation.snapmemo.server.Utility.ResponseCodeInterpreter;
import org.sensation.snapmemo.server.Utility.ResponseQueue;
import org.sensation.snapmemo.server.Utility.UtilityTools;

import net.sf.json.JSONObject;

public class UserExecutor {
	private UserData data;
	UserExecutor(){
		this.data = new UserData();
	}
	public void SignIn(Request request){
		String source = UtilityTools.Stream2String(request.is);
		String userName = this.getAttribute(source, "userName");
		UserPO user = data.findUserByName(userName);
		if(user==null){
			this.sendBadResponse(request);
			return;
		}
		if(user.getPassword().equals(this.getAttribute(source, "password")))
			ResponseQueue.put(new Response(request.exchange,this.getResponseJSON(user),HttpURLConnection.HTTP_OK));
		else
			ResponseQueue.put(new Response(request.exchange,HttpURLConnection.HTTP_FORBIDDEN));
	}
	public void GetLogo(Request request){
		UserPO user = this.getUser(request);
		if(user==null) ResponseQueue.put(new Response(request.exchange,"your user doesn't exits",HttpURLConnection.HTTP_BAD_REQUEST));
		byte[] logo = UtilityTools.location2Img(user.getLogoLocation());
		ResponseQueue.put(new Response(request.exchange,HttpURLConnection.HTTP_OK,logo));
	}
	public void GetUserInfo(Request request){
		UserPO user = this.getUser(request);
		JSONObject tmp = new JSONObject();
		tmp.accumulate("signature", user.getSignature());
		ResponseQueue.put(new Response(request.exchange,tmp.toString(),HttpURLConnection.HTTP_OK));
	}
	public void SignUp(Request request){
		String source = UtilityTools.Stream2String(request.is);
		String userName = this.getAttribute(source, "userName");
		String password = this.getAttribute(source, "password");
		UserPO user = new UserPO(userName,password);
		data.addUser(user);
		UserPO tmp = data.findUserByName(userName);
		String repString = "{\"userID\":\""+tmp.getID()+"\"}";
		ResponseQueue.put(new Response(request.exchange,repString,HttpURLConnection.HTTP_OK));
	}
	
	private void sendBadResponse(Request request){
		ResponseQueue.put(new Response(request.exchange,HttpURLConnection.HTTP_NOT_FOUND));
	}
	private UserPO getUser(Request request){
		String source = UtilityTools.Stream2String(request.is);
		String userID = this.getAttribute(source, "userID");
		UserPO user = data.findUserByID(userID);
		return user;
	}
	private String getAttribute(String source, String attribute){
		String userID = JSONObject.fromObject(source).getString(attribute);
		return userID;
	}
	private String getResponseJSON(UserPO user){
		JSONObject tmp = new JSONObject();
		tmp.accumulate("userID", user.getID());
		return tmp.toString();
	}
	public static void main(String[] args){
		String source = "{\"userName\":\"wrr\",\"password\":\"132\"}";
		System.out.println(source);
		JSONObject jo = JSONObject.fromObject(source);
		
	}
}
