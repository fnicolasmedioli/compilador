package compiler;

import java.util.Collections;
import java.util.LinkedList;

import compiler.CompatibilityTable.ICompatibilityTable;
import jdk.nashorn.internal.ir.Symbol;

import java.util.Set;
import java.util.HashSet;

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
		String entryKey = getEntryKeyByScope(id, scope);
		if (entryKey == null) return null;
		return symbolTable.getEntry(entryKey);
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

	public void declareRecursive(LinkedList<String> varLexemeList, String scope, SymbolTableEntry dataTypeEntry, String currentClassEntryKey, boolean isRecursion)
	{
		DataType dataType = tokenIDtoDataType.get(dataTypeEntry.getTokenID());

		HashSet<String> subVarsSet = null;
		String classEntryKey = null;
		SymbolTableEntry classEntry = null;

		int varSize = 0;

		if (dataType.hasSize())
			varSize = dataType.getSize();

		if (dataType == DataType.OBJECT)
		{
			classEntryKey = getEntryKeyByScope(dataTypeEntry.getLexeme(), scope);

			if (classEntryKey == null)
			{
				compiler.reportSemanticError(
					String.format("La clase '%s' no esta definida en el ambito local", dataTypeEntry.getLexeme()), null
				);
				return;
			}

			classEntry = symbolTable.getEntry(classEntryKey);
			subVarsSet = (HashSet<String>)(classEntry.getAttrib(AttribKey.ATTRIBS_SET));

			varSize = ((MemoryAssociation)(classEntry.getAttrib(AttribKey.MEMORY_ASSOCIATION))).getSize();
		}

		// Para cada token en DATATYPE a; b; c

		for (String varLexeme : varLexemeList)
		{
			String varEntryKey = varLexeme + ":" + scope;

			if (symbolTable.getEntry(varEntryKey) != null)
			{
				compiler.reportSemanticError(
					String.format("El ID '%s' ya esta definido en el ambito local", varLexeme),
				null);
				continue;
			}

			// Declarar la variable localmente

			SymbolTableEntry varEntry = symbolTable.addNewEntry(
				new SymbolTableEntry(
					Parser.ID,
					varLexeme
				),
				varEntryKey
			)
			.setAttrib(AttribKey.ID_TYPE, IDType.VAR_ATTRIB)
			.setAttrib(AttribKey.DATA_TYPE, dataType)
			.setAttrib(AttribKey.MEMORY_ASSOCIATION, new MemoryAssociation(varEntryKey));

			// Si es un tipo de dato primitivo, setear el size inmediatamente

			if (dataType.hasSize())
				varEntry.setAttrib(AttribKey.MEMORY_ASSOCIATION, new MemoryAssociation(varEntryKey, varSize, dataType));

			// Y si es tipo objeto hacerlo recursivamente

			if (dataType == DataType.OBJECT)
			{
				// Setear el tamaño del objeto como el tamaño de todos los atributos de la clase

				varEntry.setAttrib(AttribKey.MEMORY_ASSOCIATION, new MemoryAssociation(varEntryKey, varSize, dataType));

				varEntry.setAttrib(AttribKey.INSTANCE_OF, classEntryKey);

				// Encontrar el instance of de los atributos hijo

				// LinkedList<String> subVarLexemeList = new LinkedList<>();
				// LinkedList<SymbolTableEntry> subVarDataTypeEntryList = new LinkedList<>();

				for (String subVarEntryKey : subVarsSet) {
					// sub-variable a generar
					// String subVarEntryKey = subVarName + ":" + invertScope(varEntryKey);

					SymbolTableEntry subVarEntry = symbolTable.getEntry(subVarEntryKey);

					if (subVarEntry == null)
					{
						System.out.println("Error critico 14");
						return;
					}

					String subVarName = subVarEntry.getLexeme();

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

					declareRecursive(lexeme, subVarScope, recDataTypeEntry, currentClassEntryKey, true);
				}
			}
		}

		if (currentClassEntryKey != null && isRecursion == false) {
			// Sumar el tamaño de la variable * cantidad al tamaño total de la clase
			MemoryAssociation currentClassMemoryAssociation = (MemoryAssociation)(symbolTable.getEntry(currentClassEntryKey).getAttrib(AttribKey.MEMORY_ASSOCIATION));
			currentClassMemoryAssociation.addSize(varSize * varLexemeList.size());
		}
	}

	public boolean declareClass(String scope, LocatedSymbolTableEntry classTokenData)
	{
		String classLexeme = classTokenData.getSTEntry().getLexeme();
		String classEntryKey = classLexeme + ":" + scope;

		if (alreadyDeclaredInScope(classLexeme, scope))
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
				classEntryKey
			)
			.setAttrib(AttribKey.ID_TYPE, IDType.CLASSNAME)
			.setAttrib(AttribKey.ATTRIBS_SET, new HashSet<String>())
			.setAttrib(AttribKey.METHODS_SET, new HashSet<String>())
			.setAttrib(AttribKey.MEMORY_ASSOCIATION, new MemoryAssociation(0));

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
		} else {
			type1 = listOfTriplets.getTriplet(operand1.getIndex()).getType();
		}

		if (operand2.isFinal()) {
			type2= (DataType)operand2.getstEntry().getAttrib(AttribKey.DATA_TYPE);
		} else {
			type2 = listOfTriplets.getTriplet(operand2.getIndex()).getType();
		}

		t.setDataType(compatibilityTable.calcDataType(type1, type2));

		return t;
	}

	public void declareCompos(LocatedSymbolTableEntry composTokenData, String scope, String currentClassEntryKey)
	{
		String composEntryKey = getEntryKeyByScope(composTokenData.getSTEntry().getLexeme(), scope);

		if (composEntryKey == null)
		{
			System.out.println("es null");
			return;
		}

		SymbolTableEntry composEntry = symbolTable.getEntry(composEntryKey);

		if (composEntry.getAttrib(AttribKey.ATTRIBS_SET) == null || composEntry.getAttrib(AttribKey.METHODS_SET) == null)
		{
			System.out.println("GRAN ERROR");
			return;
		}

		HashSet<String> subVarsSet = (HashSet<String>)(composEntry.getAttrib(AttribKey.ATTRIBS_SET));
		HashSet<String> subMethodsSet = (HashSet<String>)(composEntry.getAttrib(AttribKey.METHODS_SET));

		String lexeme = composTokenData.getSTEntry().getLexeme();

		// Objeto base

		symbolTable.addNewEntry(
			new SymbolTableEntry(Parser.ID, lexeme),
			lexeme + ":" + scope
		)
		.setAttrib(AttribKey.ID_TYPE, IDType.VAR_ATTRIB)
		.setAttrib(AttribKey.DATA_TYPE, DataType.OBJECT)
		.setAttrib(AttribKey.INSTANCE_OF, composEntryKey);

		// Agregar a la lista de atributos de la clase, el nuevo atributo

		SymbolTableEntry currentClassEntry = symbolTable.getEntry(currentClassEntryKey);

		Set<String> currentClassMethods = (Set<String>)(currentClassEntry.getAttrib(AttribKey.METHODS_SET));

		((HashSet<String>)(currentClassEntry.getAttrib(AttribKey.ATTRIBS_SET))).add(lexeme + ":" + scope);

		// Agregar los metodos al conjunto de metodos de la clase actual

		for (String inheritedMethodEntryKey : subMethodsSet)
		{
			// Aca se podria hacer un chequeo para eliminar entradas con el mismo nombre
			currentClassMethods.add(inheritedMethodEntryKey);
		}

		// Copiar el resto de atributos

		for (String attribEntryKey : subVarsSet)
		{
			SymbolTableEntry attribEntry = symbolTable.getEntry(attribEntryKey);

			if (attribEntry == null)
			{
				System.out.println("No se encontro el attribEntry");
				continue;
			}

			String attribName = attribEntry.getLexeme();

			DataType attribDataType = (DataType)(attribEntry.getAttrib(AttribKey.DATA_TYPE));

			SymbolTableEntry recDataTypeEntry = null;

			if (attribDataType == DataType.OBJECT)
			{
				String attribClassEntryKey = (String)attribEntry.getAttrib(AttribKey.INSTANCE_OF);
				SymbolTableEntry attribClassEntry = symbolTable.getEntry(attribClassEntryKey);
				recDataTypeEntry = symbolTable.getEntry(attribClassEntry.getLexeme());
			}
			else {
				switch (attribDataType) {
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

			LinkedList<String> t = new LinkedList<>();
			t.add(attribName);
			declareRecursive(t, scope + ":" + lexeme, recDataTypeEntry, currentClassEntryKey, true);
		}
	}
}