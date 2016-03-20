package org.sensation.snapmemo.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sensation.snapmemo.server.BusinessLogic.BLExecutor;
import org.sensation.snapmemo.server.Data.MySessionFactory;
import org.sensation.snapmemo.server.Utility.IntStringWrapper;
import org.sensation.snapmemo.server.Utility.RequestType;
import org.sensation.snapmemo.server.Utility.ResponseCodeInterpreter;

public class ServletDemo extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2730884609154843411L;
	private static MySessionFactory f;
	private static ResponseCodeInterpreter interpreter;
	public ServletDemo(){
		if(f==null) 
			f = new MySessionFactory();
		if(interpreter==null)
			interpreter = new ResponseCodeInterpreter();
	}
	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doGet(request, response);
		//        response.setContentType("text/html");
		//        PrintWriter out = response.getWriter();
		//        request.getContentType();
		//        out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		//        out.println("<HTML>");
		//        out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
		//        out.println("  <BODY>");
		//        out.println(request.getHeader("Request-Type"));
		//        out.println("  </BODY>");
		//        out.println("</HTML>");
		//        out.flush();
		//        out.close();
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String type = request.getHeader("Request-Type");
		RequestType requestType = RequestType.valueOf(type.replace("-", ""));
		response.setContentType("text/plain");
		BLExecutor exe = new BLExecutor();
		IntStringWrapper result = exe.execute(requestType, request);
		response.setStatus(result.getCode());
		OutputStream os = response.getOutputStream();   
		os.write(result.getInfo().getBytes("UTF-8"));     
		os.close();  
	}

}