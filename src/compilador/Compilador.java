package compilador;

public class Compilador {
	
	private static LexicalAnalyzer lexicalAnalyzer;
	private static Parser parser;

	public static void main(String[] args)
	{
        try
        {
        	Compilador.lexicalAnalyzer = new LexicalAnalyzer("source.fn");        	
        }
        catch (Exception e)
        {
        	System.out.println("Error leyendo archivo");
        	return;
        }

        Compilador.parser = new Parser();
        
        while (lexicalAnalyzer.doRemainTokens())
        {
        	System.out.println(lexicalAnalyzer.getToken());
        }
	}
	
	public static int yylex()
	{
		if (lexicalAnalyzer.doRemainTokens() == false)
			return 0;
		
		return 0;
	}
	
	public static void setyylval(String v)
	{
		parser.setyylval(v);
	}
}
