package compilador.semantic_actions;

import compilador.*;

public class SemanticAction8 implements SemanticAction {
	
	@Override
	public void run(
		LexicalAnalyzerState lexicalAnalyzerState,
		SymbolTable symbolTable )
	{
		String lexeme = lexicalAnalyzerState.getCurrentLexeme();
		
		SymbolTableEntry stEntry = symbolTable.getEntry(lexeme);

		if (stEntry != null)
		{
			Compilador.setyylval(new LocatedSymbolTableEntry(
				stEntry,
				new TokenLocation(lexicalAnalyzerState.getCurrentLine())
			));
		}
		else
		{
			// Add it to symbol table
			
			Compilador.setyylval(
				new LocatedSymbolTableEntry(
					symbolTable.addNewEntry(new SymbolTableEntry(
						Parser.CTE_STRING,
						lexeme
					)),
					new TokenLocation(lexicalAnalyzerState.getCurrentLine())
				)
			);
			lexicalAnalyzerState.setTokenToReturn(Parser.CTE_STRING);
		}
		
		lexicalAnalyzerState.finishTokenReading();
	}	
}
