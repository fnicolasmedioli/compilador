package compilador;

import java.util.TreeSet;

public class ReservedWordTable {
	
	private TreeSet<String> table;
	
	public ReservedWordTable()
	{
		table.add("IF");
		table.add("ELSE");
		table.add("END_IF");
		table.add("PRINT");
		table.add("CLASS");
		table.add("VOID");
	}
	
	public boolean has(String keyword)
	{
		return table.contains(keyword);
	}	
}
