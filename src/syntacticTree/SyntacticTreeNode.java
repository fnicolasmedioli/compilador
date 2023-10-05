package syntacticTree;

public class SyntacticTreeNode extends PrintableSyntacticTree {

	private PrintableSyntacticTree child;
	private String description;
	
	public SyntacticTreeNode(String description)
	{
		this.description = description;
		this.child = null;
	}
	
	public SyntacticTreeNode(String description, Object child)
	{
		this.description = description;
		this.child = (PrintableSyntacticTree)child;
	}
	
	@Override
	public void print(int offset)
	{
		if (description != null)
			System.out.println(craftSpace(offset) + description);
		if (child != null)
			this.child.print(offset + 3);
	}
}
