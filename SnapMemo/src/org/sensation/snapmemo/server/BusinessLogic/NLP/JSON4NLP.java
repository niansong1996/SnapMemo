package org.sensation.snapmemo.server.BusinessLogic.NLP;

import org.sensation.snapmemo.server.PO.MemoPO;

public class JSON4NLP {
	String PO2JSON(MemoPO po){
		return "{\"topic\":\"test\",\"date\":\"2016-02-09\",\"content\":\""+po.getContent()+"\"}";
	}
}
