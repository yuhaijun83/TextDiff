package cn.com.yuhaijun;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author YuHaijun
 *
 */
public class TextDiff {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		paramChk(args);

		try {
			fileChk(args);
		} catch (IOException e) {
			System.out.println("エラーが発生した：");
			System.out.println(e.getMessage());
			exit();
		}
	}

	private static void paramChk(String[] args) {
		int iParam = 0;

		if (null != args) {
			iParam = args.length;
		}

		if (iParam != 2) {
			System.out.println("パラメータの個数が違う！");
			System.out.println();
			System.out.println("使い方：");
			System.out.println("java -jar TextDiff.jar <フォルダ１> <フォルダ２>");
			System.out.println();
			System.out.println("例：");
			System.out.println("java -jar TextDiff.jar C:\\Temp\\Old C:\\Temp\\New");
			exit();
		}
	}

	private static void fileChk(String[] args) throws IOException {
		
		List<String> lstDiffFile = new ArrayList<String>();
		lstDiffFile.add("ファイル内容一致していないファイルの一覧：");

		String strOldPath = args[0];
		String strNewPath = args[1];

		File fileOldPath = new File(strOldPath);
		File fileNewPath = new File(strNewPath);
		
		/////////////////////////////////////////////////////////////////////////////////////////
		if (!fileOldPath.exists()) {
			System.out.println("フォルダ：" + strOldPath + " が見つかりません！");
			exit();
		} else if (!fileOldPath.isDirectory()) {
			System.out.println(strOldPath + " フォルダではない！");
			exit();
		} else if (!fileNewPath.exists()) {
			System.out.println("フォルダ：" + strNewPath + " が見つかりません！");
			exit();
		} else if (!fileNewPath.isDirectory()) {
			System.out.println(strNewPath + " フォルダではない！");
			exit();
		}
		
		/////////////////////////////////////////////////////////////////////////////////////////
		Map<String, List<String>> mapOld = readFolder(fileOldPath);
		Map<String, List<String>> mapNew = readFolder(fileNewPath);

		int iOldFile = mapOld.size();
		int iNewFile = mapNew.size();
		if (iOldFile != iNewFile) {
			System.out.println("両方のファイル個数が違う！");
			System.out.println(strOldPath + " =>：" + iOldFile);
			System.out.println(strNewPath + " =>：" + iNewFile);
			exit();
		}

		if (iOldFile == 0) {
			System.out.println(strOldPath + " =>：ファイルが見つかりません！");
			exit();
		}
		if (iNewFile == 0) {
			System.out.println(fileNewPath + " =>：ファイルが見つかりません！");
			exit();
		}
		///////////////////////////////////////////////////////////////////////////////////////////
		StringBuilder sbOldFileNameList = new StringBuilder();
		StringBuilder sbNewFileNameList = new StringBuilder();
		
		for (Map.Entry<String, List<String>> entryOld : mapOld.entrySet()) {
			sbOldFileNameList.append(entryOld.getKey());			
		}
		for (Map.Entry<String, List<String>> entryNew : mapOld.entrySet()) {
			sbNewFileNameList.append(entryNew.getKey());			
		}
		
		if (!sbOldFileNameList.equals(sbNewFileNameList)) {
			System.out.println("両方のファイル名が一致していない！");
			System.out.println(strOldPath + " => FileName All Size ：" + sbOldFileNameList.toString().length());
			System.out.println(strNewPath + " => FileName All Size ：" + sbNewFileNameList.toString().length());
			exit();
		}
		/////////////////////////////////////////////////////////////////////////////////////////

		String oldFileName = null;
		List<String> oldLstFileInfo = null;
		int oldLstCount = 0;
		String oldFileInfo = null;

		String newFileName = null;
		List<String> newLstFileInfo = null;
		int newLstCount = 0;
		String newFileInfo = null;
		
		for (Map.Entry<String, List<String>> entryOld : mapOld.entrySet()) {
			oldFileName = entryOld.getKey();
			oldLstFileInfo = entryOld.getValue();
			
			for (Map.Entry<String, List<String>> entryNew : mapNew.entrySet()) {
				newFileName = entryNew.getKey();
				newLstFileInfo = entryNew.getValue();
				
				if (oldFileName.equals(newFileName)) {
					oldLstCount = oldLstFileInfo.size();
					newLstCount = newLstFileInfo.size();
					
					if (oldLstCount != newLstCount) {
						lstDiffFile.add(oldFileName);
						break;
					} else {

						for (int i = 0; i < oldLstCount; i++) {
							if (i == 4) {
								// ファイルごと、5行目を差分対象外にする　TODO
								continue;
							}
							oldFileInfo = oldLstFileInfo.get(i);
							newFileInfo = newLstFileInfo.get(i);
							
							if (!oldFileInfo.equals(newFileInfo)) {
								lstDiffFile.add(oldFileName);
								break;
							}
						}
					}
					mapNew.remove(newFileName, newLstFileInfo);
					break;
				}
			}
		}
		
		if (lstDiffFile.size() > 1) {
			String fileName = "C:\\Windows\\Temp\\TextDiff.txt";
			File file = new File(fileName);
			if (file.exists() && file.isFile()) {
				file.delete();
			}
			toWriteFile(fileName, lstDiffFile);
			Runtime.getRuntime().exec("notepad.exe " + fileName);
		} else {
			System.out.println("両方のファイル内容が同じです！");
		}
	}

	private static Map<String, List<String>> readFolder(File filePath) throws IOException {
		Map<String, List<String>> mapRet = new HashMap<String, List<String>>();

		File[] arrayFile = filePath.listFiles();

		String fileName = null;
		List<String> lstFileLine = null;
		for (File file : arrayFile) {
			fileName = file.getName();
			lstFileLine = readFile(file);
			mapRet.put(fileName, lstFileLine);
		}

		return mapRet;
	}

	private static List<String> readFile(File file) throws IOException {
		List<String> lstLine = new ArrayList<String>();

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
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

	private static void toWriteFile(String fileName, List<String> info) throws IOException {
		FileWriter writer = null;
        try {
            writer = new FileWriter(fileName, true);
            for (String content : info) {
                writer.write(content);
                writer.write("\r\n");
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

	private static void exit() {
		System.exit(-1);
	}

}
