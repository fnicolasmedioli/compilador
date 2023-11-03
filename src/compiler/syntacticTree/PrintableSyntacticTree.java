package compiler.syntacticTree;

public abstract class PrintableSyntacticTree {

	public abstract void print(int offset);
	
	protected String craftSpace(int n)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < n; i++)
			sb.append(" ");
		return sb.toString();		
	}	
}
