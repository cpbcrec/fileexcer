package com.sapient.file.excercise.file.runnable;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Callable;

import com.sapient.file.excercise.util.Counter;
import com.sapient.file.excercise.util.FileResultHolder;

public class FileCalculatorRunnable implements Callable<FileResultHolder>
{
	private String fileName;

	public FileCalculatorRunnable(String fileName) 
	{
		this.fileName = fileName;
	}

	@Override
	public FileResultHolder call() throws Exception 
	{
		System.out.println(Thread.currentThread().getName() + " is executing file :" + fileName);
		Map<Counter, Long> fileResult = calculateFileResult(); 
		FileResultHolder obj = new FileResultHolder();
		obj.setFileNme(fileName); 
		obj.setResult(fileResult);
		return obj;
	}

	private Map<Counter, Long> calculateFileResult() 
	{
		String words = String.valueOf(0); 
		String line = String.valueOf(0);
		Long countword = 0l;
		Long countCharacters = 0l;
		Long vowelCount = 0l;
		Map<Counter, Long> resultMap = new HashMap<>();

		Scanner input;
		try 
		{
			input = new Scanner(new FileReader(this.fileName));

			if (!input.hasNext()) {
				System.out.println("File is empty. Setting default resultMap with 0 count");
				this.getDefaultResultMap(resultMap);
				return resultMap;
			}

			while (input.hasNextLine()) {
				line = input.nextLine();
				Scanner inLine = new Scanner(line);
				while (inLine.hasNext()) {
					words = inLine.next();
					countword++;
				}
				countCharacters += line.length();
				for (int i = 0; i < line.length(); i++) {
					char c = line.charAt(i);
					if ((c == 'a') || (c == 'e') || (c == 'i') || (c == 'o') || (c == 'u'))
						vowelCount++;
				}
			}

			System.out.println("Number of words: " + countword);
			System.out.println("Number of vowels: " + vowelCount);
			System.out.println("Number of letters: " + countCharacters);

			resultMap.put(Counter.WORDS, countword);
			resultMap.put(Counter.LETTERS, countCharacters);
			resultMap.put(Counter.VOWELS, vowelCount);
			resultMap.put(Counter.SPECIAL_CHAR, 0l);

		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		return resultMap;
	}

	private void getDefaultResultMap(Map<Counter, Long> resultMap)
	{
		resultMap.put(Counter.WORDS, 0l);
		resultMap.put(Counter.LETTERS, 0l);
		resultMap.put(Counter.VOWELS, 0l);
		resultMap.put(Counter.SPECIAL_CHAR, 0l);
	} 

}
