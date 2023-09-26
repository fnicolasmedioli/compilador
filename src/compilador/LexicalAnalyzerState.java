package compilador;

public class LexicalAnalyzerState {

	private int currentSourceReadIndex;
	private int currentState;
	private String currentLexeme;
	private boolean tokenReading;
	private char lastReadChar;
	private int tokenToReturn;
	private int currentLine;
	
	public LexicalAnalyzerState()
	{
		this.currentSourceReadIndex = 0;
		this.currentState = 0;
		this.currentLexeme = "";
		this.tokenReading = true;
		this.lastReadChar = '\0';
		this.tokenToReturn = 0;
		this.currentLine = 1;
	}
	
	public int getCurrentLine()
	{
		return this.currentLine;
	}
	
	public void incrementCurrentLine()
	{
		this.currentLine++;
	}
	
	public void setTokenToReturn(int t)
	{
		this.tokenToReturn = t;
	}
	
	public int getTokenToReturn()
	{
		return this.tokenToReturn;
	}
	
	public char getLastReadChar()
	{
		return this.lastReadChar;
	}
	
	public void setLastReadChar(char c)
	{
		this.lastReadChar = c;
	}
	
	public void decrementReadIndex()
	{
		this.currentSourceReadIndex--;
	}
	
	public void incrementReadIndex()
	{
		this.currentSourceReadIndex++;
	}
	
	public int getReadIndex()
	{
		return this.currentSourceReadIndex;
	}
	
	public void setNewState(int newState)
	{
		this.currentState = newState;
	}
	
	public int getCurrentState()
	{
		return this.currentState;
	}
	
	public void resetLexeme()
	{
		this.currentLexeme = "";
	}
	
	public String getCurrentLexeme()
	{
		return this.currentLexeme;
	}
	
	public void addSymbolToLexeme(char c)
	{
		this.currentLexeme += c;
	}

	public boolean tokenReading()
	{
		return this.tokenReading;
	}
	
	public void startTokenReading()
	{
		this.tokenReading = true;
		this.currentState = 0;
	}
	
	public void finishTokenReading()
	{
		this.tokenReading = false;
	}
}