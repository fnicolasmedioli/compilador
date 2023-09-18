package compilador.semantic_actions;

import compilador.Compilador;
import compilador.LexicalAnalyzerState;
import compilador.SymbolTable;

public class SemanticAction5 implements SemanticAction {
	
	private static final String MAX_LONG = "2147483648"; // pow(2, 31)
	private static final String MAX_UINT = "65535"; // pow(2, 16) - 1
	private static final double MIN_DOUBLE = 2.2250738585072014E-308;
	private static final double MAX_DOUBLE = 1.7976931348623157E+308;
	
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
			
			String num = lexeme.substring(0, lexeme.length() - 2);
			
			if (
				num.length() > MAX_LONG.length()
				|| (num.length() == MAX_LONG.length() && num.compareTo(MAX_LONG) > 0)
			)
				System.out.println("Error, constante fuera de rango: " + lexeme);
			
			symbolTable.addConstantLONG(lexeme);
			Compilador.setyylval(lexeme);
			lexicalAnalyzerState.setTokenToReturn(symbolTable.getTokenByLexeme(lexeme).getTokenID());
		}
		else if (
			lexeme.length() > 3 &&
			lexeme.substring(lexeme.length() - 3).equals("_ui")
		)
		{
			// Check UINT
			
			String num = lexeme.substring(0, lexeme.length() - 3);
			
			if (
				num.length() > MAX_UINT.length()
				|| (num.length() == MAX_UINT.length() && num.compareTo(MAX_UINT) > 0)
			)
				System.out.println("Error, constante fuera de rango: " + lexeme);
			
			symbolTable.addConstantUINT(lexeme);
			Compilador.setyylval(lexeme);
			lexicalAnalyzerState.setTokenToReturn(symbolTable.getTokenByLexeme(lexeme).getTokenID());
		}
		else
		{
			// Check DOUBLE
			
			double conv = Double.parseDouble(lexeme.replace('D', 'e').replace('d', 'e'));
			
			if (
				conv != 0 &&
				(conv > MAX_DOUBLE || conv < MIN_DOUBLE)
			)
				System.out.println("Error, constante fuera de rango: " + lexeme);
			
			symbolTable.addConstantDOUBLE(lexeme);
			Compilador.setyylval(lexeme);
			lexicalAnalyzerState.setTokenToReturn(symbolTable.getTokenByLexeme(lexeme).getTokenID());			
		}

		lexicalAnalyzerState.finishTokenReading();
	}
	
}
