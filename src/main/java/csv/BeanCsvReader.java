package main.java.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author jonas
 *
 * @param <T>
 */
public abstract class BeanCsvReader<T>
{
	protected PureCsvReader base;

	private ArrayList<T> beans;

	private boolean stateAlreadyRead = false;

	public BeanCsvReader(File csvFile) throws IOException
	{
		this(csvFile, CsvParams.getDefaultCsvParams());
	}

	public BeanCsvReader(File csvFile, CsvParams csvParams) throws IOException
	{
		this.base = getPureCsvReader(
				new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), csvParams.encoding)), csvParams);
	}

	public BeanCsvReader(Reader reader)
	{
		this.base = getPureCsvReader(reader, CsvParams.getDefaultCsvParams());
	}

	public List<T> getBeans() throws IOException
	{
		if (this.stateAlreadyRead)
			throw new IllegalStateException("File " + base.getFileName() + " was already read.");

		base.read();

		this.stateAlreadyRead = true;

		return getBeanList();
	}

	protected PureCsvReader getPureCsvReader(Reader reader, CsvParams csvParams)
	{
		return new PureCsvReader(reader, csvParams)
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

	protected ArrayList<T> getBeanList()
	{
		if (beans == null)
			beans = new ArrayList<>();
		return beans;
	}

	protected abstract T toBean(int index, List<String> list);

	public void setCsvParams(CsvParams p)
	{
		base.setCsvParams(p);
	}

	public CsvParams getCsvParams()
	{
		return base.getCsvParams();
	}
}
