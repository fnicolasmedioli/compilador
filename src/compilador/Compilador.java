package compilador;

import java.util.PriorityQueue;

public class Compilador {
	
	private static LexicalAnalyzer lexicalAnalyzer;
	private static Parser parser;
	private static PriorityQueue<SyntacticStructureResult> syntacticStructuresFound;

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

        parser = new Parser();
        syntacticStructuresFound = new PriorityQueue<>(
        	new SyntacticStructureResultComparator()
        );
        
        boolean parseSuccess = true;
        try {
        	parseSuccess = (parser.yyparse() == 0) ? true : false;
        } catch (Exception e) {
        	System.out.println(e.getMessage());
        	parseSuccess = false;
        }
        
        if (parseSuccess)
        	CompilerMessagePrinter.printGreen("Parsing correcto");
        else
        	CompilerMessagePrinter.error("Error en el parsing");
        
        System.out.println();
    	CompilerMessagePrinter.printTokenList(lexicalAnalyzer.getReadedTokensList());
    	CompilerMessagePrinter.printFoundSyntacticalStrucutres(syntacticStructuresFound);
	}
	
	public static int yylex()
	{
		int token = lexicalAnalyzer.getToken();
		return token;
	}
	
	public static void setyylval(TokenInfo v)
	{
		parser.setyylval(v);
	}
	
	public static void reportLexicalError(String msg)
	{
		CompilerMessagePrinter.error(msg);
	}
	
	public static void reportSyntaxError(String msg)
	{
		CompilerMessagePrinter.error(msg);
	}
	
	public static void addFoundSyntacticStructure(SyntacticStructureResult s)
	{
		syntacticStructuresFound.add(s);
	}
}
