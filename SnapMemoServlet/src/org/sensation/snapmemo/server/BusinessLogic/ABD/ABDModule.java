package org.sensation.snapmemo.server.BusinessLogic.ABD;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.sensation.snapmemo.server.BusinessLogic.OCR.OCRController;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ABDModule {
	OCRController ocr = new OCRController();
	ArrayList<LineVO> bb = new ArrayList<LineVO>();
	public ABDModule(){

	}

	public String getSelectedText(String source){
		System.out.println("THis is the source : "+source);
		JSONObject sourceJObject = JSONObject.fromObject(source);
		JSONArray lines = sourceJObject.getJSONArray("regions").getJSONObject(0).getJSONArray("lines");
		String result = "";
		for(int i=0;i<lines.size();i++){
			String oneLine = "";
			JSONArray words = lines.getJSONObject(i).getJSONArray("words");
			for(int j=0;j<words.size();j++){
				String word = words.getJSONObject(j).getString("text");
				if(word.equals("\""))
					word = "\\\"";
				oneLine += word;
			}
			result+= oneLine;
		}
		return result;
	}
	public String getSelectedText(String source,int x,int y){
		ArrayList<LineVO> lineLocations = getLocations(source);
		
		int touchedLine = getTouchedLine(lineLocations,x,y);
		System.out.println("Touched Line is : "+touchedLine);
		
		if(touchedLine==-1){
			return "No character detected!";
		}
		
		ArrayList<LineVO> selectedLines = getSelectedLines(touchedLine,lineLocations);
		String result = LineVO.aggregate(selectedLines);
		return result;
	}
	private ArrayList<LineVO> getSelectedLines(int touchedLine,ArrayList<LineVO> lineLocations){

		ArrayList<LineVO> result = new ArrayList<LineVO>();
		
		LineVO initialLine = lineLocations.get(touchedLine);
		result.add(initialLine);
		int initialMaxDis = initialLine.ylength;
		LineVO.findNearest(touchedLine,initialMaxDis,lineLocations,result);
		return result;
	}
	private int getTouchedLine(ArrayList<LineVO> source,int x,int y){
		for(int i=0;i<source.size();i++){
			if(source.get(i).isIn(x, y))
				return i;
		}
		
		LineVO virtualLine = new LineVO(x,y,1,1,null);
		return virtualLine.getAbsoluteNearest(source);
		
	}
	
	public ArrayList<LineVO> getLocations(String ocrResult){
		ArrayList<LineVO> result = new ArrayList<LineVO>();
//		ocrResult = ocr.OxfordOCR(getImageBinary()).getInfo();
//		System.out.println(ocrResult);
		JSONObject jo = JSONObject.fromObject(ocrResult);
		JSONArray ja = jo.getJSONArray("regions");
		for(int i=0;i<ja.size();i++){
			JSONObject regions = ja.getJSONObject(i);
			JSONArray lines = regions.getJSONArray("lines");
			for(int j=0;j<lines.size();j++){
				JSONObject tmp = lines.getJSONObject(j);
				String[] boundingBox = tmp.getString("boundingBox").split(",");
				result.add(new LineVO(
						Integer.parseInt(boundingBox[0]),Integer.parseInt(boundingBox[1]),
						Integer.parseInt(boundingBox[2]),Integer.parseInt(boundingBox[3]),
						tmp.getJSONArray("words")
						));
			}
		}
		return result;
	}

	public static void main(String[] args){
		ABDModule abd = new ABDModule();
		
	    System.out.println(abd.getSelectedText("", 0, 0));
	}

	public byte[] getImageBinary(){    
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
