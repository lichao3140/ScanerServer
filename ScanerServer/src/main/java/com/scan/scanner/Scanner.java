package com.scan.scanner;

import com.hsm.barcode.DecodeResult;
import com.hsm.barcode.Decoder;
import com.hsm.barcode.DecoderException;
import com.hsm.barcode.SymbologyConfig;
import com.hsm.barcode.DecoderConfigValues.SymbologyID;
import com.scan.service.LogUtil;
import com.scan.service.Util;

/**
 * 扫描
 * @author admin
 *
 */
public class Scanner implements IScaner{
	
	/**扫描引擎****/
	private Decoder mDecoder;  //设备
	/**扫描解码结果****/
	private DecodeResult mDecodeResult;//扫描结果
	/**debug****/
	private String TAG = "class Scanner" ;
	/**扫描扫描状态****/
	private boolean isScanning = false ;//默认
	/**扫描结果回调接口****/
	private IScanResult result ;//
	/**默认解码字符集****/
	private String charSet = "UTF-8" ;
	
	private boolean start = true ; 

	public boolean isInit = false ;
	/**
	 * 初始化设备
	 * @return
	 */
	@Override
	public int initDev() {
		mDecoder = new Decoder();
		mDecodeResult = new DecodeResult();
		try {
			//连接设备  connect dev
			mDecoder.connectDecoderLibrary();
			//设置所有码制可用  set all symbology enable
			mDecoder.enableSymbology(SymbologyID.SYM_QR);
			//设置EN13码
			SymbologyConfig config = new SymbologyConfig(SymbologyID.SYM_EAN13);
			config.Flags = 5 ;
			config.Mask = 1 ;
			mDecoder.setSymbologyConfig(config);
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						//触发扫描
						mDecoder.startScanning();
						Thread.sleep(50);
						mDecoder.stopScanning();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						LogUtil.SaveException("scan()", e.toString()) ;
					}
					
					
				}
			}).start();
			isInit = true ;
			start = true ; 
			//开启线程
			new Thread(new ScanTask()).start() ;
		} catch (DecoderException e) {
			
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 关闭设备
	 */
	@Override
	public void closeDev() {
		start = false ;
		scan = false ;
		taskOK = false ;
		isInit = false ;
		LogUtil.LogE(TAG, "closeDev()") ;
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
	
	private boolean scan = false ; 
	private boolean taskOK = true ;
	//扫描线程
	class ScanTask implements Runnable{

		@Override
		public void run() {
			
			while(start){
				try{
					//是否触发扫描
					if(scan){
						
						//加50ms的延时
						Thread.sleep(50) ;
						//线程是否被占用
						if(taskOK){
							taskOK = false ;
							mDecoder.waitForDecodeTwo(6000, mDecodeResult);
							//得到结果回调
							if(mDecodeResult.barcodeData != null && mDecodeResult.barcodeData.length() > 0){
								if(result != null){
									result.onListne(mDecodeResult.barcodeData, mDecodeResult.byteBarcodeData) ;
									scan = false ;
								}
							}
						}
					}
//					if(scan){
//						
//					}
				}catch(DecoderException e){
					scan = false ;
					//print exception
					LogUtil.LogE("ScanTask", e.toString()) ;
					LogUtil.SaveException("DecoderException", e.toString()) ;
					if(e.getErrorCode() == 5)
					closeDev() ;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				scan = false ;
				taskOK = true ;
			}
			
		}
		
	}

	/***
	 * 调用扫描
	 */
	@Override
	public void scan() {
		if(!scan ){
			scan = true ;
		}
		
//		//启动扫描时，判断是否正在调用
//		if(!isScanning){
//			isScanning = true ;
//			//创建新的线程用于调用扫描
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					try {
//						//扫描5秒的延时
//						mDecoder.waitForDecodeTwo(2000, mDecodeResult);
//						//得到结果回调
//						if(mDecodeResult.barcodeData != null && mDecodeResult.barcodeData.length() > 0){
//							if(result != null){
//								result.onListne(mDecodeResult.barcodeData, mDecodeResult.byteBarcodeData) ;
//							}
//						}
//					} catch (DecoderException e) {
//						LogUtil.SaveException(TAG, e.toString()) ;
////						e.printStackTrace();
//						
//					}
//					isScanning = false ;
//					
//				}
//			}).start();
//		}
		
	}

	/***
	 * 设置回调输出接口
	 */
	@Override
	public void setOnResultListener(IScanResult iLister) {
		
		this.result = iLister ;
	}

	/**
	 * 设置编码格式，主要是应对带中文的二维码
	 */
	@Override
	public void setChar(String charSetName) {
		
		
	}

	
}
