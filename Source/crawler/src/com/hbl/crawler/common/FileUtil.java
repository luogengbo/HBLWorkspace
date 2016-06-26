package com.hbl.crawler.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.apache.log4j.Logger;

public class FileUtil {
	
	public static Logger logger = Logger.getLogger(FileUtil.class);
	
	/**
	 * 创建文件夹
	 * @param path 目录
	 */
	public static void createDir(String path) {
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdir();
			logger.info("创建文件夹成功！");
		}
	}

	/**
	 * 递归删除文件夹 要利用File类的delete()方法删除目录时， 必须保证该目录下没有文件或者子目录，否则删除失败，
	 * 因此在实际应用中，我们要删除目录， 必须利用递归删除该目录下的所有子目录和文件， 然后再删除该目录。
	 * 
	 * @param path
	 */
	public static void delDir(String path) {
		if(path==null||"".equals(path)){
			return;
		}
		if(path.endsWith(File.separator)){
			path = path.substring(0, path.length()-1);
		}
		File dir = new File(path);
		if (dir.exists()) {
			File[] childArr = dir.listFiles();
			for (int i = 0; i < childArr.length; i++) {
				if (childArr[i].isDirectory()) {
					delDir(path + File.separator + childArr[i].getName());
				} else {
					childArr[i].delete();
				}
			}
			dir.delete();
		}
	}

	/**
	 * 转移文件目录不等同于复制文件，复制文件是复制后两个目录都存在该文件， 而转移文件目录则是转移后，只有新目录中存在该文件
	 */
	/**
	 * 转移文件目录
	 * 
	 * @param filename 文件名
	 * @param oldpath  旧目录
	 * @param newpath  新目录
	 * @param cover    若新目录下存在和转移文件具有相同文件名的文件时，是否覆盖新目录下文件， cover=true将会覆盖原文件，否则不操作
	 */
	public static void changeDirectory(String filename, String oldpath,
			String newpath, boolean cover) {
		if (!oldpath.equals(newpath)) {
			File oldfile = new File(oldpath + File.separator + filename);
			File newfile = new File(newpath + File.separator + filename);
			if (newfile.exists()) {// 若在待转移目录下，已经存在待转移文件
				if (cover){// 覆盖
					oldfile.renameTo(newfile);
				}else{
					logger.error("在新目录下已经存在：" + filename);
				}
			} else {
				oldfile.renameTo(newfile);
			}
		}
	}

	/**
	 * 创建新文件
	 * @param path 目录
	 * @param filename 文件名
	 * @throws IOException
	 */
	public static void createFile(String path, String filename) throws IOException {
		File file = new File(path + File.separator + filename);
		if (!file.exists()) {
			file.createNewFile();
			logger.info("创建文件成功！");
		}
	}
	
	/**
	 * 写文件
	 * @param path      文件路径
	 * @param content   文件内容
	 * @param encoding  存储的 编码格式
	 * @throws IOException
	 */
	public static void writeFile(String path, String content, String encoding)  
            throws IOException {  
        File file = new File(path);  
        file.delete();  
        file.createNewFile();  
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(  
                new FileOutputStream(file), encoding));  
        writer.write(content);  
        writer.close();  
    } 
	
	/**
	 * 读取文本文件内容
	 * @param filePath 带有完整绝对路径的文件名
	 * @param encoding 文本文件打开的编码方式
	 * @return 返回文本文件的内容
	 */
	public static String readFile(String filePath, String encoding){
		encoding = encoding.trim();
		StringBuffer strBuffer = new StringBuffer("");
		try {
			InputStreamReader isr;
			if (encoding == null || encoding.equals("")) {
				isr = new InputStreamReader(new FileInputStream(filePath));
			} else {
				isr = new InputStreamReader(new FileInputStream(filePath), encoding);
			}
			BufferedReader reader = new BufferedReader(isr);
			try {
				String line = null;  
		        while ((line = reader.readLine()) != null) {  
		            strBuffer.append(line).append("\n");  
		        }
			} catch (Exception e) {
				reader.close();
			}
		} catch (IOException e) {
			logger.warn("Read the file "+filePath + " Error!");
		}
		return strBuffer.toString();
	}

	/**
	 * 文件重命名--如果重命名的目标文件已经存在,则不会进行任何操作
	 * @param path    文件目录
	 * @param oldname 原来的文件名
	 * @param newname 新文件名
	 */
	public static void renameFile(String path, String oldname, String newname) {
		if (!oldname.equals(newname)) {// 新的文件名和以前文件名不同时,才有必要进行重命名
			File oldfile = new File(path + File.separator + oldname);
			File newfile = new File(path + File.separator + newname);
			if (newfile.exists())// 若在该目录下已经有一个文件和新文件名相同，则不允许重命名
				logger.error(newname + "已经存在！");
			else {
				oldfile.renameTo(newfile);
			}
		}
	}

	/**
	 * 以文件流的方式复制文件 支持中文处理，并且可以复制多种类型，比如txt，xml，jpg，doc等多种格式
	 * @param srcPath   文件源目录
	 * @param destPath  文件目的目录
	 * @throws IOException
	 */
	public static void copyFile(String srcPath, String destPath) throws IOException {
		FileInputStream in = new FileInputStream(srcPath);
		File file = new File(destPath);
		if (!file.exists()){
			file.createNewFile();
		}
		FileOutputStream out = new FileOutputStream(file);
		int c;
		byte buffer[] = new byte[1024];
		while ((c = in.read(buffer)) != -1) {
			for (int i = 0; i < c; i++){
				out.write(buffer[i]);
			}
		}
		in.close();
		out.close();
	}

	/**
	 * 删除文件
	 * @param path  目录
	 * @param filename 文件名
	 */
	public static void delFile(String path, String filename) {
		if(path==null || "".equals(path)){
			logger.error("delFile fail! path be needed!");
		}
		if(path.endsWith(File.separator)){
			path = path.substring(0, path.length()-1);
		}
		File file = new File(path + File.separator + filename);
		if (file.exists() && file.isFile()){
			file.delete();
		}
	}
	
	/**
	 * 删除文件
	 * @param filePath  文件路径
	 */
	public static void delFile(String filePath) {
		if(filePath==null || "".equals(filePath)){
			logger.error("delFile fail! path be needed!");
		}
		File file = new File(filePath);
		if (file.exists() && file.isFile()){
			file.delete();
		}
	}
}