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
        
        boolean yaccSuccess = true;
        try {
        	yaccSuccess = (parser.yyparse() == 0) ? true : false;
        } catch (Exception e) {
        	System.out.println(e.getMessage());
        	yaccSuccess = false;
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
		reportLexicalError(msg);
	}
	
	public static void reportLexicalError(String msg, TokenLocation loc)
	{
		if (loc != null)
			CompilerMessagePrinter.error("[Léxico: " + loc + "] " + msg);
		else
			CompilerMessagePrinter.error("[Léxico] " + msg);

		errorCount++;
	}
	
	public static void reportSyntaxError(String msg)
	{
		reportSyntaxError(msg);
	}
	
	public static void reportSyntaxError(String msg, TokenLocation loc)
	{
		if (loc != null)
			CompilerMessagePrinter.error("[Sintáctico: " + loc + "] " + msg);
		else
			CompilerMessagePrinter.error("[Sintáctico] " + msg);
	
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
