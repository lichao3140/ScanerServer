package com.scan.scanner;
/**
 * 扫描接口
 * @author admin
 *
 */
public interface IScaner {
	
	int initDev();  //init scaner engine
	
	void closeDev();
	
	void scan();  //start scanning 
	
	void setOnResultListener(IScanResult iLister); //listen scan result
	
	void setChar(String charSetName);
}
