package compiler;

import java.util.HashMap;

public class SymbolTableEntry {

	private final int tokenID;
	private String lexeme;
	private final boolean predefined;
	private final HashMap<AttribKey, Object> attribs;

	public SymbolTableEntry() {
		this.tokenID = -1;
		this.predefined = false;
		this.attribs = new HashMap<>();
	}

	public SymbolTableEntry(int tokenID) {
		this.tokenID = tokenID;
		this.predefined = false;
		this.attribs = new HashMap<>();
	}
	
	public SymbolTableEntry(int tokenID, String lexeme) {
		this.tokenID = tokenID;
		this.lexeme = lexeme;
		this.predefined = false;
		this.attribs = new HashMap<>();
	}
	
	public SymbolTableEntry(int tokenID, boolean predefined) {
		this.tokenID = tokenID;
		this.predefined = predefined;
		this.attribs = new HashMap<>();
	}	
	
	public int getTokenID()
	{
		return tokenID;
	}
	
	public void addNegativeSign()
	{
		lexeme = "-" + lexeme;
	}
	
	public String getLexeme()
	{
		return lexeme;
	}
	
	public boolean isPredefined()
	{
		return this.predefined;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("TokenID: ").append(tokenID);

		if (lexeme != null)
			sb.append(" | Lexema: '").append(lexeme).append("'");

		if(attribs.isEmpty() == false)
		{
			sb.append("\n  Atributos:");
			for (AttribKey key : attribs.keySet())
			{
				sb.append("\n");
				sb.append("    [");
				sb.append(key);
				sb.append(": ");
				sb.append(attribs.get(key));
				sb.append("]");
			}
		}

		return sb.toString();
	}

	public Object getAttrib(AttribKey key)
	{
		return this.attribs.get(key);
	}

	public SymbolTableEntry setAttrib(AttribKey attribKey, Object value)
	{
		this.attribs.put(attribKey, value);
		return this; // concatenate setAttribs :)
	}

	public boolean hasAttrib(AttribKey key)
	{
		return this.attribs.containsKey(key);
	}
}