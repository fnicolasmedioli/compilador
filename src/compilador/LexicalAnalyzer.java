package compilador;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import compilador.semantic_actions.*;

public class LexicalAnalyzer {
	
	private TransitionMatrix transitionMatrix;
	private String sourceCode;
	
	private LexicalAnalyzerState state;
	public SymbolTable symbolTable;
	
	private static SemanticAction[] semanticActionArray = {
		null,
		new SemanticAction1(),
		new SemanticAction2(),
		new SemanticAction3(),
		new SemanticAction4(),
		new SemanticAction5(),
		new SemanticAction6(),
		new SemanticAction7(),
		new SemanticAction8(),
		new SemanticAction9()
	};
	
	public LexicalAnalyzer(String sourceFilePath) throws FileNotFoundException
	{
		this.transitionMatrix = new TransitionMatrix();
		this.state = new LexicalAnalyzerState();
		this.loadSourceFile(sourceFilePath);
		this.symbolTable = new SymbolTable();
	}
    
    public void loadSourceFile(String sourceFilePath) throws FileNotFoundException
    {
    	this.sourceCode = "";
        Scanner scanner = new Scanner(new File(sourceFilePath));
        while (scanner.hasNextLine())
        {
        	String line = scanner.nextLine();
        	this.sourceCode += line + "\n";
        }
        scanner.close();
    }
    
    public boolean doRemainTokens()
    {
    	return this.state.getReadIndex() < this.sourceCode.length();
    }
    
    public int getToken()
    {
    	this.state.startTokenReading();
    	
    	while (this.state.tokenReading())
    	{
    		
    		if (this.state.getReadIndex() == this.sourceCode.length())
    		{
    			if ( this.state.getCurrentState() == TransitionMatrix.INITIAL_STATE
	    			&& this.state.getCurrentState() == TransitionMatrix.FINAL_STATE )
    				return 0;
    			else
    			{
        			CompilerMessagePrinter.error("final de archivo inesperado");        			
        			return 0;
    			}
    		}
    		
    		this.state.setLastReadChar(
    			this.sourceCode.charAt(
    				this.state.getReadIndex()
    			)
    		);
    		
        	Transition transition = transitionMatrix.getTransition(
        		this.state.getCurrentState(),
        		this.state.getLastReadChar()
            );
        	
        	if (transition == null)
        	{
        		CompilerMessagePrinter.error("lexico: caracter inesperado");
        		CompilerMessagePrinter.error("estado: " + this.state.getCurrentState());
        		CompilerMessagePrinter.error("leyendo: '" + this.state.getLastReadChar() + "'");
        		this.state.incrementReadIndex();
        		return 0;
        	}
        	
        	this.state.setNewState(transition.getNewState());
        	
        	for (Integer semanticAction: transition.getSemanticActionList())
        		LexicalAnalyzer.semanticActionArray[semanticAction]
            			.run(this.state, this.symbolTable);
        	
        	this.state.incrementReadIndex();
    	}
    	
    	return this.state.getTokenToReturn();
    }
    
}
