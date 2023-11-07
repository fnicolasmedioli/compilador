package compiler.semantic_actions;

import compiler.*;
import compiler.Compiler;

public class SemanticAction8 implements SemanticAction {
	
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

		if (stEntry != null)
		{
			compiler.setyylval(new LocatedSymbolTableEntry(
				stEntry,
				new TokenLocation(lexicalAnalyzerState.getCurrentLine())
			));
		}
		else
		{
			// Add it to symbol table
			
			compiler.setyylval(
				new LocatedSymbolTableEntry(
					symbolTable.addNewEntry(new SymbolTableEntry(
						Parser.CTE_STRING,
						lexeme
					)
					.setAttrib(AttribKey.DATA_TYPE, DataType.STRING)),
					new TokenLocation(lexicalAnalyzerState.getCurrentLine())
				)
			);
			lexicalAnalyzerState.setTokenToReturn(Parser.CTE_STRING);
		}
		
		lexicalAnalyzerState.finishTokenReading();
	}	
}
