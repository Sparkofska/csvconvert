package main.java.csv;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;

public class CsvParams
{
	public String newline = "\r\n";
	public char splitChar = ';';
	public char escapeChar = '\"';
	public String encoding = "UTF8";

	public static CsvParams getDefaultCsvParams()
	{
		CsvParams p = new CsvParams();
		p.encoding = getDefaultCharSet();
		return p;
	}

	public static void main(String[] args)
	{
		System.out.println(getDefaultCharSet());
	}

	private static String getDefaultCharSet()
	{
		OutputStreamWriter writer = new OutputStreamWriter(new ByteArrayOutputStream());
		String enc = writer.getEncoding();
		return enc;
	}

	@Override
	public String toString()
	{
		return "CsvParams [newline=" + newline + ", splitChar=" + splitChar + ", escapeChar=" + escapeChar
				+ ", encoding=" + encoding + "]";
	}

}
