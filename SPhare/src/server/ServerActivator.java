package server;

public class ServerActivator {
	public static void main(String args[]){
		ServerActivator activator = new ServerActivator();
		activator.activate();
	}
	
	DataIO data;
	HTMLHelper html;
	HttpRequestHandler httpHandler;
	public ServerActivator(){
		data = new DataIO();
		html = new HTMLHelper(data);
		httpHandler = new HttpRequestHandler(data,html);
		
	}
	public void activate(){
		httpHandler.startHandle();
		System.out.println("Server is ready.");
	}
}
