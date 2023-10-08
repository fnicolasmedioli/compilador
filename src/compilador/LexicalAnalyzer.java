package compilador;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import java.util.List;
import java.util.LinkedList;

import compilador.semantic_actions.*;

public class LexicalAnalyzer {
	
	private TransitionMatrix transitionMatrix;
	private String sourceCode;
	
	private LexicalAnalyzerState state;
	public SymbolTable symbolTable;
	
	private LinkedList<Integer> readedTokensList;
	
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
		this.readedTokensList = new LinkedList<>();
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
    
    public List<Integer> getReadedTokensList()
    {
    	return this.readedTokensList;
    }
    
    public int getToken()
    {
    	this.state.startTokenReading();
    	
    	while (this.state.tokenReading())
    	{
    		
    		if (this.state.getReadIndex() == this.sourceCode.length())
	    		return 0;
    		
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
        		this.state.incrementReadIndex();
        		return 0;
        	}
        	
        	this.state.setNewState(transition.getNewState());
        	
        	for (Integer semanticAction: transition.getSemanticActionList())
        		LexicalAnalyzer.semanticActionArray[semanticAction]
            			.run(this.state, this.symbolTable);
        	
        	this.state.incrementReadIndex();
    	}
    	
    	int token = this.state.getTokenToReturn();
    	
    	readedTokensList.add(token);
    	
    	return token;
    }
    
    public SymbolTable getSymbolTable()
    {
    	return symbolTable;
    }
    
}
