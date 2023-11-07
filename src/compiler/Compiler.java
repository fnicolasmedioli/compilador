package compiler;

import java.io.FileNotFoundException;
import java.util.PriorityQueue;

public class Compiler {

	private final LexicalAnalyzer lexicalAnalyzer;
	private final Parser parser;
	private final PriorityQueue<SyntacticStructureResult> syntacticStructuresFound;
	private final CompilerMessagePrinter messagePrinter;
	private int errorCount;

	public Compiler(String sourceFileName) throws FileNotFoundException
	{
		this.lexicalAnalyzer = new LexicalAnalyzer(sourceFileName, this);
		this.parser = new Parser(this);
		this.syntacticStructuresFound = new PriorityQueue<>(
			new SyntacticStructureResultComparator()
		);
		this.messagePrinter = new CompilerMessagePrinter(this);
		this.errorCount = 0;
	}

	public void compile()
	{
		boolean yaccSuccess;

		try {
			yaccSuccess = parser.yyparse() == 0;
		} catch (Exception e) {
			yaccSuccess = false;
			messagePrinter.error("Error en yyparse()");
			System.out.println(e.getMessage());
		}

		System.out.println();

		if (yaccSuccess && errorCount == 0)
			messagePrinter.printGreen("Parsing correcto");
		else
			messagePrinter.error("Hubo errores en el parsing");

		System.out.println();
		messagePrinter.printTokenList();
		messagePrinter.printFoundSyntacticalStrucutres(syntacticStructuresFound);
		messagePrinter.printSymbolTable(getSymbolTable());
	}
	
	public int yylex()
	{
		return lexicalAnalyzer.getToken();
	}
	
	public void setyylval(LocatedSymbolTableEntry tokenData)
	{
		parser.setyylval(tokenData);
	}
	
	public void reportLexicalError(String msg)
	{
		reportLexicalError(msg, null);
	}
	
	public void reportLexicalError(String msg, TokenLocation loc)
	{
		if (loc != null)
			messagePrinter.error("[Lexico: " + loc + "] " + msg);
		else
			messagePrinter.error("[Lexico] " + msg);

		errorCount++;
	}
	
	public void reportSyntaxError(String msg)
	{
		reportSyntaxError(msg, null);
	}
	
	public void reportSemanticError(String msg)
	{
		reportSemanticError(msg, null);
	}
	
	public void reportSemanticError(String msg, TokenLocation loc)
	{
		if (loc != null)
			messagePrinter.error("[Semantico: " + loc + "] " + msg);
		else
			messagePrinter.error("[Semantico] " + msg);
		errorCount++;
	}
	
	public void reportSyntaxError(String msg, TokenLocation loc)
	{
		if (loc != null)
			messagePrinter.error("[Sintactico: " + loc + "] " + msg);
		else
			messagePrinter.error("[Sintactico] " + msg);
	
		errorCount++;
	}
	
	public void addFoundSyntacticStructure(SyntacticStructureResult s)
	{
		syntacticStructuresFound.add(s);
	}
	
	public SymbolTable getSymbolTable()
	{
		return lexicalAnalyzer.getSymbolTable();
	}

	public LexicalAnalyzer getLexicalAnalyzer()
	{
		return this.lexicalAnalyzer;
	}

	public CompilerMessagePrinter getMessagePrinter()
	{
		return this.messagePrinter;
	}
}