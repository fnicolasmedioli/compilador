package compilador.semantic_actions;

import compilador.*;

public class SemanticAction6 implements SemanticAction {
	
	@Override
	public void run(
		LexicalAnalyzerState lexicalAnalyzerState,
		SymbolTable symbolTable )
	{
		String lexeme = lexicalAnalyzerState.getCurrentLexeme();
		Short tokenID = symbolTable.getPredefinedToken(lexeme);
		
		if (tokenID == null)
			Compilador.reportLexicalError("Error critico en accion semantica 6, linea: " + lexicalAnalyzerState.getCurrentLine());
		
		Compilador.setyylval(symbolTable.addNewEntry(
			new SymbolTableEntry(
				tokenID,
				null,
				new TokenLocation(lexicalAnalyzerState.getCurrentLine())
			)
		));
		
		lexicalAnalyzerState.setTokenToReturn(tokenID);

		lexicalAnalyzerState.finishTokenReading();
	}	
}
