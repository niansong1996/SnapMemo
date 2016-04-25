package org.sensation.snapmemo.server.BusinessLogic.OCR;

import org.sensation.snapmemo.server.Utility.IntStringWrapper;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class JSON4OCR {
	public IntStringWrapper getOCRResult(IntStringWrapper wrapper){
		String source = wrapper.getInfo();
		JSONObject sourceJObject = JSONObject.fromObject(source);
		if(sourceJObject.getString("orientation").equals("NotDetected")){
			return new IntStringWrapper(308,"Character not detected");
		}
		switch(wrapper.getCode()){
		case 200: return new IntStringWrapper(wrapper.getCode(),source);
		//case 200: return new IntStringWrapper(wrapper.getCode(),getSuccessResult(sourceJObject));
		case 400:
		case 415:
		case 500: return new IntStringWrapper(wrapper.getCode(),getFailedResult(sourceJObject));
		default: return new IntStringWrapper(300,"Unknown Error");
		}
	}
	private String getSuccessResult(JSONObject sourceJObject) {
		JSONArray lines = sourceJObject.getJSONArray("regions").getJSONObject(0).getJSONArray("lines");
		String result = "";
		for(int i=0;i<lines.size();i++){
			String oneLine = "";
			JSONArray words = lines.getJSONObject(i).getJSONArray("words");
			for(int j=0;j<words.size();j++){
				String word = words.getJSONObject(j).getString("text");
				if(word.equals("\""))
					word = "\\\"";
				oneLine += word;
			}
			result+= oneLine;
		}
		return result;
	}
	private String getFailedResult(JSONObject sourceJObject){
		String code = sourceJObject.getString("code");
		String message = sourceJObject.getString("message");
		return code+","+message;
	}
}
