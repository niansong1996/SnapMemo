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
		httpHandler = new HttpRequestHandler(data);
		
	}
	public void activate(){
		httpHandler.startHandle();
		System.out.println("Server is ready.");
	}
}
