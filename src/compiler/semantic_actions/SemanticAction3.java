package compiler.semantic_actions;

import compiler.LexicalAnalyzerState;
import compiler.Compiler;

public class SemanticAction3 implements SemanticAction {
	
	@Override
	public void run(
		LexicalAnalyzerState lexicalAnalyzerState,
		Compiler compiler
	)
	{
		lexicalAnalyzerState.addSymbolToLexeme(
			lexicalAnalyzerState.getLastReadChar()
		);
	}
	
}
