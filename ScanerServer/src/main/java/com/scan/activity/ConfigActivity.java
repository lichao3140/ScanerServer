package com.scan.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.util.Log;

import com.scan.service.R;
import com.scan.service.ScanService;
import com.scan.util.Config;

public class ConfigActivity extends PreferenceActivity implements OnPreferenceChangeListener{

	private SwitchPreference switchBackMode ;//后台模式打开开关
	private CheckBoxPreference checkVoice ;//声音开关
	private CheckBoxPreference checkF1 ;//设置F1作为扫描键
	private CheckBoxPreference checkF2 ;//设置F2作为扫描键
	private CheckBoxPreference checkFn ;//设置Fn作为扫描键
	private CheckBoxPreference checkLeft ;//设置左侧按键
	private CheckBoxPreference checkRight ;//设置右侧按键
	private CheckBoxPreference checkAddEnter ;//设置设置后缀回车换行

	private Config config ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.scan_settings);
		
		config = new Config(this);
	}
	
	@Override
	protected void onResume() {
		initView();
		super.onResume();
	}

	private void initView() {
		switchBackMode = (SwitchPreference) findPreference("scan_apply_input_back");
		checkVoice = (CheckBoxPreference) findPreference("scan_apply_voice");
		checkF1 = (CheckBoxPreference) findPreference("scan_key_f1");
		checkF2 = (CheckBoxPreference) findPreference("scan_key_f2");
		checkFn = (CheckBoxPreference) findPreference("scan_key_fn");
		checkLeft = (CheckBoxPreference) findPreference("scan_key_left");
		checkRight = (CheckBoxPreference) findPreference("scan_key_right");
		checkAddEnter = (CheckBoxPreference) findPreference("scan_apply_add_enter");
		
		 listener() ;
		 
			if(!config.isOpenBackMode()){
				checkVoice.setEnabled(false);
				checkVoice.setSelectable(false);
				checkF1.setEnabled(false);
				checkF1.setSelectable(false);
				checkF2.setEnabled(false);
				checkF2.setSelectable(false);
				checkFn.setEnabled(false);
				checkFn.setSelectable(false);
				checkLeft.setEnabled(false);
				checkLeft.setSelectable(false);
				checkRight.setEnabled(false);
				checkRight.setSelectable(false);
				checkAddEnter.setEnabled(false);
				checkAddEnter.setSelectable(false);
			}
	}
	
	private void listener(){
		switchBackMode.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				Log.e("switchBackMode", "value-->" + newValue);
				Boolean flag = (Boolean) newValue ;
				if(flag){
					
					Intent intentStart = new Intent(ConfigActivity.this, ScanService.class);
				    startService(intentStart);
				    new AsyncTask<Void, Void, Void>() {

						@Override
						protected Void doInBackground(Void... params) {
//							new Handler().post(new Runnable() {
//								
//								@Override
//								public void run() {
//									switchBackMode.setEnabled(false);
//									switchBackMode.setSelectable(false);
//									switchBackMode.setSummary("正在打开");
//								}
//							});
							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							return null;
						}
						
						@Override
						protected void onPostExecute(Void result) {
							switchBackMode.setEnabled(true);
							switchBackMode.setSelectable(true);
							switchBackMode.setSummary(ConfigActivity.this.getString(R.string.open));
							checkVoice.setEnabled(true);
							checkVoice.setSelectable(true);
							checkF1.setEnabled(true);
							checkF1.setSelectable(true);
							checkF2.setEnabled(true);
							checkF2.setSelectable(true);
							checkFn.setEnabled(true);
							checkFn.setSelectable(true);
							checkLeft.setEnabled(true);
							checkLeft.setSelectable(true);
							checkRight.setEnabled(true);
							checkRight.setSelectable(true);
							checkAddEnter.setEnabled(true);
							checkAddEnter.setSelectable(true);
							super.onPostExecute(result);
						}
					}.execute();
				}else{
					switchBackMode.setSummary(ConfigActivity.this.getString(R.string.close));
					checkVoice.setEnabled(false);
					checkVoice.setSelectable(false);
					checkF1.setEnabled(false);
					checkF1.setSelectable(false);
					checkF2.setEnabled(false);
					checkF2.setSelectable(false);
					checkFn.setEnabled(false);
					checkFn.setSelectable(false);
					checkLeft.setEnabled(false);
					checkLeft.setSelectable(false);
					checkRight.setEnabled(false);
					checkRight.setSelectable(false);
					checkAddEnter.setEnabled(false);
					checkAddEnter.setSelectable(false);
					Intent intentClose = new Intent();
					intentClose.setAction(ScanService.ACTION_CLOSE_SCAN);
					sendBroadcast(intentClose);
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return true ;
			}
		});
		
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if("scan_apply_input_back".equals(preference.getKey())){
			
		}else if("scan_apply_voice".equals(preference.getKey())){
			
		}else if("scan_key_middle".equals(preference.getKey())) {

		}else if("scan_key_fn".equals(preference.getKey())){
			
		}else if("scan_key_left".equals(preference.getKey())){
			
		}else if("scan_key_right".equals(preference.getKey())){
			
		}
		return true;
	}
	
	

	
}
