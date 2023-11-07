package compiler.semantic_actions;

import compiler.*;
import compiler.Compiler;

public class SemanticAction6 implements SemanticAction {
	
	@Override
	public void run(
		LexicalAnalyzerState lexicalAnalyzerState,
		Compiler compiler
	)
	{
		CompilerMessagePrinter messagePrinter = compiler.getMessagePrinter();
		SymbolTable symbolTable = compiler.getSymbolTable();
		String lexeme = lexicalAnalyzerState.getCurrentLexeme();
		SymbolTableEntry stEntry = symbolTable.getEntry(lexeme);
		
		if (stEntry.isPredefined() == false)
			compiler.reportLexicalError("Error critico en accion semantica 6", new TokenLocation(lexicalAnalyzerState.getCurrentLine()));

		compiler.setyylval(
			new LocatedSymbolTableEntry(
				stEntry,
				new TokenLocation(lexicalAnalyzerState.getCurrentLine())
			)
		);
		
		lexicalAnalyzerState.setTokenToReturn(stEntry.getTokenID());

		lexicalAnalyzerState.finishTokenReading();
	}	
}
