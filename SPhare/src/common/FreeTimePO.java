package common;

import java.util.ArrayList;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

public class FreeTimePO {
	String date;
	ArrayList<String> timePeriod;
	public FreeTimePO(){}
	public FreeTimePO(String date,ArrayList<String> period){
		this.date = date;
		this.timePeriod = period;
	}
	@Id
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	@ElementCollection
	@CollectionTable(name="timePeriod", joinColumns=@JoinColumn(name="date"))
	@Column(name="period")
	public ArrayList<String> getTimePeriod() {
		return timePeriod;
	}
	public void setTimePeriod(ArrayList<String> timePeriod) {
		this.timePeriod = timePeriod;
	}
}
