package org.sensation.snapmemo.server.BusinessLogic.NLP;

import org.sensation.snapmemo.server.BusinessLogicService.NLPModuleService;
import org.sensation.snapmemo.server.PO.MemoPO;

public class NLPModule implements NLPModuleService{
	JSON4NLP json;
	public NLPModule(){
		this.json = new JSON4NLP();
	}
	public MemoPO extractInfomation(String sentence){
		MemoPO result = new MemoPO();
		result.setContent(sentence);
		return result;
	}
	public String PO2JSON(MemoPO po){
		return json.PO2JSON(po);
	}
	
}
