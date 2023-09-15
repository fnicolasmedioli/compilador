package compilador;

import java.util.HashMap;

public class SymbolTable {

	private HashMap<String, Token> table;
	
	public SymbolTable()
	{
		table = new HashMap<>();
		loadPredefinedTable();
	}
	
	private void loadPredefinedTable()
	{
		table.put("{", new Token(1, true));
		table.put("}", new Token(2, true));
		table.put("(", new Token(3, true));
		table.put(")", new Token(4, true));
		table.put(";", new Token(5, true));
		table.put(",", new Token(6, true));
		table.put("+", new Token(7, true));
		table.put("-", new Token(8, true));
		table.put("-=", new Token(9, true));
		table.put("/", new Token(10, true));
		table.put("=", new Token(11, true));
		table.put("==", new Token(12, true));
		table.put("<", new Token(13, true));
		table.put("<=", new Token(14, true));
		table.put(">", new Token(15, true));
		table.put(">=", new Token(16, true));
		table.put("!!", new Token(17, true));
		table.put("*", new Token(18, true));
		table.put("IF", new Token(19, true));
		table.put("ELSE", new Token(20, true));
		table.put("END_IF", new Token(21, true));
		table.put("PRINT", new Token(22, true));
		table.put("CLASS", new Token(23, true));
		table.put("VOID", new Token(24, true));
	}	
}