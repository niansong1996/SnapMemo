package server;

public interface DataService {
	
public String getGroupJSON(String groupID);
public boolean setFreeTime(String request);
public boolean deleteFreeTime(String request);
}
