package org.sensation.snapmemo.server.BusinessLogic;

import org.sensation.snapmemo.server.BusinessLogic.NLP.NLPModule;
import org.sensation.snapmemo.server.BusinessLogic.OCR.OCRController;
import org.sensation.snapmemo.server.Utility.IntStringWrapper;
import org.sensation.snapmemo.server.Utility.Request;
import org.sensation.snapmemo.server.Utility.RequestQueue;
import org.sensation.snapmemo.server.Utility.Response;
import org.sensation.snapmemo.server.Utility.ResponseQueue;

public class BLExecutor implements Runnable{
	OCRController ocr;
	NLPModule nlp;
	public BLExecutor(){
		this.ocr = new OCRController();
		this.nlp = new NLPModule();
	}
	private void execute(Request request){
		if(request==null) return;
		switch(request.type){
		case ResolveImage: resolveImage(request);break;
		default : System.err.println("invalid request in executor!");break;
		}
	}
	private void resolveImage(Request request){
		byte[] img = OCRController.InputStream2Img(request.is);
		IntStringWrapper wrapper = ocr.getOCRResult(img);
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

	@Override
	public void run() {
		Request request = null;
		while(true){
			synchronized(this){
				if(!RequestQueue.isEmpty()){
					request = RequestQueue.get();
				}
			}
			execute(request);
			request = null;
		}
	}
}
