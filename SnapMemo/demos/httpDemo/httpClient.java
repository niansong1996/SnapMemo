package httpDemo;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

import javax.imageio.ImageIO;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class httpClient {

	public static void main(String[] args) {
		test1();
	}
	public static void test1(){
		CloseableHttpClient httpclient = HttpClients.createDefault();  
		try
		{
			URIBuilder builder = new URIBuilder("http://snapmemo.chinacloudapp.cn:5678/SnapMemo/servlet/main");
//			builder.addParameter("Touch-X","60");
//			builder.addParameter("Touch-Y", "235");
			URI uri = builder.build();
			System.out.println(uri.toURL().toString());
			HttpPost request = new HttpPost(uri);
			// Request body
			System.out.println("sending the request");
			request.setHeader("Request-Type", "Resolve-Image");
			request.setHeader("Touch-Location","80,235");
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
	public static void test2(){
		CloseableHttpClient httpclient = HttpClients.createDefault();  
		try
		{
			URIBuilder builder = new URIBuilder("http://snapmemo.chinacloudapp.cn:5678/SnapMemo/servlet/main");
			URI uri = builder.build();
			HttpPost request = new HttpPost(uri);
			// Request body
			System.out.println("sending the request");
			request.setHeader("Request-Type", "Get-Logo");
			StringEntity entity1 = new StringEntity("{\"userID\":\"000001\"}");
			request.setEntity(entity1);
			HttpResponse response = httpclient.execute(request);
			System.out.println("response code is "+response.getStatusLine().getStatusCode());
			HttpEntity entity = response.getEntity();
			String result = "";
			if (entity != null) 
			{
				writeImage(EntityUtils.toByteArray(entity));
			}
			System.out.println(result);

		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	public static void test3(){
		CloseableHttpClient httpclient = HttpClients.createDefault();  
		try
		{
			URIBuilder builder = new URIBuilder("http://snapmemo.chinacloudapp.cn/SnapMemo/servlet/main");
			URI uri = builder.build();
			HttpPost request = new HttpPost(uri);
			// Request body
			System.out.println("sending the request");
			request.setHeader("Request-Type", "Get-Memo-List");
			StringEntity entity1 = new StringEntity("{\"userID\":\"000001\"}");
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
	static void writeImage(byte[] src) throws IOException{
		byte[] b = src;//传来的图片信息byte数组

		String URL="D:\\3.jpg";

		File file=new File(URL);

		FileOutputStream fos=new FileOutputStream(file);

		fos.write(b,0,b.length);

		fos.flush();

		fos.close();
	}
}
