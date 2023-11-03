package compiler.semantic_actions;

import compiler.LexicalAnalyzerState;
import compiler.Compiler;

public class SemanticAction1 implements SemanticAction {
	
	@Override
	public void run(
		LexicalAnalyzerState lexicalAnalyzerState,
		Compiler compiler
	)
	{
		lexicalAnalyzerState.decrementReadIndex();
	}
}