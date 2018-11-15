package com.scan.service;

import android.content.Context;
import android.provider.Settings;
import android.view.inputmethod.InputMethodManager;

public class SwicthMethod {

	private Context context ;
	private String defualt ;  //系统默认输入法
	
	private final String M_METHOD =  "com.scan.service/com.scan.activity.Mservice";  //后台输入法
	
	private  String SYS_METHOD = "com.android.inputmethod.latin/.LatinIME" ;//系统默认输入法
	public SwicthMethod(Context context){
		this.context = context ;
	}
	
	/**
	 * 获取默认输入法
	 */
	public void getDefualtInputM(){
		 //获取默认选择的输入法
		defualt  = Settings.Secure.getString(context.getContentResolver(),  Settings.Secure.DEFAULT_INPUT_METHOD);
	}
	
	//切换到后台可输入数据的输入法
	public  void switchMethod(){

		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);  
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

//		 System.out.println("default---" + defualt);
		//"com.android.inputmethod.latin/.LatinIME"
/*			 Settings.Secure.putString(this.getContentResolver(), 
				 Settings.Secure.ENABLED_INPUT_METHODS,
				 "com.android.inputmethod.latin/.LatinIME");*/
		//获取系统默认输入法
		String sysInput = Settings.Secure.getString(context.getContentResolver(),  Settings.Secure.DEFAULT_INPUT_METHOD);
		if(sysInput.equals(M_METHOD)){
			return ;
		}
		//获取之前不是后台输入的输入法
		SYS_METHOD = sysInput ;
			//激活输入法
		 Settings.Secure.putString(context.getContentResolver(), 
				 Settings.Secure.ENABLED_INPUT_METHODS,
				 "com.scan.service/com.scan.activity.Mservice");
		 //强制切换
//		 this.switchInputMethod("com.scan.service/com.scan.activity.Mservice");
//		 InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		 
		 //强制转换输入法
		 Settings.Secure.putString(context.getContentResolver(), 
				 Settings.Secure.DEFAULT_INPUT_METHOD,
				 "com.scan.service/com.scan.activity.Mservice");
	}
	
	
	public void retoreMethod(){
		
//		 this.switchInputMethod("com.scan.service/com.scan.activity.Mservice");
		String sysInput = Settings.Secure.getString(context.getContentResolver(),  Settings.Secure.DEFAULT_INPUT_METHOD);
		if(sysInput.equals(M_METHOD)){
			Settings.Secure.putString(context.getContentResolver(), 
					 Settings.Secure.ENABLED_INPUT_METHODS,
					 SYS_METHOD);
		 Settings.Secure.putString(context.getContentResolver(), 
					 Settings.Secure.DEFAULT_INPUT_METHOD,
					 SYS_METHOD);
			InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);  
			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		}

	}
}
