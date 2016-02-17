package org.sensation.snapmemo.server.BusinessLogic;

import org.sensation.snapmemo.server.BusinessLogic.NLP.NLPModule;
import org.sensation.snapmemo.server.BusinessLogic.OCR.OCRController;
import org.sensation.snapmemo.server.Utility.IntStringWrapper;
import org.sensation.snapmemo.server.Utility.Request;
import org.sensation.snapmemo.server.Utility.Response;
import org.sensation.snapmemo.server.Utility.ResponseQueue;
import org.sensation.snapmemo.server.Utility.UtilityTools;

public class MainExecutor {
	OCRController ocr;
	NLPModule nlp;
	public MainExecutor(){
		this.ocr = new OCRController();
		this.nlp = new NLPModule();
	}
	void resolveImage(Request request){
		System.out.println("Resolve Image Begin @ "+UtilityTools.getCurrentTime());
		byte[] img = OCRController.InputStream2Img(request.is);
		IntStringWrapper wrapper = ocr.getOCRResult(img);
		System.out.println("JSON Module responsed @ "+UtilityTools.getCurrentTime());
		if(wrapper.getCode()==200){
			String info = wrapper.getInfo();
			info = "{\"topic\":\"test\",\"date\":\"2016-02-09\",\"content\":\""+info+"\"}";
			Response response = new Response(request.exchange,info,200);
			ResponseQueue.put(response);
		}else{
			Response response = new Response(request.exchange,wrapper.getInfo(),wrapper.getCode());
			ResponseQueue.put(response);
		}
	}
}
