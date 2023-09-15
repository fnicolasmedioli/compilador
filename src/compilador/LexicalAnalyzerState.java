package compilador;

public class LexicalAnalyzerState {

	private int currentSourceReadIndex;
	private int currentState;
	private String currentLexeme;
	private boolean tokenReading;
	
	public LexicalAnalyzerState()
	{
		this.currentSourceReadIndex = 0;
		this.currentState = 0;
		this.currentLexeme = "";
		this.tokenReading = true;
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
	
	public int getCurrentstate()
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
	}
	
	public void finishTokenReading()
	{
		this.tokenReading = false;
	}
}