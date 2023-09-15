package compilador;

public class Source
{
    public static void main(String[] args)
    {
    	LexicalAnalyzer lexicalAnalyzer;
        
        try
        {
        	lexicalAnalyzer = new LexicalAnalyzer("source.fn");        	
        }
        catch (Exception e)
        {
        	System.out.println("Error leyendo archivo");
        	return;
        }
        
        while (lexicalAnalyzer.doRemainTokens())
        	lexicalAnalyzer.getToken();
        
    }
}