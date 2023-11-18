package compiler;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.LinkedList;

public class SymbolTable {

	private HashMap<String, SymbolTableEntry> table;

	private int auxVarCounter = 0;

	private final Compiler compiler;
	
	public SymbolTable(Compiler compiler)
	{
		table = new HashMap<>();
		loadPredefinedTable();
		this.compiler = compiler;
	}
	
	public SymbolTableEntry addNewEntry(SymbolTableEntry entry)
	{
		String entryKey = entry.getLexeme();
		table.put(entryKey, entry);
		return entry;
	}
	
	public SymbolTableEntry addNewEntry(SymbolTableEntry entry, String key)
	{
		table.put(key, entry);
		return entry;
	}
	
	public SymbolTableEntry getEntry(String entryKey)
	{
		return table.get(entryKey);
	}
	
	public boolean isPredefined(String lexeme)
	{
		SymbolTableEntry entry = table.get(lexeme);
		
		if (entry == null || !entry.isPredefined())
			return false;
	
		return true;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		// sb.append(String.format("| %-15s | %s | %15s |%n\n", "feqfbewqhfewjfewfjew", "Token ID", "Lexema"));

		for (String key : table.keySet())
		{
			sb.append("Clave: ");
			sb.append(key);
			sb.append(" | ");
			sb.append(table.get(key));
			sb.append("\n");
		}
		return sb.toString();
	}
	
	private void loadPredefinedTable()
	{
		table = new HashMap<>();
		table.put("{", new SymbolTableEntry('{', true));
		table.put("}", new SymbolTableEntry('}', true));
		table.put("(", new SymbolTableEntry('(', true));
		table.put(")", new SymbolTableEntry(')', true));
		table.put(";", new SymbolTableEntry(';', true));
		table.put(",", new SymbolTableEntry(',', true));
		table.put("+", new SymbolTableEntry('+', true));
		table.put("-", new SymbolTableEntry('-', true));
		table.put("/", new SymbolTableEntry('/', true));
		table.put("=", new SymbolTableEntry('=', true));
		table.put("<", new SymbolTableEntry('<', true));
		table.put(">", new SymbolTableEntry('>', true));
		table.put("*", new SymbolTableEntry('*', true));
		table.put(".", new SymbolTableEntry('.', true));
		table.put(":", new SymbolTableEntry(':', true));
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
		table.put("TOD", new SymbolTableEntry(Parser.TOD, true));
		table.put("STRING", new SymbolTableEntry(Parser.STRING, true));
	}

	public String getTokenDescription(short token)
	{
		if (token == 0) return "Fin de archivo";
		if (token < 256) return "'" + (char)token + "'";
			
		if (token == Parser.CTE_STRING) return "CTE_STRING";
		if (token == Parser.CTE_LONG) return "CTE_LONG";
		if (token == Parser.CTE_UINT) return "CTE_UINT";
		if (token == Parser.DOUBLE) return "CTE_DOUBLE";
		if (token == Parser.ID) return "ID";

		for (Entry<String, SymbolTableEntry> entry : table.entrySet())
			if (entry.getValue().getTokenID() == token)
				return entry.getKey();

		return "TOKEN NO CONOCIDO";
	}

	public String createAuxVar(DataType dataType)
	{
		String name = "@aux" + auxVarCounter++;
		addNewEntry(new SymbolTableEntry(), name)
			.setAttrib(AttribKey.DATA_TYPE, dataType);
		return name;
	}

	public List<String> getChildrenOf(String entryKey)
	{
		LinkedList<String> toReturn = new LinkedList<>();

		String scope = compiler.getSemanticHelper().invertScope(entryKey);

		for (String key : table.keySet())
			if (key.endsWith(":" + scope))
				toReturn.add(key);

		for (String key : new LinkedList<String>(toReturn))
			toReturn.addAll(getChildrenOf(key));

		return toReturn;
	}

	public List<String> getConstantList()
	{
		LinkedList<String> toReturn = new LinkedList<>();

		for (String key : table.keySet())
		{
			SymbolTableEntry entry = table.get(key);
			if (entry.isPredefined()) continue;
			if (entry.getAttrib(AttribKey.IS_CONSTANT) == null) continue;

			toReturn.add(key);
		}

		return toReturn;
	}
}