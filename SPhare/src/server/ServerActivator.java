package server;

public class ServerActivator {
	public static void main(String args[]){
		ServerActivator activator = new ServerActivator();
		activator.activate();
	}
	
	DataIO data;
	HttpRequestHandler httpHandler;
	public ServerActivator(){
		data = new DataIO();
		httpHandler = new HttpRequestHandler();
		
	}
	public void activate(){
		httpHandler.startHandle();
	}
}
