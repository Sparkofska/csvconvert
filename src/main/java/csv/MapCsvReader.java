package main.java.csv;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapCsvReader extends BeanCsvReader<Map<String, Object>>
{
	private List<String> headers;

	public MapCsvReader(File csvFile, String charset) throws IOException
	{
		super(csvFile, charset);
	}

	public MapCsvReader(File csvFile) throws IOException
	{
		super(csvFile);
	}

	@Override
	protected Map<String, Object> toBean(int index, List<String> list)
	{
		if (list.size() != headers.size())
		{
			throw new IllformedCsvException("Line " + index + " has " + list.size() + " columns, but header has "
					+ headers.size() + " columns.");
			// TODO would be interesting here to know which file is illformed.
		}

		Map<String, Object> map = new HashMap<String, Object>(list.size());

		for (int i = 0; i < list.size(); i++)
		{
			String key = headers.get(i);
			String value = list.get(i);

			map.put(key, value);
		}

		return map;
	}

	@Override
	protected PureCsvReader getPureCsvReader(Reader reader)
	{
		return new PureCsvReader(reader)
		{
			@Override
			protected void onHeaders(List<String> list)
			{
				headers = list;
			}

			@Override
			protected void onLine(int index, List<String> list)
			{
				Map<String, Object> bean = toBean(index, list);
				if (bean != null)
					getBeanList().add(bean);
			}
		};
	}
}
