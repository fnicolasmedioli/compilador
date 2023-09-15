package compilador.semantic_actions;

import compilador.LexicalAnalyzerState;
import compilador.SymbolTable;

public class SemanticAction5 implements SemanticAction {
	
	private static final String MAX_LONG = "2147483648"; // pow(2, 31)
	private static final String MAX_UINT = "65535"; // pow(2, 16) - 1
	
	@Override
	public void run(
		LexicalAnalyzerState lexicalAnalyzerState,
		SymbolTable symbolTable )
	{
		String lexeme = lexicalAnalyzerState.getCurrentLexeme();
		
		if (lexeme.substring(lexeme.length() - 2).equals("_l"))
		{
			// Check LONG
			
			String num = lexeme.substring(0, lexeme.length() - 2);
			
			if (
				num.length() > MAX_LONG.length()
				|| num.compareTo(MAX_LONG) > 0
			)
				System.out.println("Error, constante fuera de rango: " + lexeme);
		}
		else if (lexeme.substring(lexeme.length() - 3).equals("_ui"))
		{
			// Check UINT
			
			String num = lexeme.substring(0, lexeme.length() - 3);
			
			if (
				num.length() > MAX_UINT.length()
				|| num.compareTo(MAX_UINT) > 0
			)
				System.out.println("Error, constante fuera de rango: " + lexeme);	
		}		
		
		System.out.println("Lexema encontrado: " + lexicalAnalyzerState.getCurrentLexeme());
		lexicalAnalyzerState.finishTokenReading();
	}
	
}
