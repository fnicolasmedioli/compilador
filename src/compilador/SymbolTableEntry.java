package compilador;

public class SymbolTableEntry {

	private int tokenID;
	private String lexeme;
	private boolean predefined;
	
	public SymbolTableEntry(int tokenID) {
		this.tokenID = tokenID;
		this.predefined = false;
	}
	
	public SymbolTableEntry(int tokenID, String lexeme) {
		this.tokenID = tokenID;
		this.lexeme = lexeme;
		this.predefined = false;
	}
	
	public SymbolTableEntry(int tokenID, boolean predefined) {
		this.tokenID = tokenID;
		this.predefined = predefined;
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
		if (lexeme != null)
			return "TokenID: " + tokenID + "\tLexema: '" + lexeme + "'";
		else
			return "TokenID: " + tokenID;
	}
}