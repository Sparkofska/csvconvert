package main.java.csv;

/**
 * Thrown, when a Format-Error occurs in the processed csv-File.
 * 
 * @author jonas
 *
 */
public class IllformedCsvException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public IllformedCsvException()
	{
		super("CSV-Format-Error");
	}

	public IllformedCsvException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super("CSV-Format-Error: " + message, cause, enableSuppression, writableStackTrace);
	}

	public IllformedCsvException(String message, Throwable cause)
	{
		super("CSV-Format-Error: " + message, cause);
	}

	public IllformedCsvException(String message)
	{
		super("CSV-Format-Error: " + message);
	}

	public IllformedCsvException(Throwable cause)
	{
		super(cause);
	}

}
