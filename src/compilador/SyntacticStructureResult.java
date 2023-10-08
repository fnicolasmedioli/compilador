package compilador;

public class SyntacticStructureResult {

	String description;
	TokenLocation location;
	
	public SyntacticStructureResult(String description, TokenLocation location) {
		this.description = description;
		this.location = location;
	}
	
	public TokenLocation getLocation()
	{
		return this.location;
	}
	
	@Override
	public String toString()
	{
		return "" + location + ", estructura: " + description;
	}
}
