package compilador;

public class Token {

	private int tokenID;
	private boolean predefined;
	
	public Token(int tokenID, boolean predefined) {
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
	
}