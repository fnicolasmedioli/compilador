package compilador.semantic_actions;

import compilador.*;

public class SemanticAction6 implements SemanticAction {
	
	@Override
	public void run(
		LexicalAnalyzerState lexicalAnalyzerState,
		SymbolTable symbolTable )
	{
		String lexeme = lexicalAnalyzerState.getCurrentLexeme();
		lexicalAnalyzerState.setTokenToReturn(symbolTable.getTokenByLexeme(lexeme).getTokenID());
		lexicalAnalyzerState.finishTokenReading();
	}
	
}
