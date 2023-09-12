package compilador;

import java.util.List;

public class Transition {
	
	private Integer newState;
	private List<Integer> semanticActionList;

	public Transition(Integer newState, List<Integer> semanticActionList)
	{
		this.newState = newState;
		if (semanticActionList != null)
			this.semanticActionList = semanticActionList;
		else
			this.semanticActionList = List.of();
	}
	
	public Integer getNewState()
	{
		return this.newState;
	}
	
	public List<Integer> getSemanticActionList()
	{
		return this.semanticActionList;
	}
	
	@Override
	public String toString()
	{
		return "[ Transition to " + this.newState + " ] with semantic actions: " + this.semanticActionList;
	}
}
