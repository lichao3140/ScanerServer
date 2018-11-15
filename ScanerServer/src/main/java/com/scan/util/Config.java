package com.scan.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class Config {

	private Context context ;
	private SharedPreferences shared ;
	public Config(Context context){
		this.context = context ;
		this.shared = PreferenceManager.getDefaultSharedPreferences(context);
	}
	/**
	 * 判断是否打开后台扫描
	 * @return
	 */
	public boolean isOpenBackMode(){
		boolean isOpen = shared.getBoolean("scan_apply_input_back", true);
//		Log.e("isOpen", "isOpen----" + isOpen);
		return isOpen;
	}
	
	/**
	 * 设置后台扫描模式
	 * @param isOpen
	 */
	public void setBackMode(boolean isOpen){
		Editor editor = shared.edit();
		editor.putBoolean("scan_apply_input_back", isOpen);
		editor.commit();
	}
	
	/**
	 * 声音是否打开
	 * @return
	 */
	public boolean isOpenVoice(){
		boolean isOpen = shared.getBoolean("scan_apply_voice", true);
		return isOpen ;
	}
	
	/**
	 * 是否添加回车换行
	 * @return
	 */
	public boolean isAddEnter(){
		boolean isAdd =  shared.getBoolean("scan_apply_add_enter", true);
		return isAdd ;
	}
	/**
	 * 判断功能键是否开启
	 * @param keyCode
	 * @return
	 */
	public boolean getFunKeyOpen(int keyCode){
		boolean isKey = false ;
		switch (keyCode) {
		case 142:
			isKey = shared.getBoolean("scan_key_fn", false);
//			setBackMode(false);
			break;
		case 277:
			isKey = shared.getBoolean("scan_key_right", false);
			break;
		case 276:
			isKey = shared.getBoolean("scan_key_left", false);
			break;
		case 278:
			isKey = shared.getBoolean("scan_key_middle", false);
			break;

		}
		return isKey;
	}
	
}
