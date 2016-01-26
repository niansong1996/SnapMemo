package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import common.TimeOccupancyPO;

public class HTMLHelper {
	private DataIO data;
	public HTMLHelper(DataIO data){
		this.data = data;
	}
	public String getTemplate(){
		String result = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("index.html")));
			while(br.ready()){
				result += br.readLine();
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	public String modifyHtml(String raw){
		//TODO
		String result = raw;
//		for(TimeOccupancyPO po : null){
//			String day = po.day;
//			int start = Integer.parseInt(po.startTime);
//			int end = Integer.parseInt(po.endTime);
//			for(int i=start;i<=end;i++){
//				System.out.println(""+day+i);
//				result = paint(po.severness,po.userName,""+day+i,result);
//			}
//		}
		
		return result;
	}
	public String paint(int severness,String name,String code,String raw){
		String color = "green";
		switch(severness){
		case 0: color = "yellow";break;
		case 1: color = "orange";break;
		case 2: color = "red";break;
		case 3: color = "black";break;
		default: color = "blue";break;
		}
		String old = "<td>"+code+"</td>";
		String nw = "<td bgcolor = \""+color+"\">"+name+"</td>";
		if(raw.contains(old)) System.out.println(old+" going to be replaced with "+nw);
		raw = raw.replaceAll(old, nw);
		return raw;
	}
	public String getResponseHtml(){
		String result = getTemplate();
		result = modifyHtml(result);
		return result;
	}
}
