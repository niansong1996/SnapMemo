package org.sensation.snapmemo.server.BusinessLogic;

import java.net.HttpURLConnection;

import org.sensation.snapmemo.server.BusinessLogic.NLP.NLPModule;
import org.sensation.snapmemo.server.BusinessLogic.OCR.OCRController;
import org.sensation.snapmemo.server.Utility.IntStringWrapper;
import org.sensation.snapmemo.server.Utility.Request;
import org.sensation.snapmemo.server.Utility.Response;
import org.sensation.snapmemo.server.Utility.ResponseCodeInterpreter;
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

		//Get the image in byte[] from InputSream of HttpExchange
		byte[] img = OCRController.InputStream2Img(request.is);

		//Get the result of Oxford OCR, info contains result JSON or error message;
		//depending on the response code.
		IntStringWrapper result = ocr.getOCRResult(img);
		System.out.println("JSON Module responsed @ "+UtilityTools.getCurrentTime());

		if(result.getCode()!=200){
			ResponseQueue.put(
					new Response(request.exchange,ResponseCodeInterpreter.getExplain(result.getCode()),result.getCode())
					);
			return;
		}
		String info = nlp.PO2JSON(nlp.extractInfomation(result.getInfo()));
		Response response = new Response(request.exchange,info,HttpURLConnection.HTTP_OK);
		ResponseQueue.put(response);

	}
}
