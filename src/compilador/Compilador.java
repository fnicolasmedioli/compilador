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
        
        while (true)
        {
        	int tokenID = lexicalAnalyzer.getToken();
        	if (tokenID == 0)
        		break;
        	
        	String lexeme = parser.getyylval();
        	System.out.println("TOKEN: " + tokenID + "\tLexema: '" + lexeme + "'");
        }
	}
	
	public static int yylex()
	{
		if (lexicalAnalyzer.doRemainTokens() == false)
			return 0;
		
		return lexicalAnalyzer.getToken();
	}
	
	public static void setyylval(String v)
	{
		parser.setyylval(v);
	}
	
	public static String getyylval()
	{
		return parser.getyylval();
	}
}
