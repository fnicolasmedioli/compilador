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
		
		SymbolTableEntry stEntry = symbolTable.getEntry(lexeme);
		
		if (stEntry != null && stEntry.isPredefined())
		{
			Compilador.setyylval(new LocatedSymbolTableEntry(
				stEntry,
				new TokenLocation(lexicalAnalyzerState.getCurrentLine())
			));
			lexicalAnalyzerState.setTokenToReturn(stEntry.getTokenID());
		}
		else
		{
			if (stEntry != null)
			{
				Compilador.setyylval(new LocatedSymbolTableEntry(
					stEntry,
					new TokenLocation(lexicalAnalyzerState.getCurrentLine())
				));
			}
			else
			{
				// Check if ID is valid (only first occurrence)
				// ID chars must be lowercase, _, or digit
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
				
				// Add to symbol table even if invalid
				
				SymbolTableEntry entry = symbolTable.addNewEntry(new SymbolTableEntry(
					Parser.ID,
					lexeme
				));
				
				Compilador.setyylval(new LocatedSymbolTableEntry(
					entry,
					new TokenLocation(lexicalAnalyzerState.getCurrentLine())
				));
			}
			lexicalAnalyzerState.setTokenToReturn(Parser.ID);
		}		
		lexicalAnalyzerState.finishTokenReading();
	}
}
