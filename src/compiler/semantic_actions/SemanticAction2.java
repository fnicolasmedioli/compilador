package compiler.semantic_actions;

import compiler.LexicalAnalyzerState;
import compiler.Compiler;

public class SemanticAction2 implements SemanticAction {
	
	@Override
	public void run(
		LexicalAnalyzerState lexicalAnalyzerState,
		Compiler compiler
	)
	{
		lexicalAnalyzerState.resetLexeme();
		
		if (lexicalAnalyzerState.getLastReadChar() == '\n')
			return;
		
		lexicalAnalyzerState.addSymbolToLexeme(
			lexicalAnalyzerState.getLastReadChar()
		);
	}
	
}
