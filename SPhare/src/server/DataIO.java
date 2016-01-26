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
import common.UserPO;
import net.sf.json.JSONObject;

public class DataIO implements DataService{
	public final String location = "data.ser";
	public ArrayList<GroupPO> data = new ArrayList<GroupPO>();
	
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
			for(GroupPO po : data){
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
				GroupPO po = (GroupPO)ois.readObject();
				data.add(po);
			}
			ois.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	@Override
	public String getGroupJSON(String groupID) {
		GroupPO result = null;
		for(GroupPO po : data){
			if(po.getGroupID().equals(groupID)) result = po;
		}
		if(result == null) return null;
		JSONObject json = JSONObject.fromObject(result);
		return json.toString();
	}
	@Override
	public boolean setFreeTime(String request) {
		System.out.println("request is "+request);
		JSONObject json = JSONObject.fromObject(request);
		String group = json.getString("groupID");
		String user = json.getString("userID");
		String date = JSONObject.fromObject(json.get("freeTime")).getString("date");
		String timePeriod = JSONObject.fromObject(json.get("freeTime")).getString("timePeriod");
		boolean success = false;
		for(int i=0;i<data.size();i++){
			GroupPO po = data.get(i);
			if(po.getGroupID().equals(group)){
				for(int j=0;j<po.getUserID().size();j++){
					UserPO userpo = po.getUserID().get(j);
					if(userpo.getName().equals(user)){
						success = true;
						for(int k=0;k<userpo.getFreeTime().size();k++){
							FreeTimePO freepo = userpo.getFreeTime().get(k);
							if(freepo.getDate().equals(date)) freepo.getTimePeriod().add(timePeriod);
							else{
								ArrayList<String> tmp = new ArrayList<String>();
								tmp.add(timePeriod);
								userpo.getFreeTime().add(new FreeTimePO(date,tmp));
							}
						}
					}
				}
			}
		}
		return success;
	}
	@Override
	public boolean deleteFreeTime(String request) {
		System.out.println("request is "+request);
		JSONObject json = JSONObject.fromObject(request);
		String group = json.getString("groupID");
		String user = json.getString("userID");
		String date = JSONObject.fromObject(json.get("freeTime")).getString("date");
		String timePeriod = JSONObject.fromObject(json.get("freeTime")).getString("timePeriod");
		boolean success = false;
		for(int i=0;i<data.size();i++){
			GroupPO po = data.get(i);
			if(po.getGroupID().equals(group)){
				for(int j=0;j<po.getUserID().size();j++){
					UserPO userpo = po.getUserID().get(j);
					if(userpo.getName().equals(user)){
						for(int k=0;k<userpo.getFreeTime().size();k++){
							FreeTimePO freepo = userpo.getFreeTime().get(k);
							if(freepo.getDate().equals(date)){
								for(int m=0;m<freepo.getTimePeriod().size();m++){
									if(freepo.getTimePeriod().get(m).equals(timePeriod)){
										success = true;
										freepo.getTimePeriod().remove(m);
									}
								}
							}
						}
					}
				}
			}
		}
		return success;
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
		Map<String,Class> classMap = new HashMap<String,Class>();
		classMap.put("userID", UserPO.class);
		classMap.put("freeTime", FreeTimePO.class);
		JSONObject jo = JSONObject.fromObject(json);
		System.out.println(jo.getString("userID"));
		GroupPO po = (GroupPO) JSONObject.toBean(jo, GroupPO.class,classMap);
		DataIO io = new DataIO();
		io.data.add(po);
		io.store();
	}
	
}
