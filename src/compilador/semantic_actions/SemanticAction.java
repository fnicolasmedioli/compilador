package compilador.semantic_actions;

import compilador.LexicalAnalyzerState;

public interface SemanticAction {
	
	void run(LexicalAnalyzerState lexicalAnalyzerState);
}