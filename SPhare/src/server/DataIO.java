package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import common.FreeTimePO;
import common.GroupPO;
import common.TimeOccupancyPO;
import common.UserPO;
import net.sf.json.JSONObject;

public class DataIO implements DataService{
	public final String location = "data.ser";
	public ArrayList<TimeOccupancyPO> data = new ArrayList<TimeOccupancyPO>();
	
	public DataIO(){
		File f = new File(location);
		if(!f.exists()){
			try {f.createNewFile();} catch (IOException e) {e.printStackTrace();}
			this.store();
		}
		this.read();
	}
	private void store(){
		try {
			FileOutputStream fos = new FileOutputStream(new File(location));
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeInt(data.size());
			for(TimeOccupancyPO po : data){
				oos.writeObject(po);
			}
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void read(){
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(location)));
			int size = ois.readInt();
			for(int i=0;i<size;i++){
				TimeOccupancyPO po = (TimeOccupancyPO)ois.readObject();
				data.add(po);
			}
			ois.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	public String getAll(){
		String date = "2016-1-23";
		ArrayList<String> time = new ArrayList<String>();
		time.add("10:00-12:00");
		time.add("22:30-23:30");
		FreeTimePO po = new FreeTimePO(date,time);
		JSONObject array = JSONObject.fromObject(po);
	    String jsonstr = array.toString();
		return jsonstr;
	}
	public void add(TimeOccupancyPO po){
		this.data.add(po);
		this.store();
	}
	public static void main(String[] args){
		String json = "";
		String tmp = "";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File("sample.json")));
			do{
				json += tmp;
				tmp = reader.readLine();
			}while(tmp!=null);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(json);
		
		Map<String,Class> classMap = new HashMap<String,Class>();
		classMap.put("userID", UserPO.class);
		classMap.put("freeTime", FreeTimePO.class);
		JSONObject jo = JSONObject.fromObject(json);
		GroupPO po = (GroupPO) JSONObject.toBean(jo, GroupPO.class,classMap);
		System.out.println(po.getUserID().get(0).getFreeTime().get(0).getDate());
		
	}
	@Override
	public String getGroupJSON(String groupID) {
		String json = "";
		String tmp = "";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File("sample.json")));
			do{
				json += tmp;
				tmp = reader.readLine();
			}while(tmp!=null);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return json;
	}
	@Override
	public boolean setFreeTime(String request) {
		return true;
	}
}
