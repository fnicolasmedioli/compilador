package compilador;

import java.util.List;
import java.util.PriorityQueue;

import compilador.syntacticTree.*;

public class CompilerMessagePrinter {
	
	private static final String ANSI_YELLOW = "\u001B[33m";
	private static final String ANSI_RED = "\u001B[31m";
	private static final String ANSI_GREEN = "\u001B[32m";
	private static final String ANSI_RESET = "\u001B[0m";
	
	static private void printWithColor(String msg, String color)
	{
		System.out.println(color + msg + ANSI_RESET);
	}
	
	static public void printGreen(String msg)
	{
		printWithColor(msg, ANSI_GREEN);
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
		if (tokenList.size() != 0)
			System.out.println("Lista de tokens leidos:");
		else
			System.out.println("No se leyeron tokens");

		for (int token : tokenList)		
			System.out.print(SymbolTableEntry.getTokenDescription(token) + " ");
		System.out.println("\n");
	}
	
	static public void printSyntacticTree(PrintableSyntacticTree root)
	{
		System.out.println("Estructuras sintácticas encontradas:");
		root.print(0);
	}
	
	static public void printFoundSyntacticalStrucutres(PriorityQueue<SyntacticStructureResult> syntacticStructuresFound)
	{
		if (syntacticStructuresFound.isEmpty() == true)
			System.out.println("No se encontraron estructuras sintácticas");
		else
			System.out.println("Estructuras sintácticas encontradas:");
		
		while (syntacticStructuresFound.isEmpty() == false)
		{
			SyntacticStructureResult h = syntacticStructuresFound.poll();
			System.out.println(h);
		}
	}
}
