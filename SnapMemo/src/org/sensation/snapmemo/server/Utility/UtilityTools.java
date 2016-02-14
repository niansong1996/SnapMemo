package org.sensation.snapmemo.server.Utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
}
