package compiler;

import java.util.Comparator;

public class SyntacticStructureResultComparator implements Comparator<SyntacticStructureResult> {

	@Override
	public int compare(SyntacticStructureResult a, SyntacticStructureResult b)
	{
		if (a.getLocation().greaterThan(b.getLocation())) return 1;
		if (b.getLocation().greaterThan(a.getLocation())) return -1;
		return 0;
	}
	
}
