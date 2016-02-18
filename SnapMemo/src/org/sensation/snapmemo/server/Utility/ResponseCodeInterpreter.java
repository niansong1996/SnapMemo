package org.sensation.snapmemo.server.Utility;

public class ResponseCodeInterpreter {
	private static String explain[];
	public ResponseCodeInterpreter(){
		explain = new String[600];
		construct();
	}
	private void construct(){
		explain[200] = "Success";
		explain[400] = "Bad Request, resolve type can't be interpreted or certain id can't be found";
		explain[402] = "Bad Request, the image can't be resolved";
		explain[404] = "Not Found, request resouce not found";
		explain[415] = "Unsupported Media Type, the image format is not supported";
		explain[422] = "Unprocessable Entity, the NLP system can't extract information";
		explain[500] = "Internal Server Error, server can't response to your request";
	}
	public static String getExplain(int code){
		return explain[code];
	}
}
