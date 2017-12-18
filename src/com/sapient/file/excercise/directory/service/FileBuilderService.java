package com.sapient.file.excercise.directory.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import com.sapient.file.excercise.util.Counter;
import com.sapient.file.excercise.util.FileResultHolder;
import com.sapient.file.excercise.util.Sorter;

public class FileBuilderService 
{
	public void createOutputFromFileResult(FileResultHolder fileResult, String extension) 
	{
		switch (extension) 
		{
		case "MTD":
			createMTDFileFromResultList(fileResult);
			break;
		case "DMTD":
			createDMTDFileFromResultList(fileResult);
			break;
		default:
			break;
		}
	}
	
	private static void createDMTDFileFromResultList(FileResultHolder fileResult) 
	{
		PrintStream out;
		try {
			String directoryName = fileResult.getFileName();
			System.out.println("Creating DMTD for directory : " + directoryName);

			out = new PrintStream(
					new File(directoryName + "/" + directoryName.substring(directoryName.lastIndexOf("\\") + 1))
							+ ".dmtd");

			writeResultMapToFile(fileResult.getResult(), out);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static void writeResultMapToFile(Map<Counter, Long> resultMap, PrintStream out) 
	{
		resultMap.forEach((k,v) -> out.println(k + " : " + v));
	}

	public void createSMTDFileFromResultList(List<FileResultHolder> fileResultList, String directoryName, Sorter sortOrder, Counter sortParam) {
		PrintStream out;
		try {
			System.out.println("Creating SMTD for directory : " + directoryName);
			
			out = new PrintStream(
					new File(directoryName + "/" + directoryName.substring(directoryName.lastIndexOf("\\") + 1))
							+ ".smtd");
			
			out.println("Result Sorted " + sortOrder + " on " + sortParam);

			fileResultList.stream().forEach(fileResult -> {
				out.println(fileResult.getFileName());
				writeResultMapToFile(fileResult.getResult(), out);
				out.println();
			});

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void createMTDFileFromResultList(FileResultHolder fileResult) 
	{
		PrintStream out;
		try {
			System.out.println("Creating MTD for file : " + fileResult.getFileName());
			
			String file = fileResult.getFileName().replaceFirst("[.][^.]+$", ""); 
			out = new PrintStream(new File(file + ".mtd"));
			writeResultMapToFile(fileResult.getResult(), out);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
