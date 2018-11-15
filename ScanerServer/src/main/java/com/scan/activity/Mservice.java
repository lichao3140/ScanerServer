package com.scan.activity;

import java.util.Timer;
import java.util.TimerTask;

import com.scan.service.MainActivity;
import com.scan.service.SwicthMethod;
import com.scan.service.Util;
import com.scan.util.Config;

import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

public class Mservice extends InputMethodService {
	
	 private String defualt ; 
     
	 private Timer timer ;
	 private boolean methodFlag = false ;
	 
	 private SwicthMethod swM ;  //切换输入法
	 private Config config ;//读取设置信息
	 
	 @Override
	public void onCreate() {
		// TODO Auto-generated method stub
		 swM = new SwicthMethod(this);
		 Util.initSoundPool(this);
		 config = new Config(this);
		super.onCreate();
	}
	 
		@Override
		public int onStartCommand(Intent intent, int flags, int startId) {
			boolean voiceFlag = config.isOpenVoice();
			boolean addEnter = config.isAddEnter() ;
			//声音是否需要打开
			if(voiceFlag){
				Util.play(1, 0);
			}
			
			String barcode = intent.getStringExtra("barcode");
			InputConnection mConnection  = getCurrentInputConnection();
			if(mConnection != null && barcode != null){
				//是否添加回车换行
				
//					barcode += "\r\n";
				
				boolean flag = getCurrentInputConnection().commitText(barcode, 1);
				if(addEnter){
				//加入enter事件
				KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER) ;
				getCurrentInputConnection().sendKeyEvent(event);
				
				KeyEvent event1 = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER) ;
				getCurrentInputConnection().sendKeyEvent(event1);
				}
//				Log.e("flag", " flag = " + flag);
			}
//			KeyEvent event = new KeyEvent(action, code)
			if(timer != null){
				timer.cancel();
			}
			timer = new Timer();
			timer.schedule(new TimerTask() {
				
				@Override
				public void run() {
//					Log.e("Timer", " Timer");
				swM.retoreMethod();
					
				}
			}, 1000);
			
			return super.onStartCommand(intent, flags, startId);
		}
		
		
		
}
