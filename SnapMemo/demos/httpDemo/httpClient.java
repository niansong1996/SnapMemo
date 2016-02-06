package httpDemo;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;

import javax.imageio.ImageIO;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class httpClient {
	public static void main(String[] args) 
	{
		String json = "";
		String tmp = "";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File("sampleSend.json")));
			do{
				json += tmp;
				tmp = reader.readLine();
			}while(tmp!=null);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		CloseableHttpClient httpclient = HttpClients.createDefault();  
		try
		{
			URIBuilder builder = new URIBuilder("http://127.0.0.1:5678/SnapMemo");
			URI uri = builder.build();
			HttpPost request = new HttpPost(uri);
			// Request body
			System.out.println("sending the request");
			request.setHeader("Request-Type", "Resolve-Image");
			ByteArrayEntity entity1 = new ByteArrayEntity(getImageBinary());
			request.setEntity(entity1);
			HttpResponse response = httpclient.execute(request);
			System.out.println("response code is "+response.getStatusLine().getStatusCode());
			HttpEntity entity = response.getEntity();
			String result = "";
			if (entity != null) 
			{
				result = EntityUtils.toString(entity,"UTF-8");
			}
			System.out.println(result);

		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}   
	static byte[] getImageBinary(){    
		File f = new File("D:\\1.jpg");           
		BufferedImage bi;    
		try {    
			bi = ImageIO.read(f);    
			ByteArrayOutputStream baos = new ByteArrayOutputStream();    
			ImageIO.write(bi, "jpg", baos);    
			byte[] bytes = baos.toByteArray();   
			return bytes;
//			return encoder.encodeBuffer(bytes).trim();    
		} catch (IOException e) {    
			e.printStackTrace();    
		}    
		return null;    
	}    
}
