package compilador;

public class SymbolTableEntry {

	private int tokenID;
	private String lexeme;
	private TokenLocation location;
	
	public SymbolTableEntry(int tokenID) {
		this.tokenID = tokenID;
	}
	
	public SymbolTableEntry(int tokenID, String lexeme) {
		this.tokenID = tokenID;
		this.lexeme = lexeme;
	}
	
	public SymbolTableEntry(int tokenID, String lexeme, TokenLocation location) {
		this.tokenID = tokenID;
		this.lexeme = lexeme;
		this.location = location;
	}
	
	public int getTokenID()
	{
		return tokenID;
	}
	
	public void addNegativeSign()
	{
		lexeme = "-" + lexeme;
	}
	
	public TokenLocation getLocation()
	{
		return location;
	}
	
	public String getLexeme()
	{
		return lexeme;
	}
	
	@Override
	public String toString()
	{
		if (lexeme != null)
			return "TokenID: " + tokenID + "\tUbicacion: " + location + "\tLexema: '" + lexeme + "'";
		else
			return "TokenID: " + tokenID + "\tUbicacion: " + location;
	}
}