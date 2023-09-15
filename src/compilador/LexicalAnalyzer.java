package compilador;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import compilador.semantic_actions.*;

public class LexicalAnalyzer {
	
	private TransitionMatrix transitionMatrix;
	private String sourceCode;
	
	private LexicalAnalyzerState state;
	private SymbolTable symbolTable;
	
	private static SemanticAction[] semanticActionArray = {
		null,
		new SemanticAction1()
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
    
    public void getToken()
    {
    	this.state.startTokenReading();
    	
    	while (this.state.tokenReading())
    	{
    		this.state.setLastReadChar(
    			this.sourceCode.charAt(
    				this.state.getReadIndex()
    			)
    		);
    		
        	Transition transition = transitionMatrix.getTransition(
        		this.state.getCurrentstate(),
        		this.state.getLastReadChar()
            );
        	this.state.setNewState(transition.getNewState());
        	
        	for (Integer semanticAction: transition.getSemanticActionList())
        		LexicalAnalyzer.semanticActionArray[semanticAction]
        			.run(this.state, this.symbolTable);
        	
        	this.state.incrementReadIndex();
    	}
    }
    
}
