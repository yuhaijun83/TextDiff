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
		
		if (iOldFile == 0) {
			System.out.println(strOldPath + " =>：ファイルが見つかりません！");
			exit();
		}
		if (iNewFile == 0) {
			System.out.println(fileNewPath + " =>：ファイルが見つかりません！");
			exit();
		}
		///////////////////////////////////////////////////////////////////////////////////////////
		List<String> lstOldFileName = new ArrayList<String>();
		List<String> lstNewFileName = new ArrayList<String>();
		Map<String, String> mapAllFileName = new HashMap<String, String>();
		
		String strKey = null;
		for (Map.Entry<String, List<String>> entryOld : mapOld.entrySet()) {
			strKey = entryOld.getKey();
			lstOldFileName.add(strKey);
			mapAllFileName.put(strKey, null);
		}
		for (Map.Entry<String, List<String>> entryNew : mapNew.entrySet()) {
			strKey = entryNew.getKey();
			lstNewFileName.add(strKey);
			mapAllFileName.put(strKey, null);
		}

		List<String> lstDiffFileName = new ArrayList<String>();
		List<String> lstNotFoundOldFileName = new ArrayList<String>();
		List<String> lstNotFoundNewFileName = new ArrayList<String>();
		for (Map.Entry<String, String> entry : mapAllFileName.entrySet()) {
			strKey = entry.getKey();
			if (lstOldFileName.contains(strKey) && lstNewFileName.contains(strKey)) {
				lstDiffFileName.add(strKey);
			} else {
				if (!lstOldFileName.contains(strKey)) {
					lstNotFoundOldFileName.add(strKey);
				}
				if (!lstNewFileName.contains(strKey)) {
					lstNotFoundNewFileName.add(strKey);
				}
			}
		}
		/////////////////////////////////////////////////////////////////////////////////////////
		List<String> oldLstFileInfo = null;
		int oldLstCount = 0;
		String oldFileInfo = null;

		List<String> newLstFileInfo = null;
		int newLstCount = 0;
		String newFileInfo = null;
		
		for (String fileName: lstDiffFileName) {
			
			oldLstFileInfo = mapOld.get(fileName);
			newLstFileInfo = mapNew.get(fileName);

			oldLstCount = oldLstFileInfo.size();
			newLstCount = newLstFileInfo.size();
			
			if (oldLstCount != newLstCount) {
				lstDiffFile.add(fileName);
			} else {
				for (int i = 0; i < oldLstCount; i++) {
					if (i == 4) {
						// ファイルごと、5行目を差分対象外にする　TODO
						continue;
					}
					oldFileInfo = oldLstFileInfo.get(i);
					newFileInfo = newLstFileInfo.get(i);
					
					if (!oldFileInfo.equals(newFileInfo)) {
						lstDiffFile.add(fileName);
						break;
					}
				}
			}
		}
		/////////////////////////////////////////////////////////////////////////////////////////
		String fileName = "C:\\Windows\\Temp\\TextDiff.txt";
		File file = new File(fileName);
		if (file.exists() && file.isFile()) {
			file.delete();
		}

		if (lstNotFoundOldFileName.size() == 0 && lstNotFoundNewFileName.size() == 0 && lstDiffFile.size() == 0) {
			System.out.println("両方のファイルリストとファイル内容が同じです！");
		} else {
			toWriteFile(fileName, lstNotFoundOldFileName, lstNotFoundNewFileName, lstDiffFile);
			Runtime.getRuntime().exec("notepad.exe " + fileName);
		}
	}
	
//	private static int getMaxLength(List<String> lstNotFoundOldFileName, List<String> lstNotFoundNewFileName) {
//		int iRet = 0;
//		int iTemp = 0;
//		
//		int iOld = 0;
//		for (String name : lstNotFoundOldFileName) {
//			iTemp = name.length();
//			if (iTemp > iOld) {
//				iOld = iTemp;
//			}
//		}
//		
//		int iNew = 0;
//		for (String name : lstNotFoundOldFileName) {
//			iTemp = name.length();
//			if (iTemp > iNew) {
//				iNew = iTemp;
//			}
//		}
//		
//		if (iOld > iNew) {
//			iRet = iOld;
//		} else {
//			iRet = iNew;
//		}
//		
//		return iRet;
//	}

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

	private static void toWriteFile(String fileName, List<String> lstNotFoundOldFileName, 
			List<String> lstNotFoundNewFileName, List<String> lstDiffFile) throws IOException {
		FileWriter writer = null;
        try {
            writer = new FileWriter(fileName, true);

            if (lstNotFoundOldFileName.size() > 0) {
                writer.write("下記ファイル名一覧は、フォルダ２に存在しているが、ファイル１に見つかりません！");
                writer.write("\r\n");
                for (String content : lstNotFoundOldFileName) {
                    writer.write(content);
                    writer.write("\r\n");
                }
                writer.write("---------------------------------------------------------------------------------------------------------");
                writer.write("\r\n");
            }

            if (lstNotFoundNewFileName.size() > 0) {
                writer.write("下記ファイル名一覧は、フォルダ１に存在しているが、ファイル２に見つかりません！");
                writer.write("\r\n");
                for (String content : lstNotFoundNewFileName) {
                    writer.write(content);
                    writer.write("\r\n");
                }
                writer.write("---------------------------------------------------------------------------------------------------------");
                writer.write("\r\n");
            }
            
            if (lstDiffFile.size() > 0) {
                writer.write("下記ファイル名のファイル内容は両方が違う！");
                writer.write("\r\n");
                for (String content : lstDiffFile) {
                    writer.write(content);
                    writer.write("\r\n");
                }
                writer.write("---------------------------------------------------------------------------------------------------------");
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
