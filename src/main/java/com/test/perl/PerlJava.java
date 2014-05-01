/* Author Purusoth Mahendran */

package com.test.perl;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
	private static final Logger log = Logger
			.getLogger(PerlJava.class.getName());
	Boolean isPlagiarised;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PerlJava() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// Changed to True when the code is plagiarised
		try {
			Boolean result;
			Boolean isBasePlagiarised;

			StringBuilder fileNames = new StringBuilder();
			Process process;
			ServletContext servletContext = request.getSession()
					.getServletContext();
			String path = servletContext.getRealPath("/WEB-INF/moss.pl");
			String baseFileName = getBaseFileName(request);
			String folderName = getFolderName(request);
			String masterFileName = getMasterFileName(request);
			String masterFile = PLAG_ROOT_FOLDER + File.separator + folderName
					+ File.separator + masterFileName;
			String compFiles = PLAG_ROOT_FOLDER + File.separator + folderName
					+ File.separator + "vFiles";
			String baseFilePath = PLAG_ROOT_FOLDER + File.separator
					+ folderName + File.separator + baseFileName;
			File file = new File(path);
			if (file.exists()) {
				System.out.println("hello");
				response.getWriter().print("moss.pl exists \n");
			}
			File filer2 = new File(masterFile);
			if (filer2.exists()) {
				System.out.println("hello");
				response.getWriter().print("Checking Files exists \n");
			}

			isBasePlagiarised = isBasePlagiarised(path, masterFile,
					baseFilePath, masterFileName);

			if (!isBasePlagiarised) {
				File vFiles = new File(compFiles);
				File[] listFiles = vFiles.listFiles();

				for (int i = 0; i < listFiles.length; i++) {

					fileNames.append(listFiles[i] + " ");

				}
				String command_normal = "perl " + path + " -l java -b "
						+ baseFilePath + " " + masterFile + " " + fileNames;

				String resultURL = MOSSCheck(command_normal);
				log.info(resultURL);
				isPlagiarised = interpretURL(resultURL, masterFileName, 16);
				log.info("The Master file is plagiarised from other files : "
						+ isPlagiarised);
				response.getWriter().print(isPlagiarised);
			} else {

				response.getWriter().print("Base File pLagiarised");
				isPlagiarised = isBasePlagiarised;
			}

		} catch (Exception excep) {
			excep.printStackTrace();
		}
		response.getWriter().print(isPlagiarised);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	protected String getFolderName(HttpServletRequest request) {
		String folderName = "1";
		return folderName;
	}

	protected String getMasterFileName(HttpServletRequest request) {
		String masterFileName = "MyFirstRobot.java";
		return masterFileName;
	}

	protected String getBaseFileName(HttpServletRequest request) {
		String baseFileName = "Base.java";
		return baseFileName;
	}

	protected Boolean interpretURL(String resultURL, String masterFileName,
			int thresHold) throws Exception {
		String result = resultURL;
		String percentage;
		Boolean plagiarised = false;
		StringBuilder percent = new StringBuilder();
		// List<Integer> plagPercent=new ArrayList<Integer>();
		int percentDuplicate = 0;
		URL oracle = new URL(result);
		BufferedReader in = new BufferedReader(new InputStreamReader(
				oracle.openStream()));
		String inputLine;
		while ((inputLine = in.readLine()) != null) {

			String fileName;
			String re1 = ".*?"; // Non-greedy match on filler
			String re2 = "(" + masterFileName + ")";
			// String re2="(Walls\\.java)";// Fully Qualified Domain Name 1
			String txt = inputLine;
			Pattern p = Pattern.compile(re1 + re2, Pattern.CASE_INSENSITIVE
					| Pattern.DOTALL);
			Matcher m = p.matcher(txt);
			if (m.find()) {
				/*
				 * String fqdn1=m.group(1); System.out.println(inputLine);
				 * fileName=fqdn1.toString();
				 */

				String reg1 = ".*?"; // Non-greedy match on filler
				String reg2 = "(\\([0-9]+%\\))"; // Round Braces 1
				String txt2 = inputLine;
				Pattern pa = Pattern.compile(reg1 + reg2,
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
				Matcher ma = pa.matcher(txt2);
				if (ma.find()) {

					String print = "";
					String rbraces1 = ma.group(1);
					String intermediate = rbraces1.toString();
					int length = intermediate.length();
					if (length == 5) {
						print = intermediate.substring(1, 3);
					} else if (length == 6) {
						print = intermediate.substring(1, 4);
					} else if (length == 4) {

						print = intermediate.substring(1, 2);
					}
					percentDuplicate = Integer.parseInt(print);

					if (percentDuplicate > thresHold) {
						percent.append("True" + percentDuplicate);
						log.info(percent.toString());
						plagiarised = true;
						return plagiarised;
					}
					// plagPercent.add(percentDuplicate);
				}
			}

		}
		in.close();
		if (percent.toString().isEmpty()) {
			percent.append("False");
			// plagPercent.add(percentDuplicate);
		}

		percentage = percent.toString();
		return plagiarised;

	}

	protected final Boolean isBasePlagiarised(String path,
			String masterFilePath, String baseFilePath, String masterFileName)
			throws Exception {

		String command = "perl " + path + " -l java " + masterFilePath + " "
				+ baseFilePath;
		try {

			String resultURL = MOSSCheck(command);
			log.info(resultURL);
			Boolean isPlagiarised = interpretURL(resultURL, masterFileName, 90);
			log.info("Base File is Plagiarised : " + isPlagiarised.toString());
			return isPlagiarised;
		} catch (Exception e) {

		}

		return false;
	}

	protected String MOSSCheck(String command) throws Exception {
		String resultURL;
		Process process;
		Runtime r = Runtime.getRuntime();
		process = r.exec(command);

		process.waitFor();

		if (process.exitValue() == 0) {
			System.out.println("Process Executed Normally");
			log.info("PROCESS EXECUTED SUCCESSFULLY");

			try {

				BufferedReader in = new BufferedReader(new InputStreamReader(
						process.getInputStream()));
				String inputLine;

				while ((inputLine = in.readLine()) != null) {
					String re1 = ".*?"; // Non-greedy match on filler
					String re2 = "(http:\\/\\/moss\\.stanford\\.edu\\/results\\/[0-9]+)"; // HTTP
																							// URL
																							// 1

					Pattern p = Pattern.compile(re1 + re2,
							Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
					Matcher m = p.matcher(inputLine);
					if (m.find()) {
						String httpurl1 = m.group(1);
						System.out.print(httpurl1.toString() + "\n");
						log.info(httpurl1.toString());
						resultURL = httpurl1.toString();
						log.info("The URL FROM MOSS CHECK FUNC " + resultURL);
						return resultURL;

					} else {
						// response.getWriter().print("NOT MATCHING");
						System.out.println("nomatching URL Pattern");
					}

				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Execution Failed");
			log.info("Failed Executing");
		}
		return null;
	}
}
