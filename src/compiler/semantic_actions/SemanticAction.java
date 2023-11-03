package compiler.semantic_actions;

import compiler.LexicalAnalyzerState;
import compiler.Compiler;

public interface SemanticAction {
	
	void run(
		LexicalAnalyzerState lexicalAnalyzerState,
		Compiler compiler
	);
}