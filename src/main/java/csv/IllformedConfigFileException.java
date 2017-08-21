package main.java.csv;

/**
 * Thrown, when a Format-Error occurs in the processed config-File.
 * 
 * @author jonas
 *
 */
public class IllformedConfigFileException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public IllformedConfigFileException()
	{
		super("Config-File-Error");
	}

	public IllformedConfigFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super("Config-File-Error: " + message, cause, enableSuppression, writableStackTrace);
	}

	public IllformedConfigFileException(String message, Throwable cause)
	{
		super("Config-File-Error: " + message, cause);
	}

	public IllformedConfigFileException(String message)
	{
		super("Config-File-Error: " + message);
	}

	public IllformedConfigFileException(Throwable cause)
	{
		super(cause);
	}

}
