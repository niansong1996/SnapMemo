package org.sensation.snapmemo.server.BusinessLogic.NLP;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.sensation.snapmemo.server.BusinessLogicService.NLPModuleService;
import org.sensation.snapmemo.server.PO.MemoPO;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

public class NLPModule implements NLPModuleService{
	JSON4NLP json;
	public NLPModule(){
		this.json = new JSON4NLP();
	}
	public void extractInfomation_Stub(String sentence){

		CloseableHttpClient httpclient = HttpClients.createDefault();  
		try{
			URIBuilder builder = new URIBuilder("https://api.projectoxford.ai/luis/v1/application");
			
//			builder.addParameter("id","c27c4af7-d44a-436f-a081-841bb834fa29");
//			builder.addParameter("subscription-key","545d95b9cdd84b0190161feca8b6bddf");
			builder.addParameter("id","e8ba6de1-02ca-41ec-b7a1-25f6452b448f");
			builder.addParameter("subscription-key","545d95b9cdd84b0190161feca8b6bddf");
			builder.addParameter("q", sentence);
			URI uri = builder.build();
			HttpGet request = new HttpGet(uri);
//			request.setHeader("Content-Type", "application/octet-stream");
//			request.setHeader("Ocp-Apim-Subscription-Key", "19d222efe0a543ff880ee2b56f0766df");


			// Request body
//			ByteArrayEntity reqEntity = new ByteArrayEntity(img);
//			request.setEntity(reqEntity);
			
			
			HttpResponse response = httpclient.execute(request);
			
			HttpEntity entity = response.getEntity();

			if (entity != null){
				String result = EntityUtils.toString(entity);
				System.out.println(result);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public MemoPO extractInfomation(String sentence){
		MemoPO result = new MemoPO();
		result.setContent(sentence);
		return result;
	}
	public String PO2JSON(MemoPO po){
		return json.PO2JSON(po);
	}
	public static void main(String[] args){
		NLPModule nlp = new NLPModule();
		nlp.extractInfomation_Stub("我们明天9点去吃饭");
	}
	
}
