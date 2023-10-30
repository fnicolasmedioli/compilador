package compilador.semantic_actions;

import compilador.*;

public class SemanticAction6 implements SemanticAction {
	
	@Override
	public void run(
		LexicalAnalyzerState lexicalAnalyzerState,
		SymbolTable symbolTable )
	{
		String lexeme = lexicalAnalyzerState.getCurrentLexeme();

		SymbolTableEntry stEntry = symbolTable.getEntry(lexeme);
		
		if (stEntry.isPredefined() == false)
			Compilador.reportLexicalError("Error critico en accion semantica 6, linea: " + lexicalAnalyzerState.getCurrentLine());
		
		Compilador.setyylval(
			new LocatedSymbolTableEntry(
				stEntry,
				new TokenLocation(lexicalAnalyzerState.getCurrentLine())
			)
		);
		
		lexicalAnalyzerState.setTokenToReturn(stEntry.getTokenID());

		lexicalAnalyzerState.finishTokenReading();
	}	
}
