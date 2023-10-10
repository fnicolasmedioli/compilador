package compilador;

import java.util.PriorityQueue;

public class Compilador {

	private static LexicalAnalyzer lexicalAnalyzer;
	private static Parser parser;
	private static PriorityQueue<SyntacticStructureResult> syntacticStructuresFound;
	private static int errorCount;

	public static void main(String[] args)
	{
		if (args.length == 0)
		{
			CompilerMessagePrinter.error("Falta al parametro indicando el nombre de archivo");
			return;
		}
		
		String fileName = args[0];		
		
        try
        {
        	Compilador.lexicalAnalyzer = new LexicalAnalyzer(fileName);
        }
        catch (Exception e)
        {
        	CompilerMessagePrinter.error("Error leyendo archivo fuente");
        	return;
        }
        
        errorCount = 0;
        parser = new Parser();
        syntacticStructuresFound = new PriorityQueue<>(
        	new SyntacticStructureResultComparator()
        );
        
        boolean yaccSuccess = false;

        try {
        	yaccSuccess = (parser.yyparse() == 0) ? true : false;
        } catch (Exception e) {
        	System.out.println(e.getMessage());
        }

        System.out.println();
        if (yaccSuccess && errorCount == 0)
        	CompilerMessagePrinter.printGreen("Parsing correcto");
        else
        	CompilerMessagePrinter.error("Hubo errores en el parsing");
        
        System.out.println();
    	CompilerMessagePrinter.printTokenList(lexicalAnalyzer.getReadedTokensList());
    	CompilerMessagePrinter.printFoundSyntacticalStrucutres(syntacticStructuresFound);
    	CompilerMessagePrinter.printSymbolTable(getSymbolTable());
	}
	
	public static int yylex()
	{
		int token = lexicalAnalyzer.getToken();
		return token;
	}
	
	public static void setyylval(String symbolTableEntryKey)
	{
		parser.setyylval(symbolTableEntryKey);
	}
	
	public static void reportLexicalError(String msg)
	{
		reportLexicalError(msg, null);
	}
	
	public static void reportLexicalError(String msg, TokenLocation loc)
	{
		if (loc != null)
			CompilerMessagePrinter.error("[Lexico: " + loc + "] " + msg);
		else
			CompilerMessagePrinter.error("[Lexico] " + msg);

		errorCount++;
	}
	
	public static void reportSyntaxError(String msg)
	{
		reportSyntaxError(msg, null);
	}
	
	public static void reportSyntaxError(String msg, TokenLocation loc)
	{
		if (loc != null)
			CompilerMessagePrinter.error("[Sintactico: " + loc + "] " + msg);
		else
			CompilerMessagePrinter.error("[Sintactico] " + msg);
	
		errorCount++;
	}
	
	public static void addFoundSyntacticStructure(SyntacticStructureResult s)
	{
		syntacticStructuresFound.add(s);
	}
	
	public static SymbolTable getSymbolTable()
	{
		return lexicalAnalyzer.getSymbolTable();
	}
}
