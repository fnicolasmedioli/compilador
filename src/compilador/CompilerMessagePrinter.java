package compilador;

import java.util.List;

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
	
	static public void printTokenList(List<Integer> tokenList)
	{
		System.out.println("Lista de tokens leidos:");
		for (int token : tokenList)		
			System.out.println(Token.getTokenDescription(token));
	}
	
	static public void printSyntacticTree(SyntacticTreeNode root)
	{
		System.out.println("Estructuras sintácticas encontradas:");
		root.print();
	}
}
