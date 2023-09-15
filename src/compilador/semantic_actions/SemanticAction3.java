package compilador.semantic_actions;

import compilador.LexicalAnalyzerState;
import compilador.SymbolTable;

public class SemanticAction3 implements SemanticAction {
	
	@Override
	public void run(
		LexicalAnalyzerState lexicalAnalyzerState,
		SymbolTable symbolTable )
	{
		lexicalAnalyzerState.addSymbolToLexeme(
			lexicalAnalyzerState.getLastReadChar()
		);
	}
	
}
