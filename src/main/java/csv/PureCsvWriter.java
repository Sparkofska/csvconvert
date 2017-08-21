package main.java.csv;

import java.io.IOException;
import java.io.Writer;

/**
 * 
 * @author Jonas Münch
 * @since Jan 2016
 */
public class PureCsvWriter
{
	private CsvParams csvParams = CsvParams.getDefaultCsvParams();

	private Writer out;
	private boolean lastWasNewLine = true;

	/**
	 * 
	 * @param out
	 *            Defines where to ouput the csv content.
	 */
	public PureCsvWriter(Writer out)
	{
		this.out = out;
	}

	public PureCsvWriter(Writer out, CsvParams csvParams)
	{
		this.out = out;
		setCsvParams(csvParams);
	}

	public void emitCell(String content) throws IOException
	{
		writeCell(content);
	}

	public void emitCell(int content) throws IOException
	{
		writeCell(content + "");
	}

	public void emitCell(Object content) throws IOException
	{
		writeCell(content.toString());
	}

	protected void writeCell(String content) throws IOException
	{
		content = content.trim();
		// escaping the quoteCharacter by doubling it
		content = content.replace(String.valueOf(csvParams.escapeChar),
				String.valueOf(csvParams.escapeChar) + String.valueOf(csvParams.escapeChar));

		// quoting the content if it contains delimiter
		if (content.contains(String.valueOf(csvParams.splitChar)))
			content = csvParams.escapeChar + content + csvParams.escapeChar;

		// write delimiter if necessary
		if (!lastWasNewLine)
			out.write(csvParams.splitChar);

		lastWasNewLine = false;
		out.write(content);
	}

	public void emitNewline() throws IOException
	{
		out.write(csvParams.newline);
		lastWasNewLine = true;
	}

	public void flush() throws IOException
	{
		out.flush();
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
