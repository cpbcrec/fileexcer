package com.sapient.file.excercise.util;

public enum Sorter 
{
	ASC(1), DESC(-1);

	private int multiplier;

	private Sorter(int multiplier) {
		this.setMultiplier(multiplier);
	}

	public int getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(int multiplier) {
		this.multiplier = multiplier;
	}
}
