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
	private String	newline			= "\r\n";
	private String	delimiter		= ";";
	private String	quoteCharacter	= "\"";

	private Writer	out;
	private boolean	lastWasNewLine	= true;

	/**
	 * 
	 * @param out Defines where to ouput the csv content.
	 */
	public PureCsvWriter(Writer out)
	{
		this.out = out;
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
		content = content.replace(quoteCharacter, quoteCharacter + quoteCharacter);

		// quoting the content if it contains delimiter
		if (content.contains(delimiter))
			content = quoteCharacter + content + quoteCharacter;

		// write delimiter if necessary
		if (!lastWasNewLine)
			out.write(delimiter);

		lastWasNewLine = false;
		out.write(content);
	}

	public void emitNewline() throws IOException
	{
		out.write(newline);
		lastWasNewLine = true;
	}

	public void flush() throws IOException
	{
		out.flush();
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

	public String getNewLine()
	{
		return this.newline;
	}

	public void setNewLine(String newline)
	{
		this.newline = newline;
	}
}
