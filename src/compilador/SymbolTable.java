package compilador;

import java.util.HashMap;

public class SymbolTable {

	private HashMap<String, SymbolTableEntry> table;
	
	public SymbolTable()
	{
		table = new HashMap<>();
		loadPredefinedTable();
	}
	
	public SymbolTableEntry getTokenByLexeme(String lexeme)
	{
		return table.get(lexeme);
	}
	
	public void replaceLexeme(String oldLexeme, String newLexeme)
	{
		table.put(newLexeme, table.get(oldLexeme));
		table.remove(oldLexeme);
	}
	
	public boolean contains(String lexeme)
	{
		return table.containsKey(lexeme);
	}
	
	public void addIdentifier(String lexeme)
	{
		table.put(lexeme, new SymbolTableEntry(Parser.ID, false));
	}
	
	public void addConstantLONG(String lexeme)
	{
		table.put(lexeme, new SymbolTableEntry(Parser.CTE_LONG, false));
	}
	
	public void addConstantUINT(String lexeme)
	{
		table.put(lexeme, new SymbolTableEntry(Parser.CTE_UINT, false));
	}
	
	public void addConstantDOUBLE(String lexeme)
	{
		table.put(lexeme, new SymbolTableEntry(Parser.CTE_DOUBLE, false));
	}
	
	public void addStringLiteral(String lexeme)
	{
		table.put(lexeme, new SymbolTableEntry(Parser.CTE_STRING, false));
	}
	
	private void loadPredefinedTable()
	{
		table.put("{", new SymbolTableEntry((int)'{', true));
		table.put("}", new SymbolTableEntry((int)'}', true));
		table.put("(", new SymbolTableEntry((int)'(', true));
		table.put(")", new SymbolTableEntry((int)')', true));
		table.put(";", new SymbolTableEntry((int)';', true));
		table.put(",", new SymbolTableEntry((int)',', true));
		table.put("+", new SymbolTableEntry((int)'+', true));
		table.put("-", new SymbolTableEntry((int)'-', true));
		table.put("/", new SymbolTableEntry((int)'/', true));
		table.put("=", new SymbolTableEntry((int)'=', true));
		table.put("<", new SymbolTableEntry((int)'<', true));
		table.put(">", new SymbolTableEntry((int)'>', true));
		table.put("*", new SymbolTableEntry((int)'*', true));
		table.put(".", new SymbolTableEntry((int)'.', true));
		table.put("IF", new SymbolTableEntry(Parser.IF, true));
		table.put("ELSE", new SymbolTableEntry(Parser.ELSE, true));
		table.put("END_IF", new SymbolTableEntry(Parser.END_IF, true));
		table.put("PRINT", new SymbolTableEntry(Parser.PRINT, true));
		table.put("CLASS", new SymbolTableEntry(Parser.CLASS, true));
		table.put("VOID", new SymbolTableEntry(Parser.VOID, true));
		table.put("LONG", new SymbolTableEntry(Parser.LONG, true));
		table.put("UINT", new SymbolTableEntry(Parser.UINT, true));
		table.put("DOUBLE", new SymbolTableEntry(Parser.DOUBLE, true));
		table.put(">=", new SymbolTableEntry(Parser.CMP_GE, true));
		table.put("<=", new SymbolTableEntry(Parser.CMP_LE, true));
		table.put("==", new SymbolTableEntry(Parser.CMP_EQUAL, true));
		table.put("!!", new SymbolTableEntry(Parser.CMP_NOT_EQUAL, true));
		table.put("-=", new SymbolTableEntry(Parser.SUB_ASIGN, true));
		table.put("DO", new SymbolTableEntry(Parser.DO, true));
		table.put("UNTIL", new SymbolTableEntry(Parser.UNTIL, true));
		table.put("IMPL", new SymbolTableEntry(Parser.IMPL, true));
		table.put("FOR", new SymbolTableEntry(Parser.FOR, true));
		table.put("RETURN", new SymbolTableEntry(Parser.RETURN, true));
		table.put(":", new SymbolTableEntry((int)':', true));
		table.put("TOD", new SymbolTableEntry(Parser.TOD, true));
	}
	
	@Override
	public String toString()
	{
		return toString(true);
	}
	
	public String toString(boolean showPredefined)
	{
		String toPrint = "";
		
		for (String lexeme : table.keySet())
		{
			SymbolTableEntry entry = table.get(lexeme);
			if (!entry.isPredefined() || entry.isPredefined() && showPredefined)
			{
				toPrint += "Lexema: '" + lexeme + "' TokenID: '" + entry.getTokenID() + "' \n";
			}
		}
		return toPrint;
	}
}