package com.scan.service;

import com.scan.activity.Mservice;
import com.scan.util.Config;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class KeyListener extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		int keycode = intent.getIntExtra("keycode", 0);
//		Log.e("", "keycode = " + keycode);
		Config config = new Config(context);
		boolean openFlag = config.isOpenBackMode();
		boolean keydown = intent.getBooleanExtra("keydown", false);
		boolean isKey = config.getFunKeyOpen(keycode);//是否设置了功能键扫描
		if(keydown && openFlag && isKey){
			Intent mIntent = new Intent(context, ScanService.class);
//			Intent mIntent = new Intent(context, MScanService.class);
			mIntent.putExtra("barcode", "adb");
			context.startService(mIntent);
		}
	}

}
	