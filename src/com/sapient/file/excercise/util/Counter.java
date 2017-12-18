package com.sapient.file.excercise.util;

public enum Counter 
{
	WORDS("WORDS"), LETTERS("LETTERS"), VOWELS("VOWELS"), SPECIAL_CHAR("SPECIAL_CHAR");

	private String param;

	Counter(String param){
		this.setParam(param);
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public static Counter getEnumFromParam(String iParam) {
		for (Counter c : Counter.values()) {
			if(c.getParam().equals(iParam)) {
				return c;
			}
		}
		return null;
	} 
}
