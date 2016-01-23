package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import common.TimeOccupancyPO;

public class DataIO {
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
		JSONArray array = new JSONArray(data);
		String source = "[{\"startTime\":\"1\",\"endTime\":\"1\",\"userName\":\"Your_Name\",\"severness\":0},{\"startTime\":\"1\",\"endTime\":\"1\",\"userName\":\"Your_Name\",\"severness\":0},{\"startTime\":\"1\",\"endTime\":\"1\",\"userName\":\"Your_Name\",\"severness\":0},{\"startTime\":\"1\",\"endTime\":\"1\",\"userName\":\"Your_Name\",\"severness\":0},{\"startTime\":\"1\",\"endTime\":\"1\",\"userName\":\"Your_Name\",\"severness\":0},{\"startTime\":\"5\",\"endTime\":\"7\",\"userName\":\"Anthony\",\"severness\":0},{\"startTime\":\"5\",\"endTime\":\"7\",\"userName\":\"Anthony\",\"severness\":0},{\"startTime\":\"8\",\"endTime\":\"11\",\"userName\":\"Alan\",\"severness\":2},{\"startTime\":\"8\",\"endTime\":\"11\",\"userName\":\"Alan\",\"severness\":2},{\"startTime\":\"8\",\"endTime\":\"11\",\"userName\":\"Alan\",\"severness\":2},{\"startTime\":\"8\",\"endTime\":\"11\",\"userName\":\"Alan\",\"severness\":2},{\"startTime\":\"8\",\"endTime\":\"11\",\"userName\":\"Alan\",\"severness\":2}]";
		try {
			JSONObject a = new JSONObject(source);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    String jsonstr = array.toString();
		return jsonstr;
	}
	public void toObject(){
		String source = "[{\"startTime\":\"1\",\"endTime\":\"1\",\"userName\":\"Your_Name\",\"severness\":0},{\"startTime\":\"1\",\"endTime\":\"1\",\"userName\":\"Your_Name\",\"severness\":0},{\"startTime\":\"1\",\"endTime\":\"1\",\"userName\":\"Your_Name\",\"severness\":0},{\"startTime\":\"1\",\"endTime\":\"1\",\"userName\":\"Your_Name\",\"severness\":0},{\"startTime\":\"1\",\"endTime\":\"1\",\"userName\":\"Your_Name\",\"severness\":0},{\"startTime\":\"5\",\"endTime\":\"7\",\"userName\":\"Anthony\",\"severness\":0},{\"startTime\":\"5\",\"endTime\":\"7\",\"userName\":\"Anthony\",\"severness\":0},{\"startTime\":\"8\",\"endTime\":\"11\",\"userName\":\"Alan\",\"severness\":2},{\"startTime\":\"8\",\"endTime\":\"11\",\"userName\":\"Alan\",\"severness\":2},{\"startTime\":\"8\",\"endTime\":\"11\",\"userName\":\"Alan\",\"severness\":2},{\"startTime\":\"8\",\"endTime\":\"11\",\"userName\":\"Alan\",\"severness\":2},{\"startTime\":\"8\",\"endTime\":\"11\",\"userName\":\"Alan\",\"severness\":2}]";
	}
	public void add(TimeOccupancyPO po){
		this.data.add(po);
		this.store();
	}
	public static void main(String[] args){
		DataIO data = new DataIO();
		System.out.println(data.getAll());
	}
}
