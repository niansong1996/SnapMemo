package org.sensation.snapmemo.server.BusinessLogic.OCR;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.sensation.snapmemo.server.BusinessLogicService.OCRModuleService;
import org.sensation.snapmemo.server.Utility.IntStringWrapper;

public class OCRController implements OCRModuleService{
	private JSON4OCR json;
	public OCRController(){
		this.json = new JSON4OCR();
	}
	public IntStringWrapper getOCRResult(byte[] img){
		IntStringWrapper OCRResponse = OxfordOCR(img);
		return json.getOCRResult(OCRResponse);
	}
	private IntStringWrapper OxfordOCR(byte[] img){
		CloseableHttpClient httpclient = HttpClients.createDefault();  
		try{
			URIBuilder builder = new URIBuilder("https://api.projectoxford.ai/vision/v1/ocr");

			builder.setParameter("language", "unk");
			builder.setParameter("detectOrientation ", "true");

			URI uri = builder.build();
			HttpPost request = new HttpPost(uri);
			request.setHeader("Content-Type", "application/octet-stream");
			request.setHeader("Ocp-Apim-Subscription-Key", "19d222efe0a543ff880ee2b56f0766df");


			// Request body
			ByteArrayEntity reqEntity = new ByteArrayEntity(img);
			request.setEntity(reqEntity);
			
			
			HttpResponse response = httpclient.execute(request);
			
			HttpEntity entity = response.getEntity();

			if (entity != null){
				String result = EntityUtils.toString(entity);
				System.out.println(result);
				return new IntStringWrapper(response.getStatusLine().getStatusCode(),result);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new IntStringWrapper(300,"Unknown Error");
	}
	public static byte[] InputStream2Img(InputStream is){
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream(); 
		byte[] buff = new byte[100]; 
		int rc = 0; 
		try {
			while ((rc = is.read(buff, 0, 100)) > 0) { 
			swapStream.write(buff, 0, rc); 
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return swapStream.toByteArray();  
	}
//	static byte[] getImageBinary(){    
//		File f = new File("D:\\1.jpg");           
//		BufferedImage bi;    
//		try {    
//			bi = ImageIO.read(f);    
//			ByteArrayOutputStream baos = new ByteArrayOutputStream();    
//			ImageIO.write(bi, "jpg", baos);    
//			byte[] bytes = baos.toByteArray();   
//			return bytes;
////			return encoder.encodeBuffer(bytes).trim();    
//		} catch (IOException e) {    
//			e.printStackTrace();    
//		}    
//		return null;    
//	}    
//	public static void main(String[] args){
//		OCRModule m = new OCRModule();
//		System.out.println(m.Img2String(getImageBinary()));
//	}
}
