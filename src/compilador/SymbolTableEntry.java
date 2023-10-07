package compilador;

import java.util.HashMap;

public class SymbolTableEntry {

	private int tokenID;
	private boolean predefined;
	
	private static HashMap<Short, String> tokenDescriptionMap;
	
	public SymbolTableEntry(int tokenID, boolean predefined) {
		this.tokenID = tokenID;
		this.predefined = predefined;
	}
	
	public int getTokenID()
	{
		return this.tokenID;
	}
	
	public boolean isPredefined()
	{
		return this.predefined;
	}
	
	public static String getTokenDescription(int token)
	{
		if (token < 256)
			return "'" + (char)token + "'";

		if (tokenDescriptionMap == null)
		{
			tokenDescriptionMap = new HashMap<>();
			tokenDescriptionMap.put(Parser.IF, "IF");
			tokenDescriptionMap.put(Parser.ELSE, "ELSE");
			tokenDescriptionMap.put(Parser.END_IF, "END_IF");
			tokenDescriptionMap.put(Parser.PRINT, "PRINT");
			tokenDescriptionMap.put(Parser.CLASS, "CLASS");
			tokenDescriptionMap.put(Parser.VOID, "VOID");
			tokenDescriptionMap.put(Parser.ID, "ID");
			tokenDescriptionMap.put(Parser.LONG, "LONG");
			tokenDescriptionMap.put(Parser.UINT, "UINT");
			tokenDescriptionMap.put(Parser.DOUBLE, "DOUBLE");
			tokenDescriptionMap.put(Parser.STRING, "STRING");
			tokenDescriptionMap.put(Parser.CTE_LONG, "CTE_LONG");
			tokenDescriptionMap.put(Parser.CTE_UINT, "CTE_UINT");
			tokenDescriptionMap.put(Parser.CTE_DOUBLE, "CTE_DOUBLE");
			tokenDescriptionMap.put(Parser.CTE_STRING, "CTE_STRING");
			tokenDescriptionMap.put(Parser.CMP_GE, "CMP_GE");
			tokenDescriptionMap.put(Parser.CMP_LE, "CMP_LE");
			tokenDescriptionMap.put(Parser.CMP_EQUAL, "CMP_EQUAL");
			tokenDescriptionMap.put(Parser.CMP_NOT_EQUAL, "CMP_NOT_EQUAL");
			tokenDescriptionMap.put(Parser.SUB_ASIGN, "SUB_ASIGN");
			tokenDescriptionMap.put(Parser.DO, "DO");
			tokenDescriptionMap.put(Parser.UNTIL, "UNTIL");
			tokenDescriptionMap.put(Parser.IMPL, "IMPL");
			tokenDescriptionMap.put(Parser.FOR, "FOR");
			tokenDescriptionMap.put(Parser.RETURN, "RETURN");
			tokenDescriptionMap.put(Parser.TOD, "TOD");
		}
		return tokenDescriptionMap.get((short)token);
	}	
}