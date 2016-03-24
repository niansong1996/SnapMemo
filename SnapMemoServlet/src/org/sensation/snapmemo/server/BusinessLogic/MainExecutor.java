package org.sensation.snapmemo.server.BusinessLogic;

import java.io.InputStream;
import java.net.HttpURLConnection;

import org.sensation.snapmemo.server.BusinessLogic.NLP.NLPModule;
import org.sensation.snapmemo.server.BusinessLogic.OCR.OCRController;
import org.sensation.snapmemo.server.PO.MemoPO;
import org.sensation.snapmemo.server.Utility.IntStringWrapper;
import org.sensation.snapmemo.server.Utility.ResponseCodeInterpreter;
import org.sensation.snapmemo.server.Utility.UtilityTools;

public class MainExecutor {
	OCRController ocr;
	NLPModule nlp;
	public MainExecutor(){
		this.ocr = new OCRController();
		this.nlp = new NLPModule();
	}
	IntStringWrapper resolveImage(InputStream is){
		System.out.println("Resolve Image Begin @ "+UtilityTools.getCurrentTime());

		//Get the image in byte[] from InputSream of HttpExchange
		byte[] img = OCRController.InputStream2Img(is);

		//Get the result of Oxford OCR, info contains result JSON or error message;
		//depending on the response code.
		IntStringWrapper result = ocr.getOCRResult(img);
		System.out.println("JSON Module responsed @ "+UtilityTools.getCurrentTime());

		if(result.getCode()!=200){
			return new IntStringWrapper(result.getCode(),ResponseCodeInterpreter.getExplain(result.getCode()));
		}
		MemoPO memo = nlp.extractInfomation(result.getInfo());
		if(memo==null) return new IntStringWrapper(HttpURLConnection.HTTP_INTERNAL_ERROR,ResponseCodeInterpreter.getExplain(HttpURLConnection.HTTP_INTERNAL_ERROR));
		String info = nlp.PO2JSON(memo);
		return new IntStringWrapper(HttpURLConnection.HTTP_OK,info);
	}
}
