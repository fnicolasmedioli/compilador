package compiler.semantic_actions;

import compiler.*;
import compiler.Compiler;

public class SemanticAction5 implements SemanticAction {
	
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
			lexicalAnalyzerState.setTokenToReturn(stEntry.getTokenID());
			lexicalAnalyzerState.finishTokenReading();
			return;
		}
		
		// Constant not in table, add it
		
		boolean validRange = true;
		
		if (
			lexeme.length() > 2 &&
			lexeme.endsWith("_l")
		)
		{
			// Check LONG
			
			if (ConstantRange.isValidLONG(lexeme, true) == false)
			{
				compiler.reportLexicalError(
					"Constante LONG fuera de rango: " + lexeme,
					new TokenLocation(lexicalAnalyzerState.getCurrentLine())
				);
				validRange = false;
			}
			
			compiler.setyylval(
				new LocatedSymbolTableEntry(
					symbolTable.addNewEntry(new SymbolTableEntry(
						Parser.CTE_LONG,
						validRange ? lexeme : null
					)
					.setAttrib(AttribKey.DATA_TYPE, DataType.LONG)),
					new TokenLocation(lexicalAnalyzerState.getCurrentLine())
				)				
			);
			lexicalAnalyzerState.setTokenToReturn(Parser.CTE_LONG);
		}
		else if (
			lexeme.length() > 3 &&
			lexeme.endsWith("_ui")
		)
		{
			// Check UINT
			
			if (ConstantRange.isValidUINT(lexeme) == false)
			{
				compiler.reportLexicalError(
					"Constante UINT fuera de rango: " + lexeme,
					new TokenLocation(lexicalAnalyzerState.getCurrentLine())
				);
				validRange = false;
			}
			
			compiler.setyylval(
				new LocatedSymbolTableEntry(
					symbolTable.addNewEntry(new SymbolTableEntry(
						Parser.CTE_UINT,
						validRange ? lexeme : null
					)
					.setAttrib(AttribKey.DATA_TYPE, DataType.UINT)),
					new TokenLocation(lexicalAnalyzerState.getCurrentLine())
				)
			);
			lexicalAnalyzerState.setTokenToReturn(Parser.CTE_UINT);
		}
		else
		{
			// Check DOUBLE
			
			if (ConstantRange.isValidDOUBLE(lexeme) == false)
			{
				compiler.reportLexicalError(
					"Constante DOUBLE fuera de rango: " + lexeme,
					new TokenLocation(lexicalAnalyzerState.getCurrentLine())
				);
				validRange = false;
			}

			compiler.setyylval(
				new LocatedSymbolTableEntry(
					symbolTable.addNewEntry(new SymbolTableEntry(
						Parser.CTE_DOUBLE,
						validRange ? lexeme : null
					)
					.setAttrib(AttribKey.DATA_TYPE, DataType.DOUBLE)),
					new TokenLocation(lexicalAnalyzerState.getCurrentLine())
				)				
			);
			lexicalAnalyzerState.setTokenToReturn(Parser.CTE_DOUBLE);
		}

		lexicalAnalyzerState.finishTokenReading();
	}
}
