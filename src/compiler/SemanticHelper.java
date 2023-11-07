package compiler;

import com.sun.org.apache.bcel.internal.generic.FSUB;

import java.util.Collections;
import java.util.LinkedList;
import java.util.HashMap;

public class SemanticHelper {

	private final Compiler compiler;
	private final SymbolTable symbolTable;

	private final static HashMap<Integer, DataType> tokenIDtoDataType;

	static {
		tokenIDtoDataType = new HashMap<>();
		tokenIDtoDataType.put((int)Parser.DOUBLE, DataType.DOUBLE);
		tokenIDtoDataType.put((int)Parser.STRING, DataType.STRING);
		tokenIDtoDataType.put((int)Parser.UINT, DataType.UINT);
		tokenIDtoDataType.put((int)Parser.LONG, DataType.LONG);
	}

	public SemanticHelper(Compiler compiler)
	{
		this.compiler = compiler;
		this.symbolTable = compiler.getSymbolTable();
	}
	
	public boolean alreadyDeclaredInScope(String id, String scope)
	{
		return symbolTable.getEntry(id + ":" + scope) != null;
	}
	
	private String removeScopeLevel(String s)
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
	public SymbolTableEntry getEntryByScope(String id, String scope)
	{
		String idscope = id + ":" + scope;
		
		while (true)
		{			
			SymbolTableEntry stEntry = symbolTable.getEntry(idscope);
			
			if (stEntry != null)
				return stEntry;
			
			if (idscope.length() < 7 || idscope.endsWith(":global"))
				return null;
			
			idscope = removeScopeLevel(idscope);
			
			if (idscope == null)
				return null;
		}
	}

	public String getKeyByScope(String id, String scope)
	{
		String idscope = id + ":" + scope;

		while (true)
		{
			SymbolTableEntry stEntry = symbolTable.getEntry(idscope);

			if (stEntry != null)
				return idscope;

			if (idscope.length() < 7 || idscope.endsWith(":global"))
				return null;

			idscope = removeScopeLevel(idscope);

			if (idscope == null)
				return null;
		}
	}

	/**
	 * Elimina el lexema del idscope
	 * Ejemplo: saludar:global:persona -> global:persona
	 */
	public String removeLexemeFromKey(String key)
	{
		int index = key.indexOf(":global");
		return key.substring(index + 1);
	}

	/**
	 * Add for each ID of the list, an entry in the symbol table
	 * as ID:[scope] with attributes, if possible.
	 * @param list Must be a list of LocatedSymbolTableEntry, wrapped into a ParserVal
	 * @param scope Scope to declare the vars in
	 * @param dataType
	 */
	private void declareIDList(ParserVal list, String scope, DataType dataType)
	{
		for (LocatedSymbolTableEntry e: (LinkedList<LocatedSymbolTableEntry>)(list.obj))
			if (alreadyDeclaredInScope(e.getSTEntry().getLexeme(), scope))
				compiler.reportSemanticError(
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
				)
				.setAttrib(AttribKey.ID_TYPE, IDType.VAR_ATTRIB)
				.setAttrib(AttribKey.DATA_TYPE, dataType);
	}

	public void declarePrimitiveList(ParserVal list, String scope, SymbolTableEntry dataTypeEntry)
	{
		DataType dataType = tokenIDtoDataType.get(dataTypeEntry.getTokenID());
		if (dataType == null)
		{
			System.out.println("Error critico!");
			return;
		}
		declareIDList(list, scope, dataType);
	}
	
	public void declareObjectList(ParserVal list, String scope, LocatedSymbolTableEntry classTokenData)
	{
		// Find symbol table entry for class ID if reachable
		SymbolTableEntry classEntry = getEntryByScope(classTokenData.getSTEntry().getLexeme(), scope);
		
		if (classEntry == null)
		{
			compiler.reportSemanticError(
				"Tipo de dato no definido: " + classTokenData.getSTEntry().getLexeme(),
				classTokenData.getLocation()
			);
			return;
		}
		
		/* Aca se deberia chequar si realmente es una clase o un id de una variable, por ejemplo */
		
		declareIDList(list, scope, DataType.OBJECT);
	}

	public void declareClass(String scope, LocatedSymbolTableEntry classTokenData)
	{
		SymbolTableEntry classEntry = getEntryByScope(classTokenData.getSTEntry().getLexeme(), scope);

		if (alreadyDeclaredInScope(classTokenData.getSTEntry().getLexeme(), scope))
			compiler.reportSemanticError(
				"Clase ya definida en el ambito local: " + classTokenData.getSTEntry().getLexeme(),
					classTokenData.getLocation()
			);
		else
			symbolTable.addNewEntry(
				new SymbolTableEntry(
					Parser.ID,
					classTokenData.getSTEntry().getLexeme()
				),
			classTokenData.getSTEntry().getLexeme() + ":" + scope
			)
			.setAttrib(AttribKey.ID_TYPE, IDType.CLASSNAME);
	}

	public void declareArg(String scope, Object _argNameTokenData, Object _dataTypeTokenData)
	{
		LocatedSymbolTableEntry argNameTokenData = (LocatedSymbolTableEntry)_argNameTokenData;
		LocatedSymbolTableEntry dataTypeTokenData = (LocatedSymbolTableEntry)_dataTypeTokenData;

		DataType dataType = tokenIDtoDataType.get(dataTypeTokenData.getSTEntry().getTokenID());
		if (dataType == null)
		{
			System.out.println("Error critico!");
			return;
		}

		symbolTable.addNewEntry(
			new SymbolTableEntry(
				Parser.ID,
				argNameTokenData.getSTEntry().getLexeme()
			),
		argNameTokenData.getSTEntry().getLexeme() + ":" + scope
		)
		.setAttrib(AttribKey.ID_TYPE, IDType.ARGNAME)
		.setAttrib(AttribKey.DATA_TYPE, dataType);
	}

	public void declareFunction(String scope, Object _idTokenData)
	{
		LocatedSymbolTableEntry idTokenData = (LocatedSymbolTableEntry)_idTokenData;

		symbolTable.addNewEntry(
			new SymbolTableEntry(
				Parser.ID,
				idTokenData.getSTEntry().getLexeme()
			),
		idTokenData.getSTEntry().getLexeme() + ":" + scope
		)
		.setAttrib(AttribKey.ID_TYPE, IDType.FUNC_METHOD);
	}

	public void declareComposition(String scope, Object _idTokenData)
	{
		LocatedSymbolTableEntry idTokenData = (LocatedSymbolTableEntry)_idTokenData;

		if (alreadyDeclaredInScope(idTokenData.getSTEntry().getLexeme(), scope))
		{
			compiler.reportSemanticError("Herencia por composicion duplicada", idTokenData.getLocation());
			return;
		}

		/* Ver si el ID referido por la composicion, existe */

		SymbolTableEntry classEntry = getEntryByScope(idTokenData.getSTEntry().getLexeme(), scope);

		if (classEntry == null)
		{
			compiler.reportSemanticError("La clase referenciada por la composicion no existe", idTokenData.getLocation());
			return;
		}

		/* Ver si el ID es una clase */

		if (classEntry.getAttrib(AttribKey.ID_TYPE) != IDType.CLASSNAME)
		{
			compiler.reportSemanticError("El ID de la composicion no hace referencia a una clase", idTokenData.getLocation());
			return;
		}

		symbolTable.addNewEntry(
			new SymbolTableEntry(
				Parser.ID,
				idTokenData.getSTEntry().getLexeme()
			),
			idTokenData.getSTEntry().getLexeme() + ":" + scope
		)
		.setAttrib(AttribKey.ID_TYPE, IDType.COMPOSITION);
	}

	public LinkedList<String> scopeStrToList(String scopeStr)
	{
		LinkedList<String> list = new LinkedList<>();
        Collections.addAll(list, scopeStr.substring("global:".length()).split(":"));
		return list;
	}
}