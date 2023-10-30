package compilador;

public class LocatedSymbolTableEntry {
	
	private SymbolTableEntry entry;
	private TokenLocation location;

	public LocatedSymbolTableEntry(SymbolTableEntry entry, TokenLocation location)
	{
		this.entry = entry;
		this.location = location;
	}
	
	public SymbolTableEntry getSTEntry()
	{
		return this.entry;
	}
	
	public TokenLocation getLocation()
	{
		return this.location;
	}
}
