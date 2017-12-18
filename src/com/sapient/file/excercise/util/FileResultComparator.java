package com.sapient.file.excercise.util;

import java.util.Comparator;

public class FileResultComparator implements Comparator<FileResultHolder> 
{
	private Counter countParam;
	private Sorter sortOrder;
	
	public FileResultComparator(Counter countParam, Sorter sortOrder) 
	{
		this.countParam = countParam;
		this.sortOrder = sortOrder;
	}

	@Override
	public int compare(FileResultHolder o1, FileResultHolder o2) 
	{
		return (o1.getResult().get(countParam).compareTo(o2.getResult().get(countParam))) * sortOrder.getMultiplier();
	}

}
