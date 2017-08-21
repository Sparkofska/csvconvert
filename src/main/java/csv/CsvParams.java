package main.java.csv;

public class CsvParams
{
	public String newline = "\r\n";
	public char splitChar = ';';
	public char escapeChar = '\"';
	public String encoding = "ISO8859_1";

	public static CsvParams getDefaultCsvParams()
	{
		return new CsvParams();
	}

	@Override
	public String toString()
	{
		return "CsvParams [newline=" + newline + ", splitChar=" + splitChar + ", escapeChar=" + escapeChar
				+ ", encoding=" + encoding + "]";
	}

}
