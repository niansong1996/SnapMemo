package org.sensation.snapmemo.server.BusinessLogic;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class JSONModule {
	public static String getOCRResult(String source){
		JSONObject sourceJObject = JSONObject.fromObject(source);
		JSONArray lines = sourceJObject.getJSONArray("regions").getJSONObject(0).getJSONArray("lines");
		String result = "";
		for(int i=0;i<lines.size();i++){
			String oneLine = "";
			JSONArray words = lines.getJSONObject(i).getJSONArray("words");
			for(int j=0;j<words.size();j++){
				oneLine += words.getJSONObject(j).getString("text");
			}
			result+= oneLine;
		}
		return result;
	}
}
