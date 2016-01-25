package httpDemo;

import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class httpClient {
	public static void main(String[] args) 
	{
		CloseableHttpClient httpclient = HttpClients.createDefault();  
		try
		{
			URIBuilder builder = new URIBuilder("http://127.0.0.1:5678/SPhare");
			URI uri = builder.build();
			HttpPost request = new HttpPost(uri);
			// Request body
			System.out.println("sending the request");
			request.setHeader("Request-Type", "Set-Free-Time");
			StringEntity entity1 = new StringEntity("0000");
			request.setEntity(entity1);
			HttpResponse response = httpclient.execute(request);
			System.out.println("response code is "+response.getStatusLine().getStatusCode());
			HttpEntity entity = response.getEntity();
			String result = "";
			if (entity != null) 
			{
				result = EntityUtils.toString(entity);
			}
			System.out.println(result);

		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}    
}
