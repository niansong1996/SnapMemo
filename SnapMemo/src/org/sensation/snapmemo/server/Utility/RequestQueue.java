package org.sensation.snapmemo.server.Utility;

import java.util.PriorityQueue;
import java.util.Queue;

public class RequestQueue{
	private static Queue<Request> queue;
	public RequestQueue(){
		RequestQueue.queue = new PriorityQueue<>();
	}
	public static void put(Request request ){
		RequestQueue.queue.add(request);
		System.out.println("RequestQueue IN, Current size: "+queue.size());
	}
	public static Request get(){
		System.out.println("RequestQueue OUT, Current size: "+(queue.size()-1));
		return queue.poll();
	}
	public static boolean isEmpty(){
		return queue.isEmpty();
	}
}
