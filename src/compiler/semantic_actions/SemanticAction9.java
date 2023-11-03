package compiler.semantic_actions;

import compiler.*;
import compiler.Compiler;

public class SemanticAction9 implements SemanticAction {
	
	@Override
	public void run(
		LexicalAnalyzerState lexicalAnalyzerState,
		Compiler compiler
	)
	{
		lexicalAnalyzerState.incrementCurrentLine();
	}
	
}
