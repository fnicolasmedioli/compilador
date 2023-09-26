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
			String trunc = lexeme.substring(0, 20);
			CompilerMessagePrinter.warning(
				"[Linea " + lexicalAnalyzerState.getCurrentLine() + "] " +
				"lexema truncado\n\t" + lexeme + " -> " + trunc
			);
			lexeme = trunc;
		}		
		
		if (symbolTable.contains(lexeme))
		{
			Token t = symbolTable.getTokenByLexeme(lexeme);
			Compilador.setyylval(lexeme);
			lexicalAnalyzerState.setTokenToReturn(t.getTokenID());
		}
		else
		{
			// ID must be lowercase, _, or digit
			
			for (char c : lexeme.toCharArray())
				if (
					c != '_'
					&& ((int)c < 48 || (int)c > 57)
					&& ((int)c < 97 || (int)c > 122)
				)
				{
					CompilerMessagePrinter.error(
						"[Linea " + lexicalAnalyzerState.getCurrentLine() + "] " +
						"los identificadores deben ser minusculas"
					);
					return;
				}		
			
			symbolTable.addIdentifier(lexeme);
			Token t = symbolTable.getTokenByLexeme(lexeme);
			Compilador.setyylval(lexeme);
			lexicalAnalyzerState.setTokenToReturn(t.getTokenID());
		}
		
		lexicalAnalyzerState.finishTokenReading();
	}
	
}
