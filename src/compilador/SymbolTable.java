package compilador;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

public class SymbolTable {

	private HashMap<String, SymbolTableEntry> table;
	private static HashMap<String, Short> predefinedTable;
	
	public SymbolTable()
	{
		table = new HashMap<>();
		loadPredefinedTable();
	}
	
	private String generateUniqueEntryKey()
	{
		String entryKey;

		do
		{
			entryKey = UUID.randomUUID().toString().substring(0, 6);
		} while (table.containsKey(entryKey));
		
		return entryKey;
	}
	
	public String addNewEntry(SymbolTableEntry entry)
	{
		String entryKey = generateUniqueEntryKey();
		table.put(entryKey, entry);
		return entryKey;
	}
	
	public SymbolTableEntry getEntry(String entryKey)
	{
		return table.get(entryKey);
	}
	
	public Short getPredefinedToken(String lexeme)
	{
		if (predefinedTable == null)
			loadPredefinedTable();
		return predefinedTable.get(lexeme);
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
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
	
	private static void loadPredefinedTable()
	{
		predefinedTable = new HashMap<>();
        predefinedTable.put("{", (short)'{');
        predefinedTable.put("}", (short)'}');
        predefinedTable.put("(", (short)'(');
        predefinedTable.put(")", (short)')');
        predefinedTable.put(";", (short)';');
        predefinedTable.put(",", (short)',');
        predefinedTable.put("+", (short)'+');
        predefinedTable.put("-", (short)'-');
        predefinedTable.put("/", (short)'/');
        predefinedTable.put("=", (short)'=');
        predefinedTable.put("<", (short)'<');
        predefinedTable.put(">", (short)'>');
        predefinedTable.put("*", (short)'*');
        predefinedTable.put(".", (short)'.');
        predefinedTable.put(":", (short)':');
        predefinedTable.put("IF", Parser.IF);
        predefinedTable.put("ELSE", Parser.ELSE);
        predefinedTable.put("END_IF", Parser.END_IF);
        predefinedTable.put("PRINT", Parser.PRINT);
        predefinedTable.put("CLASS", Parser.CLASS);
        predefinedTable.put("VOID", Parser.VOID);
        predefinedTable.put("LONG", Parser.LONG);
        predefinedTable.put("UINT", Parser.UINT);
        predefinedTable.put("DOUBLE", Parser.DOUBLE);
        predefinedTable.put(">=", Parser.CMP_GE);
        predefinedTable.put("<=", Parser.CMP_LE);
        predefinedTable.put("==", Parser.CMP_EQUAL);
        predefinedTable.put("!!", Parser.CMP_NOT_EQUAL);
        predefinedTable.put("-=", Parser.SUB_ASIGN);
        predefinedTable.put("DO", Parser.DO);
        predefinedTable.put("UNTIL", Parser.UNTIL);
        predefinedTable.put("IMPL", Parser.IMPL);
        predefinedTable.put("FOR", Parser.FOR);
        predefinedTable.put("RETURN", Parser.RETURN);        
        predefinedTable.put("TOD", Parser.TOD);
        predefinedTable.put("STRING", Parser.STRING);
	}

	public static String getTokenDescription(short token)
	{
		if (token == 0) return "Fin de archivo";
		if (token < 256) return "'" + (char)token + "'";
			
		if (token == Parser.CTE_STRING) return "CTE_STRING";
		if (token == Parser.CTE_LONG) return "CTE_LONG";
		if (token == Parser.CTE_UINT) return "CTE_UINT";
		if (token == Parser.DOUBLE) return "CTE_DOUBLE";
		if (token == Parser.ID) return "ID";

		for (Entry<String, Short> entry : predefinedTable.entrySet())
			if (entry.getValue() == token)
				return entry.getKey();

		return "TOKEN NO CONOCIDO";
	}
}