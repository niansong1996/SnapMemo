package httpDemo;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
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
import org.apache.http.util.EntityUtils;

public class httpClient {
	public static void main(String[] args) 
	{
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
		File f = new File("D:\\2.jpg");           
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
