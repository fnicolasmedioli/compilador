package compiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import java.util.List;
import java.util.LinkedList;

import compiler.semantic_actions.*;

public class LexicalAnalyzer {
	
	private final TransitionMatrix transitionMatrix;
	private String sourceCode;
	private final LexicalAnalyzerState state;
	private final SymbolTable symbolTable;
	private final Compiler compiler;
	private final LinkedList<Integer> readedTokensList;
	
	private static final SemanticAction[] semanticActionArray = {
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
	
	public LexicalAnalyzer(String sourceFilePath, Compiler compiler) throws FileNotFoundException
	{
		this.transitionMatrix = new TransitionMatrix();
		this.state = new LexicalAnalyzerState();
		this.loadSourceFile(sourceFilePath);
		this.symbolTable = new SymbolTable(compiler);
		this.readedTokensList = new LinkedList<>();
		this.compiler = compiler;
	}
    
    public void loadSourceFile(String sourceFilePath) throws FileNotFoundException
    {
    	StringBuilder sb = new StringBuilder();
        Scanner scanner = new Scanner(new File(sourceFilePath));
        while (scanner.hasNextLine())
        {
        	String line = scanner.nextLine();
        	sb.append(line);
        	sb.append("\n");
        }
        scanner.close();
        this.sourceCode = sb.toString();
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
        		compiler.reportLexicalError(
        			"Caracter no reconocido: '" + sourceCode.charAt(this.state.getReadIndex()) + "'",
        			new TokenLocation(this.state.getCurrentLine())
        		);
        		
        		this.state.incrementReadIndex();

				this.state.setNewState(0);
				this.state.resetLexeme();

        		continue;
        	}
        	
        	this.state.setNewState(transition.getNewState());
        	
        	for (Integer semanticAction: transition.getSemanticActionList())
        		LexicalAnalyzer.semanticActionArray[semanticAction]
					.run(this.state, this.compiler);
        	
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
