package com.scan.scanner;
/**
 * 扫描结果回调
 * @author admin
 *
 */
public interface IScanResult {
	void onListne(String barcode, byte[] barcodeByte) ;
}
