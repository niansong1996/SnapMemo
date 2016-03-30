package org.sensation.snapmemo.server.BusinessLogic;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

import org.sensation.snapmemo.server.Data.UserData;
import org.sensation.snapmemo.server.PO.UserPO;
import org.sensation.snapmemo.server.Utility.IntStringWrapper;
import org.sensation.snapmemo.server.Utility.ResponseCodeInterpreter;
import org.sensation.snapmemo.server.Utility.UtilityTools;

import net.sf.json.JSONObject;

public class UserExecutor {
	private UserData data;
	UserExecutor(){
		this.data = new UserData();
	}
	public IntStringWrapper SignIn(InputStream is){
		String source = UtilityTools.Stream2String(is);
		String userName = this.getAttribute(source, "userName");
		UserPO user = data.findUserByName(userName);
		if(user==null){
			return this.getBadResponse();
		}
		if(user.getPassword().equals(this.getAttribute(source, "password")))
			return new IntStringWrapper(HttpURLConnection.HTTP_OK,this.getResponseJSON(user));
		else
			return new IntStringWrapper(HttpURLConnection.HTTP_FORBIDDEN,ResponseCodeInterpreter.getExplain(HttpURLConnection.HTTP_FORBIDDEN));
	}
	public IntStringWrapper GetLogo(InputStream is){
		String source = UtilityTools.Stream2String(is);
		UserPO user = this.getUser(source);
		if(user==null) return new IntStringWrapper(HttpURLConnection.HTTP_NOT_FOUND,"your user is not found");
		byte[] logo = UtilityTools.location2Img(user.getLogoLocation());
		return new IntStringWrapper(HttpURLConnection.HTTP_OK,logo);
	}
	public IntStringWrapper GetUserInfo(InputStream is){
		String source = UtilityTools.Stream2String(is);
		UserPO user = this.getUser(source);
		JSONObject tmp = new JSONObject();
		tmp.accumulate("signature", user.getSignature());
		return new IntStringWrapper(HttpURLConnection.HTTP_OK,tmp.toString());
	}
	public IntStringWrapper SignUp(InputStream is){
		String source = UtilityTools.Stream2String(is);
		String userName = this.getAttribute(source, "userName");
		String password = this.getAttribute(source, "password");
		UserPO user = new UserPO(userName,password);
		data.addUser(user);
		UserPO tmp = data.findUserByName(userName);
		String repString = "{\"userID\":\""+tmp.getID()+"\"}";
		return new IntStringWrapper(HttpURLConnection.HTTP_OK,repString);
	}
	
	private IntStringWrapper getBadResponse(){
		return new IntStringWrapper(HttpURLConnection.HTTP_NOT_FOUND,ResponseCodeInterpreter.getExplain(HttpURLConnection.HTTP_NOT_FOUND));
	}
	private UserPO getUser(String source){
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
