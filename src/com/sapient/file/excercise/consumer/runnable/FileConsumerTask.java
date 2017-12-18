package com.sapient.file.excercise.consumer.runnable;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import com.sapient.file.excercise.directory.service.DirectoryProcessService;
import com.sapient.file.excercise.directory.wrapper.DirectoryWrapper;

public class FileConsumerTask implements Runnable
{
	BlockingQueue<DirectoryWrapper> queue = null;
	
	public FileConsumerTask(BlockingQueue<DirectoryWrapper> queue) 
	{
		this.queue = queue;
	}

	@Override
	public void run() 
	{
		while(true)
		{
			try 
			{
				DirectoryWrapper take = queue.take();
				System.out.println("Taking from queue " + take);
				DirectoryProcessService obj = new DirectoryProcessService(take);
				obj.process();
			} 
			catch (InterruptedException | IOException e)  
			{
				e.printStackTrace();
			} 
		}
	}
}
