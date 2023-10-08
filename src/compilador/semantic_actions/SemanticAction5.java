package compilador.semantic_actions;

import compilador.*;

public class SemanticAction5 implements SemanticAction {
	
	@Override
	public void run(
		LexicalAnalyzerState lexicalAnalyzerState,
		SymbolTable symbolTable )
	{		
		String lexeme = lexicalAnalyzerState.getCurrentLexeme();
		
		boolean validRange = true;
		
		if (
			lexeme.length() > 2 &&
			lexeme.substring(lexeme.length() - 2).equals("_l")
		)
		{
			// Check LONG
			
			if (ConstantRange.isValidLONG(lexeme, true) == false)
			{
				Compilador.reportLexicalError(
					"[Linea " + lexicalAnalyzerState.getCurrentLine() + "] " +
					"constante LONG fuera de rango: " + lexeme
				);
				validRange = false;
			}
			
			Compilador.setyylval(
				symbolTable.addNewEntry(
					new SymbolTableEntry(
						Parser.CTE_LONG,
						(validRange == true) ? lexeme : null,
						new TokenLocation(lexicalAnalyzerState.getCurrentLine())
					)
				)
			);
			lexicalAnalyzerState.setTokenToReturn(Parser.CTE_LONG);
		}
		else if (
			lexeme.length() > 3 &&
			lexeme.substring(lexeme.length() - 3).equals("_ui")
		)
		{
			// Check UINT
			
			if (ConstantRange.isValidUINT(lexeme) == false)
			{
				Compilador.reportLexicalError(
					"[Linea " + lexicalAnalyzerState.getCurrentLine() + "] " +
					"constante UINT fuera de rango: " + lexeme
				);
				validRange = false;
			}
			
			Compilador.setyylval(
				symbolTable.addNewEntry(
					new SymbolTableEntry(
						Parser.CTE_UINT,
						(validRange == true) ? lexeme : null,
						new TokenLocation(lexicalAnalyzerState.getCurrentLine())
					)
				)
			);
			lexicalAnalyzerState.setTokenToReturn(Parser.CTE_UINT);
		}
		else
		{
			// Check DOUBLE
			
			if (ConstantRange.isValidDOUBLE(lexeme) == false)
			{
				Compilador.reportLexicalError(
					"[Linea " + lexicalAnalyzerState.getCurrentLine() + "] " +
					"constante DOUBLE fuera de rango: " + lexeme
				);
				validRange = false;
			}

			Compilador.setyylval(
				symbolTable.addNewEntry(
					new SymbolTableEntry(
						Parser.CTE_DOUBLE,
						(validRange == true) ? lexeme : null,
						new TokenLocation(lexicalAnalyzerState.getCurrentLine())
					)
				)
			);
			lexicalAnalyzerState.setTokenToReturn(Parser.CTE_DOUBLE);
		}

		lexicalAnalyzerState.finishTokenReading();
	}
	
}
