package org.sensation.snapmemo.server.BusinessLogic.NLP;

import java.util.Calendar;

import org.sensation.snapmemo.server.PO.MemoPO;
import org.sensation.snapmemo.server.Utility.UtilityTools;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class JSON4NLP {
	public String PO2JSON(MemoPO po){
		String time= UtilityTools.Cal2String(po.getTime());
		return "{\"topic\":\""+
		po.getTopic()+"\",\"time\":\""+time+"\",\"content\":\""+po.getContent()+"\"}";
	}
	MemoPO string2po(String source){
		JSONObject jo = JSONObject.fromObject(source);
		MemoPO memo = new MemoPO();
		JSONArray intentJA = jo.getJSONArray("intents");
		JSONArray entityJA = jo.getJSONArray("entities");
		String content = jo.getString("query");
		String[] tmp = UtilityTools.Cal2String(Calendar.getInstance()).split(" ");
		String date = tmp[0];
		String time = tmp[1];
		String topic = intentJA.getJSONObject(0).getString("intent").toString()+":";
		for(int i=0;i<entityJA.size();i++){
			JSONObject temp = entityJA.getJSONObject(i);
			switch(temp.getString("type")){
			case "event": topic += temp.getString("entity");break;
			case "builtin.datetime.date": date = temp.getJSONObject("resolution").getString("date");break;
			case "builtin.datetime.time": time = temp.getJSONObject("resolution").getString("time");break;
			default: break;
			}
		}
		memo.setContent(content);
		memo.setTime(UtilityTools.Luis2Cal(date,time));
		memo.setTopic(topic);
		return memo;
	}
}
