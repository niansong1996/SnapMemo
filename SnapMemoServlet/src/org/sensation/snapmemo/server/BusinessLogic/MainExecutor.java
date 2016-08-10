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
		System.out.println("Resolve Image End @ "+UtilityTools.getCurrentTime());
		System.out.println("Oxford-OCR response code is : "+result.getCode());
		

		if(result.getCode()!=200){
			System.out.println("ERROR: OCR can't do, response "+result.getCode());
			return new IntStringWrapper(result.getCode(),ResponseCodeInterpreter.getExplain(result.getCode()));
		}
		
		System.out.println("Boundary detect begin @ "+UtilityTools.getCurrentTime());
		String selectedText = "";
		if(x!=-1&&y!=-1)
			selectedText = abd.getSelectedText(result.getInfo(),x,y);
		else
			selectedText = abd.getSelectedText(result.getInfo());
		System.out.println("Boundary detect End @ "+UtilityTools.getCurrentTime());
		System.out.println("ABD result is "+selectedText);
		
		System.out.println("NLP module start @ "+UtilityTools.getCurrentTime());
		MemoPO memo = nlp.extractInfomation(selectedText);

		if(memo==null){
			System.out.println("ERROR: can't do NLP with the selectedText, response 500");
			return new IntStringWrapper(HttpURLConnection.HTTP_INTERNAL_ERROR,ResponseCodeInterpreter.getExplain(HttpURLConnection.HTTP_INTERNAL_ERROR));
		}
		String info = nlp.PO2JSON(memo);
		System.out.println("SUCCESS: all good to go, ready to send");
		return new IntStringWrapper(HttpURLConnection.HTTP_OK,info);
	}
}
