package compilador;

public class Source
{
    public static void main(String[] args)
    {
    	AnalizadorLexico analizadorLexico;
        
        try {
        	analizadorLexico = new AnalizadorLexico("codigoFuente.fn");
        }
        catch (Exception e) {
        	System.out.println("Error leyendo archivo");
        	return;
        }
    }
}