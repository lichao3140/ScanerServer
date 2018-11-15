package com.scan.service;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.hsm.barcode.DecodeResult;
import com.hsm.barcode.DecodeWindowing.DecodeWindowShowWindow;
import com.hsm.barcode.Decoder;
import com.hsm.barcode.DecoderConfigValues.LightsMode;
import com.hsm.barcode.DecoderConfigValues.SymbologyID;
import com.hsm.barcode.DecoderException;
import com.hsm.barcode.ImageAttributes;
import com.hsm.barcode.SymbologyConfig;
import com.scan.activity.Mservice;
import com.scan.util.Config;
import com.scan.util.UpdateApp;

import java.io.File;
import java.io.FileOutputStream;
//import android.util.Log;


public class ScanService extends Service {
	
	private static final int INIT_OK = 1 ;
	private static final int  INIT_FAIL = -1 ;
	
	private Decoder mDecoder ;  // scan inital
	private DecodeResult mDecodeResult;  //scan result
	
	private String TAG = "ScanService"; //DEBUG
	
	private String charSetName = "utf-8";
	
	private boolean running = false ;// scaner open flag

	private Config config ;//读取配置列表
	
	private KillReceiver killReceiver ;//广播接受者用于接受打开和关闭的广播
	
	private SwicthMethod swM ;//切换输入法
	
	public static final  String ACTION_CLOSE_SCAN = "colseScan";
	
	public static final  String ACTION_START_SCAN = "startScan";
	@Override
	public void onCreate() {
		config = new Config(this);
		//注册广播接受者
		killReceiver  = new KillReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_CLOSE_SCAN);
		filter.addAction(ACTION_START_SCAN);
		registerReceiver(killReceiver, filter);
//		Log.e("onCreate", "onCreate-->registerReceiver");
		
		swM = new SwicthMethod(this);
		
		//读取软件的版本号
		UpdateApp up = new UpdateApp(this) ;
		Log.e("APP version ", up.getVersion()) ;
		super.onCreate();
	}
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return aidlScan;
	}
	
	@Override
	public void onDestroy() {
		if(killReceiver != null){
			unregisterReceiver(killReceiver);
		}
	
		super.onDestroy();
	}
	
	private long startTime = 0L;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		synchronized (aidlScan) {
			try{
				if(!running){
					swM.getDefualtInputM() ;//获取默认输入法
					running = true ;
					scanning = false ;
					aidlScan.init();
					aidlScan.setOnResultListener(new IScanResult() {
						
						@Override
						public IBinder asBinder() {
							// TODO Auto-generated method stub
							return null;
						}
						
						@Override
						public void onListener(String barcode, byte[] bar) throws RemoteException {
//							Log.e("bar-------", barcode);
							
							Intent mIntent = new Intent(ScanService.this, Mservice.class);
							mIntent.putExtra("barcode", barcode);
							startService(mIntent);
						}
					});
				}else{
					//200ms内的操作不做处理
					if(System.currentTimeMillis() - startTime < 200){
						startTime = System.currentTimeMillis();
					}else{
						swM.switchMethod() ;
						aidlScan.scan();	
					}

				}
				}catch(Exception e){
					
				}
		}

		return super.onStartCommand(intent, flags, startId);
	}
	
	
	boolean isOpening = false ;// 如果程序正在打开，则不能马上关闭
	/**
	 * 用于接收关闭信息
	 * @author pang
	 *
	 */
	private class KillReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
//			Log.e("KillReceiver", "KillReceiver ");
			try{
			if(intent.getAction().equals(ACTION_CLOSE_SCAN)){
//				Log.e("KillReceiver", "KillReceiver=== CLOSE");
				if(aidlScan != null){
						aidlScan.close();
						running = false ;
				}
			}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LogUtil.SaveException("KillReceiver()", e.toString()) ;
			}
			
		}
		
	}
	
	
	
	private  AidlIscan aidlScan = new AidlIscan();
	
	private boolean scanning = false ;
	//远程调用接口
	class AidlIscan extends IScan.Stub{
		private IScanResult iResultLister ;
		
		private Thread scanThread = null ;//扫描线程
		
		private Runnable scanRun = new Runnable() {
			
			@Override
			public synchronized void run() {
				if(mDecoder != null){
					scanning = true ;
					try {
						boolean is = mDecoder.callbackKeepGoing();
						LogUtil.LogE("callbackKeepGoing", "" + is);
						Thread.sleep(10) ;
						//扫描，超时为5秒
//						mDecoder.waitForDecodeTwo(5000, mDecodeResult);
						mDecoder.waitForDecode(5000) ;
						if(mDecoder.getBarcodeData() != null && mDecoder.getBarcodeLength() > 0){
							//获取最后一张图片并保存
//							getLastImg() ;
							//�ص�
							if(iResultLister != null){
//								Log.e("barcode", "listner");
								//将结果回调
								iResultLister.onListener(mDecoder.getBarcodeData(), mDecoder.getBarcodeByteData());
								
							}
//							scanning = false ;
						}
						//减慢速度
						Thread.sleep(10) ;
						scanning = false ;
					} catch (DecoderException e) {
						swM.retoreMethod();
						//出现异常停止扫描
						try {
							Thread.sleep(100) ;
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						e.printStackTrace();
						
						scanning = false ;
					} catch (RemoteException e) {
						//输入法恢复
						swM.retoreMethod();
						scanning = false ;
						LogUtil.SaveException("scan()", e.toString()) ;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		};
		@Override//初始化
		public int init() throws RemoteException {
			mDecoder = new Decoder();
			mDecodeResult = new DecodeResult();
			try {
				mDecoder.connectDecoderLibrary();
				//打开所有码制,(只打开QR码)
//				mDecoder.enableSymbology(SymbologyID.SYM_ALL);
				mDecoder.enableSymbology(SymbologyID.SYM_QR);
//				mDecoder.enableSymbology(SymbologyID.SYM_PDF417);
//				mDecoder.enableSymbology(SymbologyID.SYM_DATAMATRIX);
//				mDecoder.enableSymbology(SymbologyID.SYM_UPCA);
//				mDecoder.enableSymbology(SymbologyID.SYM_CHINAPOST);
				mDecoder.enableSymbology(SymbologyID.SYM_EAN13);
//				mDecoder.enableSymbology(SymbologyID.SYM_CODE39);
//				mDecoder.enableSymbology(SymbologyID.SYM_CODE128);
//				mDecoder.enableSymbology(SymbologyID.SYM_EAN8);
//				mDecoder.enableSymbology(SymbologyID.SYM_CODE32);
				//打开EAN13码校验
				SymbologyConfig config = new SymbologyConfig(SymbologyID.SYM_EAN13);
				config.Flags = 5 ;
				config.Mask = 1 ;
				mDecoder.setSymbologyConfig(config);
				//设置解码窗口模式
				mDecoder.setDecodeWindowMode(DecodeWindowShowWindow.DECODE_WINDOW_SHOW_WINDOW_WHITE) ;
				//设置灯光模式
				mDecoder.setLightsMode(LightsMode.ILLUM_AIM_ON);
				try {
					mDecoder.startScanning();
					Thread.sleep(50);
					mDecoder.stopScanning();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					LogUtil.SaveException("scan()", e.toString()) ;
				}
//				new Thread(new Runnable() {
//					
//					@Override
//					public void run() {
//						try {
//							mDecoder.startScanning();
//							Thread.sleep(50);
//							mDecoder.stopScanning();
//						} catch (Exception e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//							LogUtil.SaveException("scan()", e.toString()) ;
//						}
//						
//						
//					}
//				}).start();

			} catch (DecoderException e) {
				LogUtil.SaveException("init()", e.toString()) ;
				e.printStackTrace();
				return INIT_FAIL;
			}
			return INIT_OK;
		}

		int m_img_height  ;
		int m_img_width ;

		private Bitmap bmp;
		//获取最后一张图
		private void getLastImg(){
			try {
				m_img_height = mDecoder.getImageHeight() ;
				m_img_width = mDecoder.getImageWidth() ;
				//图像参数
				ImageAttributes attr = new ImageAttributes();
				byte[] buffer = new byte[m_img_width * m_img_height]; // it 8-bit RAW
				buffer = mDecoder.getLastImage(attr);
				if(buffer != null){
					// Convert from 8-bit RAW to 16-bit color
					int width = m_img_width;
					int height = m_img_height;
					int[] array = new int[width*height*2]; // 2 bytes per pixel (RGB_565)
					for(int h = 0; h < height; h++)
					{
						for(int w = 0; w < width; w++)
						{
							array[width*h + w] = buffer[width*h + w] * 0x00010101;		
						}
					}
					bmp = Bitmap.createBitmap(m_img_width, m_img_height, Bitmap.Config.RGB_565);
					// Set the pixels
					bmp.setPixels(array, 0, width, 0, 0, width, height);
					//save image
					saveImg(bmp) ;
				}
				
			} catch (DecoderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		//保存图片
		private void saveImg(Bitmap bp){
			try{
				File file = new File("/mnt/sdcard/Img.png") ;
				if(file.exists()){
					file.delete() ;
				}
				FileOutputStream fout = new FileOutputStream(file);
				if(bp.compress(Bitmap.CompressFormat.PNG, 900, fout)){
					fout.flush();
					fout.close() ;
				}
				
			}catch(Exception e){
				
			}
		}
		
		@Override
		public void close() throws RemoteException {
			running = false ;
			if(mDecoder != null){
				try {
					mDecoder.disconnectDecoderLibrary();
					mDecoder = null ;
				} catch (DecoderException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					LogUtil.SaveException("close()", e.toString()) ;
				}
			}
			
		}

		@Override
		public void scan() throws RemoteException {
//			mDecoder.
			if(!scanning){
				if(scanThread != null ){
					LogUtil.LogE("scanThread--alive--->", "" + scanThread.isAlive());
//					try {
//						mDecoder.stopScanning();
//						Thread.sleep(10) ;
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					//线程不为null强制中断
					scanThread.interrupt() ;
					scanThread = null ;
				}
				//创建单线程
				scanThread = new Thread(scanRun);
				LogUtil.LogE("scanThread--alive--->", "" + scanThread.isAlive());
				scanThread.start() ;
			}
			
		}

		@Override
		public void setOnResultListener(IScanResult iLister)
				throws RemoteException {
			
			iResultLister = iLister;
		}

		@Override
		public void setChar(String charSetName) throws RemoteException {
			ScanService.this.charSetName = charSetName ;
			
		}
		
	}
	
	private String chineseHolder(byte[] barcodeBytes, String charSetName){
		String data = null;
		try{
			if("GB2312".equals(charSetName) || "gb2312".equals(charSetName)){
				data = new String(barcodeBytes, 0, barcodeBytes.length, "gb2312");
			}else if("GBK".equals(charSetName) || "gbk".equals(charSetName)){
				data = new String(barcodeBytes, 0, barcodeBytes.length, "gbk");
			}else{
				data = new String(barcodeBytes, 0, barcodeBytes.length, "UTF-8");
			}
		}catch(Exception e){
			
			LogUtil.SaveException("chineseHolder", e.toString()) ;
			
		}
		
		return data ; 
	}

}
