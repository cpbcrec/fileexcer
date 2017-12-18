package com.sapient.file.excercise.directory.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import com.sapient.file.excercise.directory.wrapper.DirectoryWrapper;
import com.sapient.file.excercise.file.runnable.FileCalculatorRunnable;
import com.sapient.file.excercise.util.Counter;
import com.sapient.file.excercise.util.FileResultComparator;
import com.sapient.file.excercise.util.FileResultHolder;
import com.sapient.file.excercise.util.Sorter;

public class DirectoryProcessService 
{
	ExecutorService executor = Executors.newFixedThreadPool(5);

	private DirectoryWrapper wrapper;

	private Sorter sortOrder;

	private Counter countParam;

	private FileBuilderService fileBuilderService;
	

	public Sorter getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Sorter sortOrder) {
		this.sortOrder = sortOrder;
	}

	public Counter getCountParam() {
		return countParam;
	}

	public void setCountParam(Counter countParam) {
		this.countParam = countParam;
	}

	public DirectoryProcessService(DirectoryWrapper wrapper) 
	{
		this.wrapper = wrapper;
		this.fileBuilderService = new FileBuilderService();
	}

	public void process() throws IOException
	{
		List<Future<FileResultHolder>> results = new ArrayList<>(); 

		wrapper.getFileName().stream().forEach(file -> {

			Future<FileResultHolder> futureResult = executor.submit(new FileCalculatorRunnable(file)); 
			results.add(futureResult);
			System.out.println(file + " submited for .mtd creation");
			System.out.println(results); 
		});

		System.out.println("Getting existing MTD result List for " + wrapper.getDirName());
		
		List<FileResultHolder> fileResultList = this.getAlreadyCalculatedMTDResult();
		System.out.println("Fetching existing MTD result List for " + wrapper.getDirName() + " completed");

		results.stream().forEach(future -> {
			try 
			{
				fileResultList.add(future.get());
			} 
			catch (InterruptedException | ExecutionException e) 
			{
				//
			}
		});

		System.out.println(wrapper.getDirName() + "Submitted for DMTD and SMTD");
		executor.submit(() -> this.calculateAndCreateDMTDFile(fileResultList));
		executor.submit(() -> this.calculateAndCreateSMTDFile(fileResultList));
		System.out.println("Processing of " + wrapper.getDirName() + " ended.");

	}

	private void calculateAndCreateSMTDFile(List<FileResultHolder> fileResultList) 
	{
		System.out.println("Sorting MTDS for " + wrapper.getDirName() + " Directory");
		Collections.sort(fileResultList, new FileResultComparator(countParam, sortOrder));
		fileResultList.stream().forEach(fileResult -> {
			System.out.println(fileResult.toString());
		});

		System.out.println("Calling Output publisher for SMTD of " + wrapper.getDirName() + " Directory");

		fileBuilderService.createSMTDFileFromResultList(fileResultList, wrapper.getDirName(),sortOrder,countParam);
	}

	private void calculateAndCreateDMTDFile(List<FileResultHolder> fileResultList) {
		System.out.println("Calculating DMTD result for " + wrapper.getDirName());
		FileResultHolder directoryResult = new FileResultHolder(wrapper.getDirName());
		Long letterCount = 0l, vowelCount = 0l, wordCount = 0l, specialCharCount = 0l;

		for (FileResultHolder fileResult : fileResultList) {
			for (Map.Entry<Counter, Long> entry : fileResult.getResult().entrySet()) 
			{
				switch (entry.getKey()) 
				{
				case LETTERS:
					letterCount = letterCount + entry.getValue();
					break;
				case VOWELS:
					vowelCount = vowelCount + entry.getValue();
					break;
				case WORDS:
					wordCount = wordCount + entry.getValue();
					break;
				case SPECIAL_CHAR:
					specialCharCount = specialCharCount + entry.getValue();
					break;
				}
			}
		}

		Map<Counter, Long> directoryResultMap = new HashMap<>();
		directoryResultMap.put(Counter.LETTERS, letterCount);
		directoryResultMap.put(Counter.VOWELS, vowelCount);
		directoryResultMap.put(Counter.WORDS, wordCount);
		directoryResultMap.put(Counter.SPECIAL_CHAR, specialCharCount);
		directoryResult.setResult(directoryResultMap);

		System.out.println("Calling Output Publisher for publishing DMTD for " + wrapper.getDirName());
		fileBuilderService.createOutputFromFileResult(directoryResult, "DMTD");

		System.out.println("calculateAndCreateDMTDResult");
	}

	private List<FileResultHolder> getAlreadyCalculatedMTDResult() throws IOException 
	{
		Path path = Paths.get("E:/dest");
		List<File> allMTDfiles = new ArrayList<>();
		Files.walk(path).filter(Files::isRegularFile).forEach(filePath -> {
			String file = filePath.getFileName().toString();
			String ext = file.substring(file.lastIndexOf("."));
			if (ext.equals(".mtd")) {
				allMTDfiles.add(filePath.toFile());
			}
		});
		System.out.println(allMTDfiles);
		Collection<File> filterMTDs = allMTDfiles.stream()
				.filter(file -> this.exists(wrapper.getFileName(),file.getAbsolutePath())).collect(Collectors.toList());

		return this.convertMTDsToFileResultList(filterMTDs);
	}

	private boolean exists(List<String> files, String absolutePath) 
	{
		for (String file : files) 
		{
			file.replaceFirst("[.][^.]+$", "");
			if (file.equals(absolutePath.replaceFirst("[.][^.]+$", "")))
			{
				return false;
			}
		}
		return true;
	}

	private List<FileResultHolder> convertMTDsToFileResultList(Collection<File> filterMTDs) {
		List<FileResultHolder> fileResultList = new ArrayList<>();

		filterMTDs.stream().forEach(mtd -> {
			FileResultHolder fileResult = new FileResultHolder(mtd.getAbsolutePath());

			Map<Counter, Long> resultMap = this.getCountResultMapFromFile(mtd.getAbsolutePath());

			fileResult.setResult(resultMap);
			fileResultList.add(fileResult);
		});

		return fileResultList;
	}

	private Map<Counter, Long> getCountResultMapFromFile(String filePath) 
	{
		Map<Counter, Long> resultMap = new HashMap<>();
		Scanner input;
		String line;
		try {

			input = new Scanner(new FileReader(filePath));

			if (!input.hasNext()) {
				System.out.println("File is empty. Setting default resultMap with 0 count");
				return this.getDefaultResultMap(resultMap);
			}

			while (input.hasNextLine()) {

				line = input.nextLine();
				String[] splitResult = line.split(" : ");
				resultMap.put(Counter.getEnumFromParam(splitResult[0]), Long.parseLong(splitResult[1]));
			}
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		return resultMap;
	}

	private Map<Counter, Long> getDefaultResultMap(Map<Counter, Long> resultMap) 
	{
		resultMap.put(Counter.WORDS, 0l);
		resultMap.put(Counter.LETTERS, 0l);
		resultMap.put(Counter.VOWELS, 0l);
		resultMap.put(Counter.SPECIAL_CHAR, 0l);
		return resultMap; 
	}
}
