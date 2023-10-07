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
		SymbolTableEntry t = symbolTable.getTokenByLexeme(lexeme);
		Compilador.setyylval(
			new TokenInfo(
				lexeme,
				new TokenLocation(
					lexicalAnalyzerState.getCurrentLine()
				)
			)
		);
		lexicalAnalyzerState.setTokenToReturn(t.getTokenID());		
		lexicalAnalyzerState.finishTokenReading();
	}
	
}
