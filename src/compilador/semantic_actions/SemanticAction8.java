package compilador.semantic_actions;

import compilador.*;

public class SemanticAction8 implements SemanticAction {
	
	@Override
	public void run(
		LexicalAnalyzerState lexicalAnalyzerState,
		SymbolTable symbolTable )
	{
		
		String lexeme = lexicalAnalyzerState.getCurrentLexeme();
		
		Compilador.setyylval(symbolTable.addNewEntry(
			new SymbolTableEntry(
				Parser.CTE_STRING,
				lexeme,
				new TokenLocation(lexicalAnalyzerState.getCurrentLine())
			)
		));
			
		lexicalAnalyzerState.setTokenToReturn(Parser.CTE_STRING);
		
		lexicalAnalyzerState.finishTokenReading();
	}	
}
