package main.java.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public abstract class BeanCsvReader<T>
{
	private PureCsvReader	base;

	private ArrayList<T>	beans;

	private boolean			stateAlreadyRead	= false;

	public BeanCsvReader(File csvFile) throws IOException
	{
		this(csvFile, "ISO8859_1");
	}

	public BeanCsvReader(File csvFile, String charset) throws IOException
	{
		this.base = getPureCsvReader(new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), charset)));
	}

	public BeanCsvReader(Reader reader)
	{
		this.base = getPureCsvReader(reader);
	}

	public List<T> getBeans() throws IOException
	{
		if (this.stateAlreadyRead)
			throw new IllegalStateException("Die Datei wurde bereits gelesen.");

		base.read();

		this.stateAlreadyRead = true;

		return getBeanList();
	}

	protected PureCsvReader getPureCsvReader(Reader reader)
	{
		return new PureCsvReader(reader)
		{
			@Override
			protected void onLine(int index, List<String> list)
			{
				T bean = toBean(index, list);
				if (bean != null)
					getBeanList().add(bean);
			}

			@Override
			protected void onHeaders(List<String> list)
			{
				// ignore
			}
		};
	}

	private ArrayList<T> getBeanList()
	{
		if (beans == null)
			beans = new ArrayList<>();
		return beans;
	}

	protected abstract T toBean(int index, List<String> list);

	/**
	 * changes the delimiter, which is used for separation of two values.<br>
	 * default value is <code>";"</code>
	 */
	public void setDelimiter(String delimiter)
	{
		base.setDelimiter(delimiter);
	}

	public void setQuoteCharacter(String quoteCharacter)
	{
		base.setQuoteCharacter(quoteCharacter);
	}
}
