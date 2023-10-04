package compilador;

import java.util.LinkedList;

public class SyntacticTreeNode {

	private LinkedList<SyntacticTreeNode> childs;
	private String description;
	
	public SyntacticTreeNode(String description)
	{
		this.description = description;
		this.childs = new LinkedList<>();
	}
	
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public SyntacticTreeNode addChild(Object child)
	{
		this.childs.add((SyntacticTreeNode)child);
		return this;
	}
	
	public LinkedList<SyntacticTreeNode> getChilds()
	{
		return this.childs;
	}
	
	public int childrenCount()
	{
		return this.childs.size();
	}
	
	public static Object merge(Object a, Object b)
	{
		SyntacticTreeNode base, complement;

		if (((SyntacticTreeNode)a).childrenCount() > ((SyntacticTreeNode)b).childrenCount())
		{
			base = (SyntacticTreeNode)a;
			complement = (SyntacticTreeNode)b;
		}
		else
		{
			base = (SyntacticTreeNode)b;
			complement = (SyntacticTreeNode)a;
		}
		
		for (SyntacticTreeNode c : complement.getChilds())
			base.addChild(c);
		
		base.setDescription(null);
		return base;
	}
	
	public void print()
	{
		System.out.println(description);
		
		for (SyntacticTreeNode c : this.childs)
			c.print();
	}
}
