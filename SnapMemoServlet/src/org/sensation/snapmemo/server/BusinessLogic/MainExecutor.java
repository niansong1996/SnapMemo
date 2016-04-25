package org.sensation.snapmemo.server.BusinessLogic;

import java.io.InputStream;
import java.net.HttpURLConnection;

import org.sensation.snapmemo.server.BusinessLogic.ABD.ABDModule;
import org.sensation.snapmemo.server.BusinessLogic.NLP.NLPModule;
import org.sensation.snapmemo.server.BusinessLogic.OCR.OCRController;
import org.sensation.snapmemo.server.PO.MemoPO;
import org.sensation.snapmemo.server.Utility.IntStringWrapper;
import org.sensation.snapmemo.server.Utility.ResponseCodeInterpreter;
import org.sensation.snapmemo.server.Utility.UtilityTools;

public class MainExecutor {
	OCRController ocr;
	NLPModule nlp;
	ABDModule abd;
	public MainExecutor(){
		this.ocr = new OCRController();
		this.nlp = new NLPModule();
		this.abd = new ABDModule();
	}
	IntStringWrapper resolveImage(InputStream is,int x,int y){
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

		String selectedText = "";
		if(x!=-1&&y!=-1)
			selectedText = abd.getSelectedText(result.getInfo(),x,y);
		else
			selectedText = abd.getSelectedText(result.getInfo());

		MemoPO memo = nlp.extractInfomation(selectedText);

		if(memo==null) return new IntStringWrapper(HttpURLConnection.HTTP_INTERNAL_ERROR,ResponseCodeInterpreter.getExplain(HttpURLConnection.HTTP_INTERNAL_ERROR));
		String info = nlp.PO2JSON(memo);
		return new IntStringWrapper(HttpURLConnection.HTTP_OK,info);
	}
}
