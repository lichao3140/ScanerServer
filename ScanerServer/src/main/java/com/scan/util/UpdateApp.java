package com.scan.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * 更新app
 * @author admin
 *
 */
public class UpdateApp {
	private Context activity ;
	public UpdateApp(Context activity){
		this.activity = activity ;
	}
	
	
	/**
	 * 获取版本号
	 * @return
	 */
	public String getVersion(){
		PackageManager pm = activity.getPackageManager() ;
		String version = "";
		try {
			PackageInfo packageInfo = pm.getPackageInfo(  
					activity.getPackageName(), 0);
			version = packageInfo.versionName ;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return version ;
		
	}
}
