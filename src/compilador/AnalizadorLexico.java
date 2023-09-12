package compilador;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.HashMap;

public class AnalizadorLexico {
	
	private TransitionMatrix transitionMatrix;
	private String codigoFuente;
	
	private int readIndex;
	private int currentState;
	
	private ReservedWordTable reservedWordTable;
	private HashMap<String, Integer> symbolTable;
	
	public AnalizadorLexico(String rutaArchivoFuente) throws FileNotFoundException
	{
		this.reservedWordTable = new ReservedWordTable();
		this.transitionMatrix = new TransitionMatrix();
		this.currentState = 0;
		this.readIndex = 0;
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
    
    public void getToken()
    {
    	String lexeme = "";
    	boolean readComplete = false;
    	
    	while (!readComplete)
    	{
    		Character readChar = codigoFuente.charAt(readIndex);
        	Transition transition = transitionMatrix.getTransition(
        		currentState,
        		readChar
            );
        	this.currentState = transition.getNewState();
        	
        	for (Integer semanticAction : transition.getSemanticActionList())
        	{
        		if (semanticAction == 1)
        			if (this.readIndex != 0)
        				this.readIndex--;
        		if (semanticAction == 2)
        			lexeme = "";
        		if (semanticAction == 3)
        			lexeme += readChar;
        		if (semanticAction == 4)
        		{
        			
        		}
        		if (semanticAction == 5)
        		{
        			lexeme = "";
        			System.out.println("Error léxico");
        		}
        	}
        	readIndex++;
    	}
    }
    
}
