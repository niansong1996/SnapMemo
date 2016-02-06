package org.sensation.snapmemo.server.BusinessLogic;

import org.sensation.snapmemo.server.Utility.Request;
import org.sensation.snapmemo.server.Utility.RequestQueue;
import org.sensation.snapmemo.server.Utility.Response;
import org.sensation.snapmemo.server.Utility.ResponseQueue;

public class BLExecutor implements Runnable{
	OCRModule ocr;
	NLPModule nlp;
	JSONModule json;
	public BLExecutor(){
		this.ocr = new OCRModule();
		this.nlp = new NLPModule();
		this.json = new JSONModule();
	}
	private void execute(Request request){
		if(request==null) return;
		switch(request.type){
		case ResolveImage: resolveImage(request);break;
		default : System.err.println("invalid request in executor!");break;
		}
	}
	private void resolveImage(Request request){
		byte[] img = OCRModule.InputStream2Img(request.is);
		String info = ocr.Img2String(img);
		Response response = new Response(request.exchange,info,200);
		ResponseQueue.put(response);
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
