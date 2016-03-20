package org.sensation.snapmemo.server.Utility;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.imageio.ImageIO;

public class UtilityTools {
	public static String Cal2String(Object cal){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return sdf.format(((Calendar)cal).getTime());
	}

	public static Calendar String2Cal(String s){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Calendar cal = Calendar.getInstance();
		try {
			cal.setTime(sdf.parse(s));
		} catch (ParseException e) {System.out.println("parse failed!!!");}
		return cal;
	}
	public static String getCurrentTime(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSSS");
		return sdf.format(new Date());
	}
	public static String Stream2String(InputStream stream){
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		String result = "";
		String tmp = "";
		try {
			while(tmp!=null){
				result += tmp;
				tmp = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	public static byte[] location2Img(String logoLocation){
		File f = new File(logoLocation);           
		BufferedImage bi;    
		try {    
			bi = ImageIO.read(f);    
			ByteArrayOutputStream baos = new ByteArrayOutputStream();    
			ImageIO.write(bi, "jpg", baos);    
			return baos.toByteArray();
		} catch (IOException e) {    
			e.printStackTrace();    
		}    
		return null;    
	}
	public static String byte2String(byte[] b){
		String responseString = "";
		try {
			responseString = new String(b,"UTF-8");
		} catch (UnsupportedEncodingException e1) {
			responseString = "It is an image";
		}
		return responseString;
	}
}
