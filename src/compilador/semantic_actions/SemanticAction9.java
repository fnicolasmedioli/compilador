package compilador.semantic_actions;

import compilador.*;

public class SemanticAction9 implements SemanticAction {
	
	@Override
	public void run(
		LexicalAnalyzerState lexicalAnalyzerState,
		SymbolTable symbolTable )
	{
		lexicalAnalyzerState.incrementCurrentLine();
	}
	
}
