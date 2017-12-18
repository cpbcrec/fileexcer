package com.sapient.file.excercise.util;

import java.util.Map;

public class FileResultHolder 
{
	private String fileName;
	private Map<Counter, Long> result;
	
	public FileResultHolder() {	}
	
	public FileResultHolder(String fileName) 
	{
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}
	public void setFileNme(String fileNme) {
		this.fileName = fileNme;
	}
	public Map<Counter, Long> getResult() {
		return result;
	}
	public void setResult(Map<Counter, Long> result) {
		this.result = result;
	}
	
}
