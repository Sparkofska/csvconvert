package main.java;

public enum ErrorCode
{
	ERROR_WRONG_NUMBER_ARGUMENTS(1, "Wrong number of arguments passed.");

	private int n;
	private String msg;

	private ErrorCode(int n, String msg)
	{
		this.n = n;
		this.msg = msg;
	}

	public int getN()
	{
		return n;
	}

	public void setN(int n)
	{
		this.n = n;
	}

	public String getMsg()
	{
		return msg;
	}

	public void setMsg(String msg)
	{
		this.msg = msg;
	}

}
