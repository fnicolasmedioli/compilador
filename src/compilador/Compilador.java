package compilador;

import java.util.List;
import java.util.LinkedList;

public class Compilador {
	
	private static LexicalAnalyzer lexicalAnalyzer;
	private static Parser parser;
	
	private static List<Integer> syntacticStructures;

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

        syntacticStructures = new LinkedList<>();
        parser = new Parser();
        boolean parseSuccess = (parser.yyparse() == 0) ? true : false;
        
        CompilerMessagePrinter.printTokenList(lexicalAnalyzer.getReadedTokensList());
        CompilerMessagePrinter.printSyntaticStructures(syntacticStructures);
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
	
	public void addSyntacticStructure(int structure)
	{
	    syntacticStructures.add(structure);
	}
}
