package compiler;

import java.util.List;
import java.util.PriorityQueue;

import compiler.syntacticTree.*;

public class CompilerMessagePrinter {
	
	private static final String ANSI_YELLOW = "\u001B[33m";
	private static final String ANSI_RED = "\u001B[31m";
	private static final String ANSI_GREEN = "\u001B[32m";
	private static final String ANSI_RESET = "\u001B[0m";

	private Compiler compiler;

	public CompilerMessagePrinter(Compiler compiler)
	{
		this.compiler = compiler;
	}
	
	private void printWithColor(String msg, String color)
	{
		System.out.println(color + msg + ANSI_RESET);
	}
	
	public void printGreen(String msg)
	{
		printWithColor(msg, ANSI_GREEN);
	}
	
	public void warning(String msg)
	{
		printWithColor(msg, ANSI_YELLOW);
	}
	
	public void error(String msg)
	{
		printWithColor(msg, ANSI_RED);
	}
	
	public void printTokenList()
	{
		List<Integer> tokenList = compiler.getLexicalAnalyzer().getReadedTokensList();

		if (!tokenList.isEmpty())
			System.out.println("Lista de tokens leidos:");
		else
			System.out.println("No se leyeron tokens");

		for (int token : tokenList)		
			System.out.print(compiler.getSymbolTable().getTokenDescription((short)token) + " ");
		System.out.println("\n");
	}
	
	static public void printSyntacticTree(PrintableSyntacticTree root)
	{
		System.out.println("Estructuras sintacticas encontradas:");
		root.print(0);
	}
	
	static public void printFoundSyntacticalStrucutres(PriorityQueue<SyntacticStructureResult> syntacticStructuresFound)
	{
		if (syntacticStructuresFound.isEmpty())
			System.out.println("No se encontraron estructuras sintacticas");
		else
			System.out.println("Estructuras sintacticas encontradas:");
		
		while (syntacticStructuresFound.isEmpty() == false)
		{
			SyntacticStructureResult h = syntacticStructuresFound.poll();
			System.out.println(h);
		}
		System.out.println();
	}
	
	static public void printSymbolTable(SymbolTable symbolTable)
	{
		System.out.println("Tabla de simbolos:");
		System.out.print(symbolTable);
	}
}