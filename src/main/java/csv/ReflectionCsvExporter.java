package main.java.csv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * 
 * @author Jonas Münch
 * @since 08.06.2017
 */
public class ReflectionCsvExporter<BEAN>
{
	private PureCsvWriter base;
	private Set<AccessibleObject> cols;

	private boolean isHeaderWritten = false;
	private Writer writerout;

	public ReflectionCsvExporter(Class<BEAN> cls, File file) throws IOException
	{
		this(cls, file, "ISO8859_1", new CsvParams(), true);
	}

	public ReflectionCsvExporter(Class<BEAN> cls, File file, String charSet, CsvParams csvParams,
			boolean onlyLookForMembersWithCsvExportValueAnnotation) throws IOException
	{
		// append on File if exists
		if (file.exists())
		{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			if ((line = reader.readLine()) != null)
			{
				reader.close();
				String[] headers = line.split(csvParams.delimiter);
				Set<AccessibleObject> set = buildColsFromHeader(cls, headers,
						onlyLookForMembersWithCsvExportValueAnnotation);

				BufferedWriter appender = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(file, true), charSet));

				init(set, appender, csvParams);
				isHeaderWritten = true;
				return;
			}
			reader.close();
			// else: File is empty, can be treated like a new file
		}
		// generate new file
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), charSet));
		init(cls, writer, csvParams);
	}

	public ReflectionCsvExporter(Class<BEAN> cls, Writer out)
	{
		init(cls, out);
	}

	private void init(Class<BEAN> cls, Writer out)
	{
		init(cls, out, new CsvParams());
	}

	private void init(Class<BEAN> cls, Writer out, CsvParams csvParams)
	{

		TreeSet<AccessibleObject> set = new TreeSet<AccessibleObject>(new CsvExportValueComparator());

		for (Field field : cls.getDeclaredFields())
		{
			if (field.getAnnotation(CsvExportValue.class) != null)
				set.add(field);
		}

		for (Method method : cls.getMethods())
		{
			if (method.getAnnotation(CsvExportValue.class) != null)
			{
				if (method.getParameterCount() > 0)
					throw new IllegalArgumentException(
							"Only \"Getter\"-Methods without parameters can be used as export values. The Method "
									+ method.getName() + " takes " + method.getParameterCount() + " parameters.");
				set.add(method);
			}
		}

		if (set.size() == 0)
			throw new RuntimeException("The given Class " + cls.getName() + " has no " + CsvExportValue.class.getName()
					+ " annotated members. For exporting, you need to annotate the desired Fields or Methods!");

		init(set, out, csvParams);
	}

	private void init(Set<AccessibleObject> set, Writer out, CsvParams csvParams)
	{
		this.writerout = out; // save reference for closing later
		cols = set;
		base = new PureCsvWriter(out);
		setCsvParams(csvParams);
	}

	private Set<AccessibleObject> buildColsFromHeader(Class<BEAN> cls, String[] headers,
			boolean onlyLookForMembersWithCsvExportValueAnnotation)
	{
		LinkedHashSet<AccessibleObject> set = new LinkedHashSet<>(headers.length);

		ArrayList<AccessibleObject> members = new ArrayList<>();
		members.addAll(Arrays.asList(cls.getDeclaredFields()));
		members.addAll(Arrays.asList(cls.getMethods()));

		for (String header : headers)
		{
			// linear search for a fitting member
			ArrayList<AccessibleObject> candidates = new ArrayList<>();
			for (AccessibleObject member : members)
			{
				if (member instanceof Field)
				{
					// ascending similarity
					// always put the most similar candidate in front of list
					if (((Field) member).getName().toLowerCase().contains(header.toLowerCase()))
						candidates.add(0, member);
					if (((Field) member).getName().contains(header))
						candidates.add(0, member);
					if (((Field) member).getName().toLowerCase().equals(header.toLowerCase()))
						candidates.add(0, member);
					if (((Field) member).getName().equals(header))
						candidates.add(0, member);
				}
				// prefer Methods over Fields - e.g. Getters (put them in front
				// of lists)
				else if (member instanceof Method)
				{
					if (((Method) member).getName().toLowerCase().contains(header.toLowerCase()))
						if (!((Method) member).getName().startsWith("set")
								&& ((Method) member).getParameterCount() == 0)
							candidates.add(0, member);
					if (((Method) member).getName().contains(header))
						if (!((Method) member).getName().startsWith("set")
								&& ((Method) member).getParameterCount() == 0)
							candidates.add(0, member);
					if (((Method) member).getName().toLowerCase().equals(header.toLowerCase())
							&& ((Method) member).getParameterCount() == 0)
						candidates.add(0, member);
					if (((Method) member).getName().equals(header) && ((Method) member).getParameterCount() == 0)
						candidates.add(0, member);
				}
			}

			if (candidates.isEmpty())
				throw new IllegalArgumentException(
						"No member with name '" + header + "' was found in class " + cls.getName() + ".");

			AccessibleObject chosen = candidates.get(0);
			if (onlyLookForMembersWithCsvExportValueAnnotation)
				if (chosen.getAnnotation(CsvExportValue.class) == null)
					throw new IllegalArgumentException("No member with name '" + header + "' and "
							+ CsvExportValue.class.getName() + "-Annotation was found in class " + cls.getName() + ".");

			set.add(chosen);
		}

		return set;
	}

	public void emit(BEAN bean) throws IOException
	{
		emitHeaderIfNotDone();

		for (AccessibleObject col : cols)
		{
			try
			{
				Object v;
				v = getValue(col, bean);
				if (v == null)
					v = "";
				base.emitCell(v);
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
			{
				throw new RuntimeException("An error occured while reflecting the bean", e);
			}
		}
		base.emitNewline();
	}

	public void emit(Collection<BEAN> beans) throws IOException
	{
		for (BEAN bean : beans)
			emit(bean);
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

		base.setDelimiter(csvparams.delimiter);
		base.setNewLine(csvparams.newline);
		base.setQuoteCharacter(csvparams.quoteCharacter);
	}

	private void emitHeaderIfNotDone() throws IOException
	{
		if (isHeaderWritten)
			return;

		for (AccessibleObject col : cols)
		{
			base.emitCell(getColNameOf(col));
		}
		base.emitNewline();
		isHeaderWritten = true;
	}

	private Object getValue(AccessibleObject col, BEAN bean)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		if (col instanceof Method)
		{
			Method method = ((Method) col);
			method.setAccessible(true);
			return method.invoke(bean);
		}
		else if (col instanceof Field)
		{
			Field field = ((Field) col);
			field.setAccessible(true);
			return field.get(bean);
		}
		else
			throw new IllegalStateException(
					"Parameter 'col' is niether instanceof Method nor instanceof Field. There must be some inconsistence...");
	}

	private String getColNameOf(AccessibleObject col)
	{
		if (col instanceof Method)
		{
			String name = ((Method) col).getName();
			if (name.startsWith("get"))
				name = name.substring(3, name.length());
			return name;
		}
		else if (col instanceof Field)
		{
			return ((Field) col).getName();
		}
		else
			throw new IllegalStateException(
					"Parameter 'col' is niether instanceof Method nor instanceof Field. There must be some inconsistence...");
	}

	public static CsvParams getDefaultCsvParams()
	{
		return new CsvParams();
	}

	public static class CsvParams
	{
		public String newline = "\r\n";
		public String delimiter = ";";
		public String quoteCharacter = "\"";
	}
}
