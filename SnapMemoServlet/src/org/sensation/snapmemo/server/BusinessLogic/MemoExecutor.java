package org.sensation.snapmemo.server.BusinessLogic;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Iterator;

import org.sensation.snapmemo.server.Data.MemoData;
import org.sensation.snapmemo.server.PO.MemoPO;
import org.sensation.snapmemo.server.Utility.IntStringWrapper;
import org.sensation.snapmemo.server.Utility.ResponseCodeInterpreter;
import org.sensation.snapmemo.server.Utility.ResultMessage;
import org.sensation.snapmemo.server.Utility.UtilityTools;

import net.sf.json.JSONObject;

public class MemoExecutor {
	private MemoData data;
	MemoExecutor(){
		this.data = new MemoData();
	}
	public IntStringWrapper GetMemoList(InputStream is){
		String source = UtilityTools.Stream2String(is);
		String userID = this.getUserID(source);
		JSONObject result = new JSONObject();
		ArrayList<String> list = new ArrayList<String>();
		Iterator<MemoPO> it = data.getMemoList(userID);
		while(it.hasNext()) list.add(UtilityTools.PO2JSON(it.next()));
		result.accumulate("memo", list);
		return new IntStringWrapper(HttpURLConnection.HTTP_OK,result.toString());
	}
	public IntStringWrapper DeleteMemo(InputStream is){
		String source = UtilityTools.Stream2String(is);
		String memoID = this.getMemoID(source);
		ResultMessage result = data.deleteMemo(memoID);
		return this.getResponse(result);
	}
	public IntStringWrapper ModifyMemo(InputStream is){
		String source = UtilityTools.Stream2String(is);
		MemoPO memo = this.getMemoPO(source);
		ResultMessage result = data.updateMemo(memo);
		return this.getResponse(result);
	}
	public IntStringWrapper SaveMemo(InputStream is){
		String src = UtilityTools.Stream2String(is);
		JSONObject source = JSONObject.fromObject(src);
		String userID = source.getString("userID");
		String topic = source.getString("topic");
		String time = source.getString("time");
		String content = source.getString("content");
		MemoPO memo = new MemoPO(userID,topic,time,content);
		ResultMessage result = data.addMemo(memo);
		if(result.isSuccess()){
			String repString = "{\"memoID\":\""+memo.getMemoID()+"\"}";
			return new IntStringWrapper(HttpURLConnection.HTTP_OK,repString);
		}
		else return new IntStringWrapper(HttpURLConnection.HTTP_NOT_FOUND,
				ResponseCodeInterpreter.getExplain(HttpURLConnection.HTTP_NOT_FOUND));
	}
	
	private IntStringWrapper getResponse(ResultMessage result){
		if(result.isSuccess()) return new IntStringWrapper(HttpURLConnection.HTTP_OK,result.getMsg());
		else return new IntStringWrapper(HttpURLConnection.HTTP_NOT_FOUND,result.getMsg());
	}
	private String getUserID(String source){
		String userID = JSONObject.fromObject(source).getString("userID");
		return userID;
	}
	private String getMemoID(String source){
		String memoID = JSONObject.fromObject(source).getString("memoID");
		return memoID;
	}
	private MemoPO getMemoPO(String source){
		JSONObject json = JSONObject.fromObject(source);
		MemoPO memo = new MemoPO(null,
				json.getString("topic"),json.getString("time"),
				json.getString("content"));
		memo.setMemoID(json.getString("memoID"));
		return memo;
	}
//	public static void main(String[] args){
//		JSONObject jo = new JSONObject();
//		List<MemoPO> list = new ArrayList<MemoPO>();
//		list.add(new MemoPO());
//		jo.accumulate("memo", list);
//		System.out.println(jo.toString());
//	}
	
}
