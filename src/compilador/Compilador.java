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

        
        //while (lexicalAnalyzer.doRemainTokens())
        //	lexicalAnalyzer.getToken();
	}
	
	public static int yylex()
	{
		return 4;
	}
}
