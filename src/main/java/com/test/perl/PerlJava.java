package com.test.perl;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class PerlJava
 */
@WebServlet("/PerlJava")
public class PerlJava extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PerlJava() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		try {
			Process process;
			ServletContext servletContext = request.getSession().getServletContext();
			String path = servletContext.getRealPath("/WEB-INF/moss.pl");
			String file1 = servletContext.getRealPath("/WEB-INF/*.java");
			String file2 = servletContext.getRealPath("/WEB-INF/deidei.java");
			//System.out.println(path+ " "+file1);
			File file  =new File(path);
			if(file.exists()){
				System.out.println("hello");
			}
			Runtime r = Runtime.getRuntime();
			process = r.exec("perl "+path+" -l java "+file1);
			
			process.waitFor();
			//Runtime rt = Runtime.getRuntime();
			//rt.exec(command);
			if(process.exitValue() == 0) {
			System.out.println("Process Executed Normally");
			
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line = null;
				while ((line = in.readLine()) != null) {
				System.out.println(line);
				response.getWriter().print(line);
				}
				} catch (IOException e) {
				e.printStackTrace();
				}
			} else {
			System.out.println("Execution Failed");
			}
			} catch(Exception excep) {
			excep.printStackTrace();
			}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
