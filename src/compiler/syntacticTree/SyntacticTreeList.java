package compiler.syntacticTree;

import java.util.LinkedList;

public class SyntacticTreeList extends PrintableSyntacticTree {

	private LinkedList<PrintableSyntacticTree> nodes;
	
	public SyntacticTreeList()
	{
		this.nodes = new LinkedList<>();
	}
	
	public SyntacticTreeList(Object baseNode) // Avoid so much casting in yacc
	{
		this.nodes = new LinkedList<>();
		this.nodes.add((PrintableSyntacticTree)baseNode);
	}
	
	public SyntacticTreeList(Object baseNode, Object baseNode2) // Avoid so much casting in yacc
	{
		this.nodes = new LinkedList<>();
		this.nodes.add((PrintableSyntacticTree)baseNode);
		this.nodes.add((PrintableSyntacticTree)baseNode2);
	}
	
	public SyntacticTreeList add(Object o)
	{
		this.nodes.add((PrintableSyntacticTree)o);
		return this;
	}
	
	@Override
	public void print(int offset)
	{
		for (PrintableSyntacticTree child : this.nodes)
			child.print(offset);
	}
	
}
