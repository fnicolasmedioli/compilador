package compilador.semantic_actions;

import compilador.*;

public class SemanticAction4 implements SemanticAction {
	
	@Override
	public void run(
		LexicalAnalyzerState lexicalAnalyzerState,
		SymbolTable symbolTable )
	{
		
		String lexeme = lexicalAnalyzerState.getCurrentLexeme();
		
		
		if (lexeme.length() > 20)
		{
			System.out.println("WARNING: Lexeme truncated");
			lexeme = lexeme.substring(0, 20);
		}		
		
		if (symbolTable.contains(lexeme))
		{
			Token t = symbolTable.getTokenByLexeme(lexeme);
			if (t.isPredefined() == false)
				Compilador.setyylval(lexeme);
			lexicalAnalyzerState.setTokenToReturn(t.getTokenID());
		}
		else
		{
			symbolTable.addIdentifier(lexeme);
			Token t = symbolTable.getTokenByLexeme(lexeme);
			Compilador.setyylval(lexeme);
			lexicalAnalyzerState.setTokenToReturn(t.getTokenID());
		}
		
		lexicalAnalyzerState.finishTokenReading();
	}
	
}
