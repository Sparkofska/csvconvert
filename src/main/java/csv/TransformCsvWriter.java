package main.java.csv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import main.java.Rule;

public class TransformCsvWriter
{
	private PureCsvWriter base;

	private boolean isHeaderWritten = false;
	private Writer writerout;
	private List<Rule> rules;

	public TransformCsvWriter(File file, List<Rule> rules) throws FileNotFoundException, UnsupportedEncodingException
	{
		this(file, rules, CsvParams.getDefaultCsvParams());
	}

	public TransformCsvWriter(File file, List<Rule> rules, CsvParams csvParams)
			throws UnsupportedEncodingException, FileNotFoundException
	{
		this(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), csvParams.encoding)), rules,
				csvParams);
	}

	public TransformCsvWriter(Writer out, List<Rule> rules)
	{
		this(out, rules, CsvParams.getDefaultCsvParams());
	}

	public TransformCsvWriter(Writer out, List<Rule> rules, CsvParams csvParams)
	{
		this.writerout = out; // save reference for closing later
		base = new PureCsvWriter(out, csvParams);

		this.rules = rules;
	}

	private void emitHeaderIfNotDone() throws IOException
	{
		if (isHeaderWritten)
			return;

		for (Rule rule : rules)
		{
			base.emitCell(rule.getDestinationColumn());
		}
		base.emitNewline();
		isHeaderWritten = true;
	}

	public void emit(Map<String, Object> map) throws IOException
	{
		emitHeaderIfNotDone();

		for (Rule rule : rules)
		{
			Object v = null;
			if (rule.getDefaultValue() == null)
			{
				String orig = rule.getOriginColumn();
				v = map.get(orig);
				if (v == null)
					v = "";
			}
			else
				v = rule.getDefaultValue();
			base.emitCell(v);
		}
		base.emitNewline();
	}

	public void emit(Collection<Map<String, Object>> maps) throws IOException
	{
		for (Map<String, Object> map : maps)
			emit(map);
	}

	public void flush() throws IOException
	{
		emitHeaderIfNotDone();
		base.flush();
	}

	public void close() throws IOException
	{
		flush();
		if (this.writerout != null)
			writerout.close();
	}

	public void setCsvParams(CsvParams csvparams)
	{
		if (isHeaderWritten)
			throw new IllegalStateException(
					"The CSV-Characters can not be changed after the header was already written.");

		base.setCsvParams(csvparams);
	}
}
