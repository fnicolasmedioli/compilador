package compilador.semantic_actions;

import compilador.*;

public class SemanticAction8 implements SemanticAction {
	
	@Override
	public void run(
		LexicalAnalyzerState lexicalAnalyzerState,
		SymbolTable symbolTable )
	{
		
		String lexeme = lexicalAnalyzerState.getCurrentLexeme();
		
		// Quitar saltos de linea ?
		
		symbolTable.addStringLiteral(lexeme);
		Token t = symbolTable.getTokenByLexeme(lexeme);
		Compilador.setyylval(lexeme);
		lexicalAnalyzerState.setTokenToReturn(t.getTokenID());		
		lexicalAnalyzerState.finishTokenReading();
	}
	
}
