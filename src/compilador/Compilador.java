package compilador;

import syntacticTree.*;

public class Compilador {
	
	private static LexicalAnalyzer lexicalAnalyzer;
	private static Parser parser;
	private static PrintableSyntacticTree syntacticTree;

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
        
        boolean parseSuccess = true;
        try {
        	parseSuccess = (parser.yyparse() == 0) ? true : false;
        } catch (Exception e) {
        	parseSuccess = false;
        }
        
        CompilerMessagePrinter.printTokenList(lexicalAnalyzer.getReadedTokensList());
        
        if (parseSuccess)
        {
        	System.out.println();
            CompilerMessagePrinter.printSyntacticTree(syntacticTree);
        }
	}
	
	public static int yylex()
	{
		int token = lexicalAnalyzer.getToken();
		return token;
	}
	
	public static void setyylval(String v)
	{
		parser.setyylval(v);
	}
	
	public static String getyylval()
	{
		return parser.getyylval();
	}
	
	public static void setSyntacticTree(PrintableSyntacticTree r)
	{
		Compilador.syntacticTree = r;
	}
	
	public static void reportLexicalError(String msg)
	{
		CompilerMessagePrinter.error(msg);
	}
	
	public static void reportSyntaxError(String msg)
	{
		CompilerMessagePrinter.error(msg);
	}
}
