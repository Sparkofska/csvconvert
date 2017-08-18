package main.java;

import java.io.File;

public class Main
{
	public static void main(String... args)
	{
		System.out.println("Hello World");
		
		System.out.println("# args: " + args.length);
		for(String arg:args)
		{
			System.out.println(" - " + arg);
			
			File file = new File(arg);
			System.out.println("exists: " + file.exists());
		}
		
		// parse input paths
		
		// read input file into beans
		
		// write beans to outputfile
		
		System.out.println("terminated successfully.");
	}
}
