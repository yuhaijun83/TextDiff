package cn.com.yuhaijun;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReWriteFile {

	public static void main(String[] args) throws Exception {
		
		String strProjectPath = "C:\\MyGitHub\\TextDiff"; // TODO
		
		readFolder(new File(strProjectPath));
		
	}
	
	private static void readFolder(File filePath) throws IOException {
		File[] arrayFile = filePath.listFiles();

		String fileName = null;
		List<String> lstFileLine = null;
		for (File file : arrayFile) {
			if (file.isFile()) {
				fileName = file.getName();
				if (fileName.endsWith(".html")) {
					lstFileLine = readFile(file);
					toWriteFile(file, lstFileLine);
					System.out.println("----------------------------");
				} else {
					continue;
				}
			} else if (file.isDirectory()) {
				readFolder(file);
			}
		}
	}

	private static List<String> readFile(File file) throws IOException {
		List<String> lstLine = new ArrayList<String>();

		Pattern p = Pattern.compile("<script src=\"(.*?)\"");
		System.out.println("文件名:" + file.getPath());
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			String strBeforeSpace = null;
			while ((tempString = reader.readLine()) != null) {
				
				Matcher m = p.matcher(tempString);
				if (m.find()) {
					String preTime = new SimpleDateFormat("yyyyddHHmm").format(new Date().getTime());
					String afterRegex = "date=" + preTime;

					strBeforeSpace = getBeforeSpace(tempString);
					System.out.println("修正前:" + m.group());					
					String[] beforeRegex1 = m.group().split("\\?");
					String strmRegex = m.group();
					String jsRegex = "";
					String jsAfterRegex = "";
					if (beforeRegex1.length > 1) {
						String[] beforeRegex2 = beforeRegex1[1].split("\"");
						strmRegex = m.group().replaceAll(beforeRegex2[0], afterRegex);
					} else {
						jsRegex = m.group().substring(0, m.group().length() - 1);
						jsAfterRegex = jsRegex + "?" + afterRegex + "\"";
						strmRegex = m.group().replaceAll(m.group(), jsAfterRegex);
					}
					strmRegex = strmRegex + " />";
					
					System.out.println("修正俊:" + strmRegex);
					tempString = strBeforeSpace + strmRegex;
				}
				
				lstLine.add(tempString);
			}
			reader.close();
		} catch (IOException e) {
			throw new IOException(e);
		} finally {
			if (null != reader) {
				try {
					reader.close();
				} catch (IOException e) {
					;
				}
			}
		}

		return lstLine;
	}

	private static void toWriteFile(File file, List<String> lstFileName) throws IOException {
		FileWriter writer = null;
		try {
			writer = new FileWriter(file, false);

			if (lstFileName.size() > 0) {
				for (String content : lstFileName) {
					writer.write(content);
					writer.write("\r\n");
				}
			}

			writer.close();
		} catch (IOException e) {
			throw new IOException(e);
		} finally {
			if (null != writer) {
				try {
					writer.close();
				} catch (IOException e) {
					;
				}
			}
		}
	}
	
	private static String getBeforeSpace(String str) {
		String strRet = "";
		
	    int i = str.indexOf("<script src=");
	    if (i > 0) {
	    	strRet = str.substring(0, i);
	    }	    
		
		return strRet;
	}

}
