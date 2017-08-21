package main.java.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public abstract class PureCsvReader
{
	private CsvParams csvParams = CsvParams.getDefaultCsvParams();
	private BufferedReader reader;

	public PureCsvReader(File csvFile) throws IOException
	{
		// TODO get charset from csvParams
		this(csvFile, "ISO8859_1");
	}

	public PureCsvReader(File csvFile, String charset) throws IOException
	{
		reader = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), charset));
	}

	public PureCsvReader(Reader reader)
	{
		if (reader instanceof BufferedReader)
			this.reader = (BufferedReader) reader;
		else
			this.reader = new BufferedReader(reader);
	}

	/**
	 * Gets called once after the header of the csv file was read.
	 * 
	 * @param list
	 *            A List containing the headers as Strings (readily parsed).
	 */
	protected abstract void onHeaders(List<String> list);

	/**
	 * Gets called for each line in the csv File.
	 * 
	 * @param list
	 *            A List containing the values as Strings (readily parsed)
	 */
	protected abstract void onLine(int index, List<String> list);

	public void read() throws IOException
	{
		String headers;
		if ((headers = reader.readLine()) == null)
		{
			throw new IllformedCsvException("Empty file was given.");
		}
		else
		{
			onHeaders(parseLine(headers));
		}

		int i = 0;
		String line;
		while ((line = reader.readLine()) != null)
		{
			onLine(i, parseLine(line));
			i++;
		}
	}

	private int lineCounter = 0;

	public List<String> parseLine(String line)
	{
		lineCounter++;

		ArrayList<String> result = new ArrayList<>();

		// ignore empty lines
		if (line == null)
			return result;
		line = line.trim();
		if (line.isEmpty())
			return result;

		boolean inEscapeSequence = false;
		int inEscapeSequenceCounter = 0;

		StringBuffer curVal = new StringBuffer();
		char[] chars = line.toCharArray();
		for (int i = 0; i < chars.length; i++)
		{
			char ch = chars[i];

			if (inEscapeSequence)
			{
				if (ch == csvParams.escapeChar)
				{
					// allow escaped escape-char in escape-sequence
					if (i < chars.length - 1 && chars[i + 1] == csvParams.escapeChar)
					{
						i++;
						curVal.append(ch);
						continue;
					}
					if (inEscapeSequenceCounter == 0)
						curVal.append(ch);
					inEscapeSequence = false;
					continue;
				}
				inEscapeSequenceCounter++;
				curVal.append(ch);
			}
			else
			{
				if (ch == csvParams.escapeChar)
				{
					inEscapeSequence = true;
					inEscapeSequenceCounter = 0;
					continue;
				}

				if (ch == csvParams.splitChar)
				{
					result.add(curVal.toString());
					curVal = new StringBuffer();
					continue;
				}
				curVal.append(ch);
			}
		}

		if (inEscapeSequence)
			throw new IllformedConfigFileException(
					"Improper number of '" + csvParams.escapeChar + "'s in line " + lineCounter);

		return result;
	}

	public CsvParams getCsvParams()
	{
		return csvParams;
	}

	public void setCsvParams(CsvParams csvParams)
	{
		this.csvParams = csvParams;
	}

}
