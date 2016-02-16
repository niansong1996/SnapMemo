package org.sensation.snapmemo.server.Utility;

import java.util.Queue;

public class ResponseQueue {
	private static Queue<Response> queue;
	public ResponseQueue(){
		ResponseQueue.queue = new java.util.PriorityQueue<>();
	}
	public static void put(Response response){
		ResponseQueue.queue.add(response);
//		System.out.println("ResponseQueue IN, Current size: "+queue.size());
	}
	public static Response get(){
//		System.out.println("ResponseQueue OUT, Current size: "+(queue.size()-1));
		return queue.poll();
	}
	public static boolean isEmpty(){
		return queue.isEmpty();
	}
}
