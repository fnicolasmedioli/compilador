package compilador;

public class CompilerMessagePrinter {
	
	private static final String ANSI_YELLOW = "\u001B[33m";
	private static final String ANSI_RED = "\u001B[31m";
	private static final String ANSI_RESET = "\u001B[0m";
	
	static private void printWithColor(String msg, String color)
	{
		System.out.println(color + msg + ANSI_RESET);
	}
	
	static public void warning(String msg)
	{
		printWithColor(msg, ANSI_YELLOW);
	}
	
	static public void error(String msg)
	{
		printWithColor(msg, ANSI_RED);
	}
}
