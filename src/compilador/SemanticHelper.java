package compilador;

import java.util.LinkedList;

public class SemanticHelper {
	
	private static boolean alreadyDeclaredInScope(String id, String scope)
	{
		SymbolTable symbolTable = Compilador.getSymbolTable();
		
		if (symbolTable.getEntry(id + ":" + scope) != null)
			return true;
		return false;
	}
	
	private static String removeScopeLevel(String s)
	{
		int lastColon = s.lastIndexOf(":");
		if (lastColon == -1)
			return null;
		return s.substring(0, lastColon);
	}
	
	/**
	 * Go over each scope level to get the referenced entry if reachable
	 * @param id
	 * @param scope
	 * @return
	 */
	public static SymbolTableEntry getEntryByScope(String id, String scope)
	{
		SymbolTable symbolTable = Compilador.getSymbolTable();
		
		String idscope = id + ":" + scope;
		
		while (true)
		{			
			SymbolTableEntry stEntry = symbolTable.getEntry(idscope);
			
			if (stEntry != null) return stEntry;
			
			if (idscope.length() < 6 || idscope.substring(idscope.length() - 6).equals("global"))
				return null;
			
			idscope = removeScopeLevel(idscope);
			
			if (idscope == null)
				return null;
		}
	}
	
	public static void declarePrimitivesIfPossible(ParserVal list, String scope)
	{
		SymbolTable symbolTable = Compilador.getSymbolTable();
		
		for (LocatedSymbolTableEntry e: ((LinkedList<LocatedSymbolTableEntry>)(list).obj))
			if (alreadyDeclaredInScope(e.getSTEntry().getLexeme(), scope))
				Compilador.reportSemanticError(
					"Identificador ya definido en el ambito local: " + e.getSTEntry().getLexeme(),
					e.getLocation()
				);
			else
				symbolTable.addNewEntry(
                    new SymbolTableEntry(
                        Parser.ID,
                        e.getSTEntry().getLexeme()
                    ),
                    e.getSTEntry().getLexeme() + ":" + scope
                );
	}
	
	public static void declareObjectsIfPossible(ParserVal list, String scope, LocatedSymbolTableEntry classTokenData)
	{
		SymbolTable symbolTable = Compilador.getSymbolTable();
		
		SymbolTableEntry classEntry = getEntryByScope(classTokenData.getSTEntry().getLexeme(), scope);
		
		if (classEntry == null)
		{
			Compilador.reportSemanticError(
				"Tipo de dato no definido: " + classTokenData.getSTEntry().getLexeme(),
				classTokenData.getLocation()
			);
			return;
		}
		
		/* Aca se deberia chequar si realmente es una clase o un id de una variable, por ejemplo */
		
		declarePrimitivesIfPossible(list, scope);
	}

}
