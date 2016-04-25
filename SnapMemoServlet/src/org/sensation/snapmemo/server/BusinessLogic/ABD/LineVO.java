package org.sensation.snapmemo.server.BusinessLogic.ABD;

import java.util.ArrayList;

import net.sf.json.JSONArray;

public class LineVO {
	int x;
	int y;
	int xlength;
	int ylength;
	String words;
	public LineVO(int x,int y,int xlength,int ylength,JSONArray ja){
		this.x = x;
		this.y = y;
		this.xlength = xlength;
		this.ylength = ylength;

		this.words = "";
		for(int i=0;i<ja.size();i++){
			words += ja.getJSONObject(i).getString("text");
		}
	}

	public boolean isIn(int x,int y){
		if(x>=this.x&&x<=(this.x+this.xlength))
			if(y>=this.y&&y<=(this.y+this.ylength))
				return true;
		return false;
	}

	public int calculateGap(LineVO other){
		//		System.out.println("This compared two one are: "
		//				+ this.words+"\n"+other.words);
		if(this.overlap(other)) return Integer.MAX_VALUE;
		int gap1 = Math.abs(this.y - (other.y+other.ylength));
		int gap2 = Math.abs((this.y+ylength)-other.y);

		return gap1<gap2?gap1:gap2;
	}

	private boolean overlap(LineVO other){
		if(other.y>this.y&&other.y<(this.y+this.ylength))
			if((other.y+other.ylength)>this.y&&(other.y+other.ylength)<(this.y+this.ylength))
				return true;
		return false;
	}
	public int getAbsoluteNearest(ArrayList<LineVO> source){
		int nearestIndex = -1;
		int minDis = Integer.MAX_VALUE;
		for(int i=0;i<source.size();i++){
			int currentDis = this.calculateGap(source.get(i));
			if(currentDis<minDis){
				nearestIndex = i;
				minDis = currentDis;
			}
		}
		return nearestIndex;
	}
	public static void findNearest(int index,int maxDis,ArrayList<LineVO> source,ArrayList<LineVO> result){
		int nearestIndex = -1;
		int minDis = Integer.MAX_VALUE;
	//	int maxDis = source.get(index).ylength;
		for(int i=0;i<source.size();i++){
			if(result.contains(source.get(i))) continue;

			int currentDis = source.get(i).calculateGap(source.get(index));
			if(currentDis<maxDis+5&&currentDis<minDis){
				nearestIndex = i;
				minDis = currentDis;
			}
		}
//		String nearLine = nearestIndex==-1?"nothing":source.get(nearestIndex).words;
//		System.out.println("The nearest line of \n"
//				+source.get(index).words+" \n"
//				+ "is : "+nearLine);
		if(nearestIndex!=-1){
			result.add(source.get(nearestIndex));
			findNearest(index,minDis,source,result);
			findNearest(nearestIndex,minDis,source,result);
		}
	}

	public static String aggregate(ArrayList<LineVO> source){
		sort(source);
		String result = "";
		for(int i=source.size()-1;i>=0;i--){
			System.out.println(source.get(i));
			result += source.get(i).words;
		}
		return result;
	}
	
	private static void sort(ArrayList<LineVO> source){
		for(int i=source.size();i>=0;i--){
			for(int j=1;j<i;j++){
				if(source.get(j).y>source.get(j-1).y){
					LineVO tmp = source.get(j);
					source.set(j, source.get(j-1));
					source.set(j-1, tmp);
				}
			}
		}
	}

	@Override
	public String toString(){
		return x+","+y+","+xlength+","+ylength;
	}

	@Override
	public boolean equals(Object obj){
		LineVO vo = (LineVO) obj;
		if(vo.x==this.x&&vo.y==this.y)
			if(vo.xlength==this.xlength&&vo.ylength==this.ylength)
				return true;
		return false;
	}
	

}
