package com.scan.service;

interface IScanResult{
	void onListener(String barcode,in byte[] bar);
	
}