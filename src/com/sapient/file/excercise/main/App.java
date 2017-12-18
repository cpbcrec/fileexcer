package com.sapient.file.excercise.main;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.sapient.file.excercise.consumer.runnable.FileConsumerTask;
import com.sapient.file.excercise.directory.wrapper.DirectoryWrapper;
import com.sapient.file.excercise.producer.runnable.FileProducerTask;

public class App 
{
	private static Map<String, Long> lookMap = new HashMap<>();
	private static BlockingQueue<DirectoryWrapper>  queue = new LinkedBlockingQueue<>();
	
	public static void main(String args[])
	{
		ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor(); 
		scheduledExecutor.scheduleAtFixedRate(new FileProducerTask(queue, lookMap), 0, 5, TimeUnit.SECONDS);
		
		new Thread( new FileConsumerTask(queue)).start();
	}
}
