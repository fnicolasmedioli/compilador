package compilador.semantic_actions;

import compilador.Compilador;

import compilador.*;

public class SemanticAction5 implements SemanticAction {
	
	@Override
	public void run(
		LexicalAnalyzerState lexicalAnalyzerState,
		SymbolTable symbolTable )
	{		
		String lexeme = lexicalAnalyzerState.getCurrentLexeme();
		
		if (
			lexeme.length() > 2 &&
			lexeme.substring(lexeme.length() - 2).equals("_l")
		)
		{
			// Check LONG
			
			if (ConstantRange.isValidLONG(lexeme, true) == false)
			{
				CompilerMessagePrinter.error(
					"[Linea " + lexicalAnalyzerState.getCurrentLine() + "] " +
					"constante LONG fuera de rango: " + lexeme
				);
				// Return default value
				symbolTable.addConstantLONG("0_l");
				Compilador.setyylval("0_l");
				lexicalAnalyzerState.setTokenToReturn(Parser.CTE_LONG);
			}
			else
			{
				symbolTable.addConstantLONG(lexeme);
				Compilador.setyylval(lexeme);
				lexicalAnalyzerState.setTokenToReturn(Parser.CTE_LONG);
			}			
		}
		else if (
			lexeme.length() > 3 &&
			lexeme.substring(lexeme.length() - 3).equals("_ui")
		)
		{
			// Check UINT
			
			if (ConstantRange.isValidUINT(lexeme) == false)
			{
				CompilerMessagePrinter.error(
					"[Linea " + lexicalAnalyzerState.getCurrentLine() + "] " +
					"constante UINT fuera de rango: " + lexeme
				);
				symbolTable.addConstantUINT("0_ui");
				Compilador.setyylval("0_ui");
				lexicalAnalyzerState.setTokenToReturn(Parser.CTE_UINT);
			}
			else 
			{
				symbolTable.addConstantUINT(lexeme);
				Compilador.setyylval(lexeme);
				lexicalAnalyzerState.setTokenToReturn(Parser.CTE_UINT);	
			}
		}
		else
		{
			// Check DOUBLE
			
			if (ConstantRange.isValidDOUBLE(lexeme) == false)
			{
				CompilerMessagePrinter.error(
					"[Linea " + lexicalAnalyzerState.getCurrentLine() + "] " +
					"constante DOUBLE fuera de rango: " + lexeme
				);
				symbolTable.addConstantDOUBLE("0.0");
				Compilador.setyylval("0.0");
				lexicalAnalyzerState.setTokenToReturn(Parser.CTE_DOUBLE);
			}
			else
			{
				symbolTable.addConstantDOUBLE(lexeme);
				Compilador.setyylval(lexeme);
				lexicalAnalyzerState.setTokenToReturn(Parser.CTE_DOUBLE);	
			}
		}

		lexicalAnalyzerState.finishTokenReading();
	}
	
}
