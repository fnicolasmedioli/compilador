package compiler;

import java.util.Collections;
import java.util.LinkedList;

import compiler.CompatibilityTable.ICompatibilityTable;
import jdk.nashorn.internal.ir.Symbol;

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

		tokenIDtoDataType.put((int)Parser.ID, DataType.OBJECT);

		tokenIDtoDataType.put((int)Parser.CTE_DOUBLE, DataType.LONG);
		tokenIDtoDataType.put((int)Parser.CTE_STRING, DataType.LONG);
		tokenIDtoDataType.put((int)Parser.CTE_UINT, DataType.LONG);
		tokenIDtoDataType.put((int)Parser.CTE_LONG, DataType.LONG);
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

	public String getEntryKeyByScope(String id, String scope)
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
	 * Funcionamiento: saludar:global:persona -> global:persona
	 */
	public String removeLexemeFromKey(String key)
	{
		int index = key.indexOf(":global");
		return key.substring(index + 1);
	}

	/**
	 * Útil cuando se require generar el scope para métodos, a partir del key de la clase
	 * Ejemplo:
	 *   Clave clase: persona:global
	 *   La clave del método sería: nombre_metodo:global:persona
	 * @param classScope
	 * @return
	 */
	public String invertScope(String classScope)
	{
		int i = classScope.indexOf(":global");
		return classScope.substring(i+1) + ":" + classScope.substring(0, i);
	}

	private LinkedList<SymbolTableEntry> entriesKeysToEntries(LinkedList<String> entriesKeys)
	{
		LinkedList<SymbolTableEntry> entries = new LinkedList<>();
		for (String entryKey : entriesKeys)
			entries.push(symbolTable.getEntry(entryKey));
		return entries;
	}

	/**
	 * Add for each ID of the list, an entry in the symbol table
	 * as ID:[scope] with attributes, if possible.
	 * @param list Must be a list of LocatedSymbolTableEntry, wrapped into a ParserVal
	 * @param scope Scope to declare the vars in
	 * @param dataType
	 */
	private LinkedList<String> declareIDList(ParserVal list, String scope, DataType dataType)
	{
		LinkedList<String> entriesKeys = new LinkedList<>();

		for (LocatedSymbolTableEntry e: (LinkedList<LocatedSymbolTableEntry>)(list.obj))
			if (alreadyDeclaredInScope(e.getSTEntry().getLexeme(), scope))
				compiler.reportSemanticError(
					"Identificador ya definido en el ambito local: " + e.getSTEntry().getLexeme(),
					e.getLocation()
				);
			else
			{
				String entryKey = e.getSTEntry().getLexeme() + ":" + scope;
				symbolTable.addNewEntry(
					new SymbolTableEntry(
						Parser.ID,
						e.getSTEntry().getLexeme()
					),
					entryKey
				)
				.setAttrib(AttribKey.ID_TYPE, IDType.VAR_ATTRIB)
				.setAttrib(AttribKey.DATA_TYPE, dataType);

				entriesKeys.push(entryKey);
			}

		return entriesKeys;
	}

	public LinkedList<String> declarePrimitiveList(ParserVal list, String scope, SymbolTableEntry dataTypeEntry)
	{
		DataType dataType = tokenIDtoDataType.get(dataTypeEntry.getTokenID());
		if (dataType == null)
		{
			System.out.println("Error critico!");
			return null;
		}
		return declareIDList(list, scope, dataType);
	}

	public LinkedList<String> declareObjectList(ParserVal list, String scope, LocatedSymbolTableEntry classTokenData)
	{
		// Find symbol table entry for class ID if reachable
		String classEntryKey = getEntryKeyByScope(classTokenData.getSTEntry().getLexeme(), scope);

		if (classEntryKey == null)
		{
			compiler.reportSemanticError(
				"Tipo de dato no definido: " + classTokenData.getSTEntry().getLexeme(),
				classTokenData.getLocation()
			);
			return null;
		}

		SymbolTableEntry classEntry = symbolTable.getEntry(classEntryKey);

		/* Aca se deberia chequar si realmente es una clase o un id de una variable, por ejemplo */

		LinkedList<String> declaredEntriesKeys = declareIDList(list, scope, DataType.OBJECT);
		LinkedList<SymbolTableEntry> declaredEntries = entriesKeysToEntries(declaredEntriesKeys);

		for (SymbolTableEntry entry : declaredEntries)
			entry.setAttrib(AttribKey.INSTANCE_OF, classEntryKey);

		// Generar los atributos dentro del objeto

		System.out.println(declaredEntriesKeys);

		return declaredEntriesKeys;
	}

	public void declareRecursive(LinkedList<String> varLexemeList, String scope, SymbolTableEntry dataTypeEntry)
	{
		DataType dataType = tokenIDtoDataType.get(dataTypeEntry.getTokenID());

		HashMap<String, String> subVarsMap = null;
		String classEntryKey = null;
		SymbolTableEntry classEntry = null;

		if (dataType == DataType.OBJECT)
		{
			classEntryKey = getEntryKeyByScope(dataTypeEntry.getLexeme(), scope);

			if (classEntryKey == null)
			{
				System.out.println("Error critico 25");
				return;
			}

			classEntry = symbolTable.getEntry(classEntryKey);

			subVarsMap = (HashMap<String, String>)(classEntry.getAttrib(AttribKey.ATTRIBS_MAP));
		}

		// Para cada token en DATATYPE a; b; c

		for (String varLexeme : varLexemeList)
		{
			String varEntryKey = varLexeme + ":" + scope;

			// Declarar la variable localmente

			SymbolTableEntry varEntry = symbolTable.addNewEntry(
				new SymbolTableEntry(
					Parser.ID,
					varLexeme
				),
				varEntryKey
			)
			.setAttrib(AttribKey.ID_TYPE, IDType.VAR_ATTRIB)
			.setAttrib(AttribKey.DATA_TYPE, dataType);

			// Y si es tipo objeto hacerlo recursivamente

			if (dataType == DataType.OBJECT)
			{
				varEntry.setAttrib(AttribKey.INSTANCE_OF, classEntryKey);

				// Encontrar el instance of de los atributos hijo

				// LinkedList<String> subVarLexemeList = new LinkedList<>();
				// LinkedList<SymbolTableEntry> subVarDataTypeEntryList = new LinkedList<>();

				for (String subVarName : subVarsMap.keySet()) {
					// sub-variable a generar
					String subVarEntryKey = subVarName + ":" + invertScope(varEntryKey);

					// Obtener tipo de dato de la sub-variable

					String staticSubVarEntryKey = subVarName + ":" + invertScope(classEntryKey);

					SymbolTableEntry staticSubVarEntry = symbolTable.getEntry(staticSubVarEntryKey);

					if (staticSubVarEntry == null)
					{
						System.out.println("Error critico, no se encontro la variable referenciada como hija de la clase");
						continue;
					}

					DataType staticSubVarDataType = (DataType)staticSubVarEntry.getAttrib(AttribKey.DATA_TYPE);

					SymbolTableEntry recDataTypeEntry = null;

					if (staticSubVarDataType == DataType.OBJECT)
					{
						String subVarClassEntryKey = (String)staticSubVarEntry.getAttrib(AttribKey.INSTANCE_OF);
						SymbolTableEntry subVarClassEntry = symbolTable.getEntry(subVarClassEntryKey);
						recDataTypeEntry = symbolTable.getEntry(subVarClassEntry.getLexeme());
					}
					else
					{
						switch (staticSubVarDataType)
						{
							case LONG:
								recDataTypeEntry = symbolTable.getEntry("LONG");
								break;
							case UINT:
								recDataTypeEntry = symbolTable.getEntry("UINT");
								break;
							case STRING:
								recDataTypeEntry = symbolTable.getEntry("STRING");
								break;
							case DOUBLE:
								recDataTypeEntry = symbolTable.getEntry("DOUBLE");
								break;
							case BOOLEAN:
								recDataTypeEntry = symbolTable.getEntry("BOOLEAN");
								break;
						}
					}

					LinkedList<String> lexeme = new LinkedList<>();
					lexeme.add(subVarName);
					String subVarScope = invertScope(varEntryKey);

					declareRecursive(lexeme, subVarScope, recDataTypeEntry);
				}
			}

		}
	}

	public boolean declareClass(String scope, LocatedSymbolTableEntry classTokenData)
	{
		SymbolTableEntry classEntry = getEntryByScope(classTokenData.getSTEntry().getLexeme(), scope);

		if (alreadyDeclaredInScope(classTokenData.getSTEntry().getLexeme(), scope))
		{
			compiler.reportSemanticError(
				"Clase ya definida en el ambito local: " + classTokenData.getSTEntry().getLexeme(),
				classTokenData.getLocation()
			);
			return false;
		}
		else
			symbolTable.addNewEntry(
				new SymbolTableEntry(
					Parser.ID,
					classTokenData.getSTEntry().getLexeme()
				),
			classTokenData.getSTEntry().getLexeme() + ":" + scope
			)
			.setAttrib(AttribKey.ID_TYPE, IDType.CLASSNAME)
			.setAttrib(AttribKey.ATTRIBS_MAP, new HashMap<String, String>());

		return true;
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

	public SymbolTableEntry declareFunction(String scope, Object _idTokenData)
	{
		LocatedSymbolTableEntry idTokenData = (LocatedSymbolTableEntry)_idTokenData;

		return symbolTable.addNewEntry(
			new SymbolTableEntry(
				Parser.ID,
				idTokenData.getSTEntry().getLexeme()
			),
		idTokenData.getSTEntry().getLexeme() + ":" + scope
		)
		.setAttrib(AttribKey.ID_TYPE, IDType.FUNC_METHOD);
	}

	public SymbolTableEntry declareFunction(String scope, Object _idTokenData, Object argToken)
	{
		if (argToken == null)
			return declareFunction(scope, _idTokenData);

		DataType argDataType = tokenIDtoDataType.get( ((LocatedSymbolTableEntry)argToken).getSTEntry().getTokenID() );
		return declareFunction(scope, _idTokenData).setAttrib(AttribKey.ARG_TYPE, argDataType);
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

	public Triplet getTriplet(TripletOperand operand1, TripletOperand operand2, String operation, ListOfTriplets listOfTriplets, ICompatibilityTable compatibilityTable){

		Triplet t = new Triplet(operation, operand1, operand2 );

		DataType type1, type2;

		if (operand1.isFinal()) {
			type1= (DataType)operand1.getstEntry().getAttrib(AttribKey.DATA_TYPE);
		}else {
			type1 = listOfTriplets.getTriplet(operand1.getIndex()).getType();
		}

		if (operand2.isFinal()) {
			type2= (DataType)operand2.getstEntry().getAttrib(AttribKey.DATA_TYPE);
		}else {
			type2 = listOfTriplets.getTriplet(operand2.getIndex()).getType();
		}

		t.setDataType(compatibilityTable.calcDataType(type1, type2));

		return t;

	}
}