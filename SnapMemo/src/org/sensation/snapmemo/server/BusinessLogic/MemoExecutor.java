package org.sensation.snapmemo.server.BusinessLogic;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.sensation.snapmemo.server.Data.MemoData;
import org.sensation.snapmemo.server.PO.MemoPO;
import org.sensation.snapmemo.server.Utility.Request;
import org.sensation.snapmemo.server.Utility.Response;
import org.sensation.snapmemo.server.Utility.ResponseQueue;
import org.sensation.snapmemo.server.Utility.ResultMessage;
import org.sensation.snapmemo.server.Utility.UtilityTools;

import net.sf.json.JSONObject;

public class MemoExecutor {
	private MemoData data;
	MemoExecutor(){
		this.data = new MemoData();
	}
	public void GetMemoList(Request request){
		String userID = this.getUserID(request);
		JSONObject result = new JSONObject();
		ArrayList<MemoPO> list = new ArrayList<MemoPO>();
		Iterator<MemoPO> it = data.getMemoList(userID);
		while(it.hasNext()) list.add(it.next());
		result.accumulate("memo", list);
		ResponseQueue.put(new Response(request.exchange,result.toString(),HttpURLConnection.HTTP_OK));
	}
	public void DeleteMemo(Request request){
		String memoID = this.getMemoID(request);
		ResultMessage result = data.deleteMemo(memoID);
		this.sendResponse(request, result);
	}
	public void ModifyMemo(Request request){
		MemoPO memo = this.getMemoPO(request);
		ResultMessage result = data.updateMemo(memo);
		this.sendResponse(request, result);
	}
	public void SaveMemo(Request request){
		MemoPO memo = this.getMemoPO(request);
		ResultMessage result = data.updateMemo(memo);
		this.sendResponse(request, result);
	}
	
	private void sendResponse(Request request,ResultMessage result){
		if(result.isSuccess()) ResponseQueue.put(new Response(request.exchange,result.getMsg(),HttpURLConnection.HTTP_OK));
		else ResponseQueue.put(new Response(request.exchange,HttpURLConnection.HTTP_NOT_FOUND));
	}
	private String getUserID(Request request){
		String userID = JSONObject.fromObject(
				UtilityTools.Stream2String(request.is)
				).getString("userID");
		return userID;
	}
	private String getMemoID(Request request){
		String memoID = JSONObject.fromObject(
				UtilityTools.Stream2String(request.is)
				).getString("memoID");
		return memoID;
	}
	private MemoPO getMemoPO(Request request){
		String json = UtilityTools.Stream2String(request.is);
		return (MemoPO) JSONObject.toBean(JSONObject.fromObject(json));
	}
//	public static void main(String[] args){
//		JSONObject jo = new JSONObject();
//		List<MemoPO> list = new ArrayList<MemoPO>();
//		list.add(new MemoPO());
//		jo.accumulate("memo", list);
//		System.out.println(jo.toString());
//	}
	
}
