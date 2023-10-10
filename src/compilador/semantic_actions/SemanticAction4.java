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
				"Lexema truncado\n\t" + lexeme + " -> " + trunc
			);
			lexeme = trunc;
		}
		
		Short predefinedToken = symbolTable.getPredefinedToken(lexeme);
		boolean isPredefined = (predefinedToken != null) ? true : false;
		
		if (isPredefined)
		{
			SymbolTableEntry newEntry = new SymbolTableEntry(
				predefinedToken,
				null,
				new TokenLocation(lexicalAnalyzerState.getCurrentLine())
			);
			String entryKey = symbolTable.addNewEntry(newEntry);

			Compilador.setyylval(entryKey);
			lexicalAnalyzerState.setTokenToReturn(predefinedToken);
		}
		else
		{
			// ID must be lowercase, _, or digit
			// and first char must be lowercase or _
			
			boolean validID = true;
			
			char firstChar = lexeme.charAt(0);
			
			if (
				firstChar != '_'
				&& ((int)firstChar < 97 || (int)firstChar > 122)
			)
				validID = false;
			else
				for (char c : lexeme.toCharArray())
					if (
						c != '_'
						&& ((int)c < 48 || (int)c > 57)
						&& ((int)c < 97 || (int)c > 122)
					)
					{
						Compilador.reportLexicalError(
							"Identificador invalido: " + lexeme,
							new TokenLocation(lexicalAnalyzerState.getCurrentLine())
						);
						validID = false;
						break;
					}
			
			String entryKey = symbolTable.addNewEntry(
				new SymbolTableEntry(
					Parser.ID,
					(validID == true) ? lexeme : null,
					new TokenLocation(lexicalAnalyzerState.getCurrentLine())
				)
			);
			
			Compilador.setyylval(entryKey);
			lexicalAnalyzerState.setTokenToReturn(Parser.ID);
		}
		
		lexicalAnalyzerState.finishTokenReading();
	}
	
}
