package com.sapient.file.excercise.producer.runnable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.sapient.file.excercise.directory.wrapper.DirectoryWrapper;

public class FileProducerTask implements Runnable {
	BlockingQueue<DirectoryWrapper> queue = null;
	Map<String, Long> lookMap = null;

	public FileProducerTask(BlockingQueue<DirectoryWrapper> queue, Map<String, Long> lookMap) {
		this.queue = queue;
		this.lookMap = lookMap;
	}

	@Override
	public void run() {
		System.out.println("Scanning started");
		Path path = Paths.get("E:/dest");
		List<File> listOfFiles = new ArrayList<>();
		try {
			Files.walk(path).filter(Files::isRegularFile).forEach(filePath -> {
				String file = filePath.getFileName().toString();
				String ext = file.substring(file.lastIndexOf("."));
				if (ext.equals(".txt") || ext.equals(".csv")) {
					listOfFiles.add(filePath.toFile());
				}
			});
			Map<String, List<File>> mapOfFiles = listOfFiles.stream()
					.filter(file -> (!lookMap.containsKey(file.getAbsolutePath())
							|| lookMap.get(file.getAbsolutePath()) != file.lastModified()))
					.collect(Collectors.groupingBy(File::getParent, HashMap::new,
							Collectors.mapping(Function.identity(), Collectors.toList())));
			mapOfFiles.forEach((key1, key2)-> {
				try 
				{
					DirectoryWrapper wrapper = new DirectoryWrapper();
					wrapper.setDirName(key1);
					List<String> list = new ArrayList<>();
					key2.stream().forEach(k -> {
						list.add(k.getAbsolutePath());
						lookMap.put(k.getAbsolutePath(), k.lastModified());
					});
					wrapper.setFileName(list); 
					queue.add(wrapper);
				}  
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
			System.out.println(queue); 
		}
		catch (IOException e) 
		{
			System.out.println(e.getMessage());
		}
		
		System.out.println("Scanning Completed");
	}

}
