package compilador;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class AnalizadorLexico {
	
	private String codigoFuente;
	
	public AnalizadorLexico(String rutaArchivoFuente) throws FileNotFoundException
	{
		this.cargarArchivo(rutaArchivoFuente);
	}
    
    public void cargarArchivo(String rutaArchivo) throws FileNotFoundException
    {
    	codigoFuente = "";
        Scanner lector = new Scanner(new File(rutaArchivo));
        while (lector.hasNextLine())
        {
        	String linea = lector.nextLine();
        	codigoFuente += linea + "\n";
        }
        lector.close();
    }
}
