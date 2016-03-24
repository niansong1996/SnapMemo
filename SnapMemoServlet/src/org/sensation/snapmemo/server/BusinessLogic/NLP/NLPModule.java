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
	public MemoPO extractInfomation(String sentence){

		CloseableHttpClient httpclient = HttpClients.createDefault();  
		try{
			URIBuilder builder = new URIBuilder("https://api.projectoxford.ai/luis/v1/application");
			
			builder.addParameter("id","e8ba6de1-02ca-41ec-b7a1-25f6452b448f");
			builder.addParameter("subscription-key","545d95b9cdd84b0190161feca8b6bddf");
			builder.addParameter("q", sentence);
			URI uri = builder.build();
			HttpGet request = new HttpGet(uri);
			
			
			HttpResponse response = httpclient.execute(request);
			
			HttpEntity entity = response.getEntity();

			if (entity != null){
				String result = EntityUtils.toString(entity);
				System.out.println(result);
				return json.string2po(result);
			}

		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
//	public MemoPO extractInfomation(String sentence){
//		MemoPO result = new MemoPO();
//		result.setContent(sentence);
//		return result;
//	}
	public String PO2JSON(MemoPO po){
		return json.PO2JSON(po);
	}
	public static void main(String[] args){
		NLPModule nlp = new NLPModule();
		nlp.extractInfomation("我们明天9点去吃饭");
	}
	
}
