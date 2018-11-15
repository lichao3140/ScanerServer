package com.scan.service;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

/**
 * 日志说明
 * @author admin
 *
 */
public class LogUtil {

	/**
	 * 后台打印日志信息
	 * @param tag
	 * @param info
	 */
	public static void LogE(String tag, String info){
		Log.e(tag, info);
	}
	
	/**
	 * 生成日志文件
	 * @param tag
	 * @param errorInfo
	 */
	public static void SaveException(String tag, String errorInfo){
		try{
			String path = "/mnt/sdcard/ScanServer/" ;
			File filePath = new File(path);
			if(!filePath.exists()){
				filePath.mkdir();
			}
			File file = new File(path + "Log.txt") ;
			FileWriter fw = new FileWriter(file , true );
			String time = getTime() ;
			fw.write(time + " " + tag + " ---> " + errorInfo);
			fw.flush();
			fw.close() ;
			
		}catch(Exception e){
			Log.e("SaveException", e.toString());
//			e.printStackTrace();
		}
	}
	/**
	 * h获取系统时间
	 * @return
	 */
	public static String getTime(){
		String time = "" ;
		Date date = new Date() ;
		SimpleDateFormat formart = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		time = formart.format(date);
		return time ;
	}
}
