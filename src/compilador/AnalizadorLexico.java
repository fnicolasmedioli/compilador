package compilador;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import compilador.semantic_actions.*;

public class AnalizadorLexico {
	
	private TransitionMatrix transitionMatrix;
	private String codigoFuente;
	
	private LexicalAnalyzerState state;
	
	private static SemanticAction[] semanticActionArray = {
		null,
		new SemanticAction1()
	};
	
	public AnalizadorLexico(String rutaArchivoFuente) throws FileNotFoundException
	{
		this.transitionMatrix = new TransitionMatrix();
		this.state = new LexicalAnalyzerState();
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
    
    public LexicalAnalyzerState getState()
    {
    	return this.state;
    }
    
    public void getToken()
    {
    	this.state.startTokenReading();
    	
    	while (this.state.tokenReading())
    	{
    		Character readChar = codigoFuente.charAt(
    			this.state.getReadIndex()
    		);
        	Transition transition = transitionMatrix.getTransition(
        		this.state.getCurrentstate(),
        		readChar
            );
        	this.state.setNewState(transition.getNewState());
        	
        	for (Integer semanticAction: transition.getSemanticActionList())
        		AnalizadorLexico.semanticActionArray[semanticAction]
        			.run(this.state);
        	
        	this.state.incrementReadIndex();
    	}
    }
    
}
