package compilador.semantic_actions;

import compilador.LexicalAnalyzerState;
import compilador.SymbolTable;

public interface SemanticAction {
	
	void run(
		LexicalAnalyzerState lexicalAnalyzerState,
		SymbolTable symbolTable
	);
}