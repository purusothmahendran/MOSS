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
	public static final String PLAG_ROOT_FOLDER = "/var/lib/openshift/534a1c2e5004461227000cf4/app-root/data/Plag";
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
			StringBuilder fileNames = new StringBuilder();
			Process process;
			ServletContext servletContext = request.getSession().getServletContext();
			String path = servletContext.getRealPath("/WEB-INF/moss.pl");
			String baseFileName=getBaseFileName(request);
			String folderName = getFolderName(request);
			String masterFileName = getMasterFileName(request);
            String masterFile = PLAG_ROOT_FOLDER+ File.separator+folderName+File.separator+masterFileName;
            String compFiles = PLAG_ROOT_FOLDER+ File.separator+folderName+File.separator+"vFiles";
            String baseFilePath=PLAG_ROOT_FOLDER+ File.separator+folderName+File.separator+baseFileName;
			File file  =new File(path);
			if(file.exists()){
				System.out.println("hello");
				response.getWriter().print("moss.pl exists");
			}
			File filer2=new File(masterFile);
			if(filer2.exists()){
				System.out.println("hello");
				response.getWriter().print("Checking Files exists");
			}
			File vFiles=new File(compFiles);
			File[] listFiles=vFiles.listFiles();
			
			for(int i=0;i<listFiles.length;i++)
			{
				//response.getWriter().print(listFiles[i]);
				fileNames.append(listFiles[i]+ " ");
				//fileNames.append("");
			}
			
			response.getWriter().print(fileNames);
			Runtime r = Runtime.getRuntime();
			process = r.exec("perl " + path + " -l java -b "+ baseFilePath+" "
					+ masterFile + " "
					+ fileNames);
			/*process = r.exec("perl " + path + " -l java "
					+ file1 + " "
					+ file2);*/
			
			process.waitFor();
			//Runtime rt = Runtime.getRuntime();
			//rt.exec(command);
			if(process.exitValue() == 0) {
			System.out.println("Process Executed Normally");
			response.getWriter().print("Normal");
			
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
			response.getWriter().print("Failed");
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
	
	protected String getFolderName(HttpServletRequest request){
		String folderName="1";
		return folderName;
	}
	
	protected String getMasterFileName(HttpServletRequest request){
		String masterFileName="MyFirstRobot.java";
		return masterFileName;
	}
	
	protected String getBaseFileName(HttpServletRequest request){
		String baseFileName="Tracker.java";
		return baseFileName;
	}
}
