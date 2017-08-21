package main.java;

import java.io.File;
import java.util.List;
import java.util.Map;

import main.java.csv.CsvParams;
import main.java.csv.MapCsvReader;
import main.java.csv.TransformCsvWriter;

public class Main
{
	public static void main(String... args)
	{
		// parse input paths
		CsvTransformArguments arguments = parseArgs(args);
		try
		{
			// parse config-file
			ConfigReader configreader = new ConfigReader(new File(arguments.pathConfigFile));
			configreader.parse();
			CsvParams inputFileParams = configreader.getInputFileParams();
			CsvParams ouputFileParams = configreader.getOutputFileParams();
			List<Rule> rules = configreader.getRules();

			// read input file into beans
			MapCsvReader csvreader = new MapCsvReader(new File(arguments.pathInputFile), inputFileParams);
			List<Map<String, Object>> inputCsvData = csvreader.getBeans();

			// write beans to outputfile
			TransformCsvWriter writer = new TransformCsvWriter(new File(arguments.pathOutputFile), rules,
					ouputFileParams);
			writer.emit(inputCsvData);
			writer.close();

			System.out.println("Ouput successfully written to " + arguments.pathOutputFile + ".");
		}
		catch (Exception e)
		{
			// catch any Exception and present it to user in a proper way
			String exName = e.getClass().getName();
			System.out.println(exName + ": " + e.getMessage());
			 e.printStackTrace();
		}
	}

	private static CsvTransformArguments parseArgs(String... args)
	{
		if (args.length != 3)
		{
			printHelp();
			throw new IllegalArgumentException(
					"Number of program Arguments must be exactly 3, but was " + args.length + ".");
		}

		CsvTransformArguments bean = new CsvTransformArguments();
		bean.pathInputFile = args[0];
		bean.pathOutputFile = args[1];
		bean.pathConfigFile = args[2];

		System.out.println("\nUsing the following files:\n" + " input  : " + bean.pathInputFile + "\n" + " output : "
				+ bean.pathOutputFile + "\n" + " config : " + bean.pathConfigFile + "\n");

		return bean;
	}

	private static void printHelp()
	{
		System.out.println("");
		System.out.println("This is the help text");
		System.out.println("---------------------");
		System.out.println("");
		System.out.println(
				"Call this programm like:\n\n" + "java -jar csvtransform.jar input.csv output.csv config.txt\n\n"
						+ "input.csv  :\n A csv file containing all the data that should be transformed. This file will remain the same after execution. \n"
						+ "output.csv :\n This file (is created if not exists) will be overwritten by the program. All the transformed data will be placed here after execution. \n"
						+ "config.txt :\n A file with a special syntax that defines all the transformation rules. \n\n"
						+ "For further information and config.txt-syntax see https://github.com/Sparkofska/csvtransform");

		System.out.println("");
	}

	/**
	 * This class is a little C-Style struct. Representating the arguments
	 * passed to program.
	 * 
	 * @author jonas
	 *
	 */
	public static class CsvTransformArguments
	{
		public String pathInputFile;
		public String pathOutputFile;
		public String pathConfigFile;
	}

}
