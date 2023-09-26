package compilador;

import java.util.HashMap;

public class SymbolTable {

	private HashMap<String, Token> table;
	
	public SymbolTable()
	{
		table = new HashMap<>();
		loadPredefinedTable();
	}
	
	public Token getTokenByLexeme(String lexeme)
	{
		return table.get(lexeme);
	}
	
	public boolean contains(String lexeme)
	{
		return table.containsKey(lexeme);
	}
	
	public void addIdentifier(String lexeme)
	{
		table.put(lexeme, new Token(Parser.ID, false));
	}
	
	public void addConstantLONG(String lexeme)
	{
		table.put(lexeme, new Token(Parser.CTE_LONG, false));
	}
	
	public void addConstantUINT(String lexeme)
	{
		table.put(lexeme, new Token(Parser.CTE_UINT, false));
	}
	
	public void addConstantDOUBLE(String lexeme)
	{
		table.put(lexeme, new Token(Parser.CTE_DOUBLE, false));
	}
	
	public void addStringLiteral(String lexeme)
	{
		table.put(lexeme, new Token(Parser.CTE_STRING, false));
	}
	
	private void loadPredefinedTable()
	{
		table.put("{", new Token((int)'{', true));
		table.put("}", new Token((int)'}', true));
		table.put("(", new Token((int)'(', true));
		table.put(")", new Token((int)')', true));
		table.put(";", new Token((int)';', true));
		table.put(",", new Token((int)',', true));
		table.put("+", new Token((int)'+', true));
		table.put("-", new Token((int)'-', true));
		table.put("/", new Token((int)'/', true));
		table.put("=", new Token((int)'=', true));
		table.put("<", new Token((int)'<', true));
		table.put(">", new Token((int)'>', true));
		table.put("*", new Token((int)'*', true));
		table.put(".", new Token((int)'.', true));
		table.put("IF", new Token(Parser.IF, true));
		table.put("ELSE", new Token(Parser.ELSE, true));
		table.put("END_IF", new Token(Parser.END_IF, true));
		table.put("PRINT", new Token(Parser.PRINT, true));
		table.put("CLASS", new Token(Parser.CLASS, true));
		table.put("VOID", new Token(Parser.VOID, true));
		table.put("LONG", new Token(Parser.LONG, true));
		table.put("UINT", new Token(Parser.UINT, true));
		table.put("DOUBLE", new Token(Parser.DOUBLE, true));
		table.put(">=", new Token(Parser.CMP_GE, true));
		table.put("<=", new Token(Parser.CMP_LE, true));
		table.put("==", new Token(Parser.CMP_EQUAL, true));
		table.put("!!", new Token(Parser.CMP_NOT_EQUAL, true));
		table.put("-=", new Token(Parser.SUB_ASIGN, true));
		table.put("DO", new Token(Parser.DO, true));
		table.put("UNTIL", new Token(Parser.UNTIL, true));
		table.put("IMPL", new Token(Parser.IMPL, true));
		table.put("FOR", new Token(Parser.FOR, true));
		table.put("RETURN", new Token(Parser.RETURN, true));
		table.put(":", new Token((int)':', true));
		table.put("TOD", new Token(Parser.TOD, true));
	}	
}