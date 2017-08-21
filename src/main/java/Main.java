package main.java;

import java.io.File;
import java.util.List;
import java.util.Map;

import main.java.csv.CsvParams;
import main.java.csv.MapCsvReader;

public class Main
{
	public static void main(String... args)
	{
		try
		{
			// parse input paths
			CsvTransformArguments arguments = parseArgs(args);

			// parse config-file
			ConfigReader configreader = new ConfigReader(new File(arguments.pathConfigFile));
			configreader.parse();
			CsvParams inputFileParams = configreader.getInputFileParams();
			CsvParams ouputFileParams = configreader.getOutputFileParams();
			List<Rule> rules = configreader.getRules();
			
			/*debugPrintRules(rules);
			debugPrint(inputFileParams, "inputFileParams");
			debugPrint(ouputFileParams, "outputFileParams");*/

			// read input file into beans
			MapCsvReader csvreader = new MapCsvReader(new File(arguments.pathInputFile));
			csvreader.setCsvParams(inputFileParams);
			List<Map<String, Object>> inputCsvData = csvreader.getBeans();
			debugPrint(inputCsvData);

			// TODO write beans to outputfile

			System.out.println("terminated successfully.");
		}
		catch (Exception e)
		{
			// catch any Exception and present it to user in a proper way
			// TODO
			System.out.println("TODO Exception was catched");
			e.printStackTrace();
		}
	}

	/**
	 * TODO remove this debug method
	 */
	private static void debugPrintRules(List<Rule> rules)
	{
		System.out.println("rules:  ---  ");
		for (Rule r : rules)
		{
			System.out.println(r);
		}
		System.out.println("  ---  ");
	}

	private static void debugPrint(CsvParams p, String msg)
	{
		if (msg != null)
			System.out.println(msg);

		System.out.println(p.toString());
	}

	/**
	 * TODO remove this debug method
	 */
	private static void debugPrint(List<Map<String, Object>> inputCsvData)
	{
		for (Map<String, Object> map : inputCsvData)
		{
			for (String key : map.keySet())
			{
				System.out.println(key + "\t -> " + map.get(key));
			}
			System.out.println(" --- ");
		}
	}

	private static CsvTransformArguments parseArgs(String... args)
	{
		if (args.length != 3)
			error(ErrorCode.ERROR_WRONG_NUMBER_ARGUMENTS, "Must be exactly 3, but were " + args.length + ".");

		CsvTransformArguments bean = new CsvTransformArguments();
		bean.pathInputFile = args[0];
		bean.pathOutputFile = args[1];
		bean.pathConfigFile = args[2];

		System.out.println("\nUsing the following files:\n" + " input  : " + bean.pathInputFile + "\n" + " output : "
				+ bean.pathOutputFile + "\n" + " config : " + bean.pathConfigFile + "\n");

		return bean;
	}

	public static void error(ErrorCode errorCode)
	{
		error(errorCode, "");
	}

	public static void error(ErrorCode errorCode, String additionalMsg)
	{
		System.err.println(errorCode.getMsg());
		System.err.println(additionalMsg);
		printHelp();
		System.exit(errorCode.getN());
	}

	private static void printHelp()
	{
		System.out.println("");
		System.out.println("This is the help text");
		System.out.println("---------------------");
		System.out.println("");
		System.out.println("Call this programm like:\n\n" + "java -jar csvtool.jar input.csv output.csv config.txt\n\n"
		// TODO
				+ "input.csv  : TODO \n" + "output.csv : TODO \n" + "config.txt : TODO \n" + "");

		System.out.println("");
	}
}
