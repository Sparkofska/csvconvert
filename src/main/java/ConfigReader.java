package main.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import main.java.csv.CsvParams;
import main.java.csv.IllformedConfigFileException;

public class ConfigReader
{
	private String commentPrefix = "#";
	private char equalsChar = '=';
	private char ruleChar = '>';
	private char defvalChar = ':';
	private char escapeChar = '\"';

	private List<Rule> rules;
	private CsvParams inputFileParams;
	private CsvParams outputFileParams;

	private File file;
	private BufferedReader reader;

	public ConfigReader(File file) throws FileNotFoundException
	{
		this.file = file;
		this.reader = new BufferedReader(new FileReader(file));
	}

	public ConfigReader(Reader reader)
	{
		this.reader = new BufferedReader(reader);
	}

	public void parse() throws IOException
	{
		String line = "";
		while ((line = this.reader.readLine()) != null)
			onLine(line);
	}

	private int lineCounter = 0;

	private void onLine(String line)
	{
		lineCounter++;
		// ignore empty lines
		if (line == null)
			return;
		line = stripWhitespace(line);
		if (line.isEmpty())
			return;
		// ignore comments
		if (line.startsWith(getCommentPrefix()))
			return;

		String value1 = null;
		String value2 = null;
		String value3 = null;
		Character splitCharacter = null;

		boolean inEscapeSequence = false;
		int inEscapeSequenceCounter = 0;

		StringBuffer curVal = new StringBuffer();
		char[] chars = line.toCharArray();
		for (int i = 0; i < chars.length; i++)
		{
			char ch = chars[i];

			if (inEscapeSequence)
			{
				if (ch == escapeChar)
				{
					// allow escaped escape-char in escape-sequence
					if (i < chars.length - 1 && chars[i + 1] == escapeChar)
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
				if (ch == escapeChar)
				{
					inEscapeSequence = true;
					inEscapeSequenceCounter = 0;
					continue;
				}

				if (ch == equalsChar || ch == ruleChar)
				{
					if (splitCharacter != null)
						throw new IllformedConfigFileException("Too many '" + ch + "'s in line " + lineCounter);
					splitCharacter = ch;

					value1 = curVal.toString();
					curVal = new StringBuffer();
					continue;
				}
				if (ch == defvalChar)
				{
					if (value2 != null)
						throw new IllformedConfigFileException("Too many '" + defvalChar + "'s in line " + lineCounter);
					value2 = curVal.toString();
					curVal = new StringBuffer();
					continue;
				}
				curVal.append(ch);
			}
		}

		if (inEscapeSequence)
			throw new IllformedConfigFileException("Improper number of '" + escapeChar + "'s in line " + lineCounter);

		if (value2 == null)
			value2 = curVal.toString();
		else
			value3 = curVal.toString();

		if (value1 == null || value2 == null)
			throw new IllformedConfigFileException("Syntax-Error in line " + lineCounter + ". Maybe no '" + equalsChar
					+ "' or '" + ruleChar + "' found or problem with the escape character '" + escapeChar + "'.");

		value1 = stripWhitespace(value1);
		value2 = stripWhitespace(value2);
		value3 = stripWhitespace(value3);

		if (splitCharacter == equalsChar)
		{
			setSetting(value1, value2);
		}
		else if (splitCharacter == ruleChar)
		{
			addRule(value1, value2, value3);
		}
		else
			throw new IllformedConfigFileException(
					"No '" + equalsChar + "' or '" + ruleChar + "' in line" + lineCounter);

	}

	private void addRule(String originColumn, String destinationColumn, String defaultValue)
	{
		Rule rule = new Rule();
		rule.setOriginColumn(originColumn);
		rule.setDestinationColumn(destinationColumn);
		rule.setDefaultValue(defaultValue);
		getRules().add(rule);
	}

	private void setSetting(String value1, String value2)
	{
		if (value1 == null || value1.isEmpty())
			throw new IllformedConfigFileException("No setting given in line " + lineCounter);

		switch (value1)
		{
			case "inputEncoding":
				getInputFileParams().encoding = value2;
				break;
			case "inputSplitChar":
				if (value2.length() > 1)
					throw new IllformedConfigFileException(
							value1 + " value is too long; can only have 1 character. In line " + lineCounter);
				getInputFileParams().splitChar = value2.charAt(0);
				break;
			case "inputEscapeChar":
				if (value2.length() > 1)
					throw new IllformedConfigFileException(
							value1 + " value is too long; can only have 1 character. In line " + lineCounter);
				getInputFileParams().escapeChar = value2.charAt(0);
				break;

			case "outputEncoding":
				getOutputFileParams().encoding = value2;
				break;
			case "ouputSplitChar":
				if (value2.length() > 1)
					throw new IllformedConfigFileException(
							value1 + " value is too long; can only have 1 character. In line " + lineCounter);
				getOutputFileParams().splitChar = value2.charAt(0);
				break;
			case "outputEscapeChar":
				if (value2.length() > 1)
					throw new IllformedConfigFileException(
							value1 + " value is too long; can only have 1 character. In line " + lineCounter);
				getOutputFileParams().escapeChar = value2.charAt(0);
				break;

			default:
				throw new IllformedConfigFileException("Unknown Setting '" + value1 + "' in line " + lineCounter + ".");
		}
	}

	private String stripWhitespace(String s)
	{
		if (s == null)
			return null;
		return s.trim();
	}

	public String getCommentPrefix()
	{
		return commentPrefix;
	}

	public void setCommentPrefix(String commentPrefix)
	{
		this.commentPrefix = commentPrefix;
	}

	public List<Rule> getRules()
	{
		if (this.rules == null)
			this.rules = new ArrayList<>();
		return this.rules;
	}

	public CsvParams getInputFileParams()
	{
		if (this.inputFileParams == null)
			this.inputFileParams = CsvParams.getDefaultCsvParams();
		return inputFileParams;
	}

	public CsvParams getOutputFileParams()
	{
		if (this.outputFileParams == null)
			this.outputFileParams = CsvParams.getDefaultCsvParams();
		return outputFileParams;
	}
}
