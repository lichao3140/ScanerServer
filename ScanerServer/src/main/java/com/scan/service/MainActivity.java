package com.scan.service;

import java.util.List;

import com.scan.activity.Mservice;
import com.scan.service.R;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
	private EditText editTest ;
	private Button buttonTest ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main) ;
		
		editTest = (EditText) findViewById(R.id.editText_test);
		buttonTest = (Button) findViewById(R.id.button_test);
		
		buttonTest.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				 List<InputMethodInfo> listInfo = imm.getInputMethodList();
				 //
				 InputMethodSubtype subtype =  imm.getCurrentInputMethodSubtype();
//				 subtype.getLocale();
//				 Log.e("subtype", "Locale ---- " + subtype.getLocale());
//				 Log.e("subtype", "NameResId ---- " + subtype.getNameResId());
//				 Log.e("subtype", "Mode ---- " + subtype.getMode());
				 if(listInfo != null && listInfo.size() > 0){
					 for(InputMethodInfo info : listInfo){
						 CharSequence label = info.loadLabel(getPackageManager());
						 System.out.println("id = "+info.getId() + ";  label = "+label);
						 
						 if(null != info.getSettingsActivity()){
							 System.out.println( " active----->"+ info.getSettingsActivity());
						 }
					 }
				 }
				 
				 
				 //01-04 06:48:26.206: I/System.out(6519): id = com.android.inputmethod.latin/.LatinIME;  label = Android 键盘 (AOSP)
				 //01-04 06:58:21.620: I/System.out(6915): id = com.scan.service/com.scan.activity.Mservice;  label = ScanerServer
//				Settings.Secure.putInt(MainActivity.this.getContentResolver(),"com.scan.service/com.scan.activity.Mservice" , 1);
				//激活输入法
				 Settings.Secure.putString(MainActivity.this.getContentResolver(), 
						 Settings.Secure.ENABLED_INPUT_METHODS,
						 "com.scan.service/com.scan.activity.Mservice");
				 
//				 Intent toApp = getPackageManager().getLaunchIntentForPackage("com.scan.service");
//				 toApp.setAction("com.psw.scan");
//				 ComponentName cn = new ComponentName("com.example.noiconapp","com.example.noiconapp.MainActivity");
//				 toApp.setClass(this, cls)
				 
				 
				 //获取默认选择的输入法
				 String defualt = Settings.Secure.getString(MainActivity.this.getContentResolver(),  Settings.Secure.DEFAULT_INPUT_METHOD);
				 System.out.println("default---" + defualt);
//				 //强制转换输入法
				 Settings.Secure.putString(MainActivity.this.getContentResolver(), 
						 Settings.Secure.DEFAULT_INPUT_METHOD,
						 "com.scan.service/com.scan.activity.Mservice");
				 
				 //恢复原先的输入法
				 Settings.Secure.putString(MainActivity.this.getContentResolver(), 
				 Settings.Secure.DEFAULT_INPUT_METHOD,
				 "com.android.inputmethod.latin/.LatinIME");
//				 imm.sw
//				Intent intent = new Intent(MainActivity.this, Mservice.class);
//				startService(intent);
				
			}
		});
	}

}
