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
	private String			delimiter		= ";";
	private String			quoteCharacter	= "\"";
	private BufferedReader	reader;

	public PureCsvReader(File csvFile) throws IOException
	{
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
			throw new IllegalArgumentException("Die Datei ist leer.");
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

	/**
	 * Thanks to https://www.mkyong.com/java/how-to-read-and-parse-csv-file-in-java/
	 * 
	 * @param cvsLine
	 * @return
	 */
	public List<String> parseLine(String cvsLine)
	{

		List<String> result = new ArrayList<>();

		// if empty, return!
		if (cvsLine == null || cvsLine.isEmpty())
		{
			return result;
		}

		StringBuffer curVal = new StringBuffer();
		boolean inQuotes = false;
		boolean startCollectChar = false;
		boolean doubleQuotesInColumn = false;

		char[] chars = cvsLine.toCharArray();

		for (char ch : chars)
		{

			if (inQuotes)
			{
				startCollectChar = true;
				if (ch == quoteCharacter.charAt(0))
				{
					inQuotes = false;
					doubleQuotesInColumn = false;
				}
				else
				{

					// Fixed : allow "" in custom quote enclosed
					if (ch == '\"')
					{
						if (!doubleQuotesInColumn)
						{
							curVal.append(ch);
							doubleQuotesInColumn = true;
						}
					}
					else
					{
						curVal.append(ch);
					}
				}
			}
			else
			{
				if (ch == quoteCharacter.charAt(0))
				{

					inQuotes = true;

					// Fixed : allow "" in empty quote enclosed
					if (chars[0] != '"' && quoteCharacter.charAt(0) == '\"')
					{
						curVal.append('"');
					}

					// double quotes in column will hit this!
					if (startCollectChar)
					{
						curVal.append('"');
					}

				}
				else if (ch == delimiter.charAt(0))
				{

					result.add(curVal.toString());

					curVal = new StringBuffer();
					startCollectChar = false;

				}
				else if (ch == '\r')
				{
					// ignore LF characters
					continue;
				}
				else if (ch == '\n')
				{
					// the end, break!
					break;
				}
				else
				{
					curVal.append(ch);
				}
			}

		}

		result.add(curVal.toString());

		return result;
	}

	public String getDelimiter()
	{
		return delimiter;
	}

	/**
	 * changes the delimiter, which is used for separation of two values.<br>
	 * default value is <code>";"</code>
	 */
	public void setDelimiter(String delimiter)
	{
		this.delimiter = delimiter;
	}

	public String getQuoteCharacter()
	{
		return quoteCharacter;
	}

	public void setQuoteCharacter(String quoteCharacter)
	{
		this.quoteCharacter = quoteCharacter;
	}

}
