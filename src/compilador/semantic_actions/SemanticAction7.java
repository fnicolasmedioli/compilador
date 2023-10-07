package compilador.semantic_actions;

import compilador.LexicalAnalyzerState;
import compilador.SymbolTable;

public class SemanticAction7 implements SemanticAction {
	
	@Override
	public void run(
		LexicalAnalyzerState lexicalAnalyzerState,
		SymbolTable symbolTable )
	{
		lexicalAnalyzerState.resetLexeme();		
	}
	
}
