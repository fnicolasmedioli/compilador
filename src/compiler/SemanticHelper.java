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
	public final static HashMap<Integer, DataType> tokenIDtoDataType;

	private final LinkedList<ForwardData> forwardClasses = new LinkedList<>();

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

	public LinkedList<String> _declareRecursive(LinkedList<String> varLexemeList, String scope, SymbolTableEntry dataTypeEntry, String currentClassEntryKey, boolean isRecursion, String originalScope)
	{
		LinkedList<String> declared = new LinkedList<String>();

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
				return declared;
			}

			classEntry = symbolTable.getEntry(classEntryKey);
			subVarsSet = (HashSet<String>)(classEntry.getAttrib(AttribKey.ATTRIBS_SET));

			varSize = ((MemoryAssociation)(classEntry.getAttrib(AttribKey.MEMORY_ASSOCIATION))).getSize();
		}


		// Para cada token en DATATYPE a; b; c

		for (String varLexeme : varLexemeList)
		{
			String varEntryKey = varLexeme + ":" + scope;

			String originalEntryKey = varLexeme + ":" + originalScope;

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

			declared.add(varEntryKey);

			boolean memorySettled = false;

			if (currentClassEntryKey != null)
			{
				varEntry.setAttrib(AttribKey.IS_ATTRIB, true);

				SymbolTableEntry currentClassEntry = symbolTable.getEntry(currentClassEntryKey);

				varEntry.setAttrib(AttribKey.ATTRIB_OF_CLASS, currentClassEntryKey);

				HashMap<String, Integer> offsetsMap = (HashMap<String, Integer>)(currentClassEntry.getAttrib(AttribKey.ATTRIBS_OFFSETS));

				int offset = ((MemoryAssociation)symbolTable.getEntry(currentClassEntryKey).getAttrib(AttribKey.MEMORY_ASSOCIATION)).getSize();

				offsetsMap.put(varEntryKey, offset);

				varEntry.setAttrib(AttribKey.MEMORY_ASSOCIATION, new MemoryAssociation(offset, varSize, dataType));

				memorySettled = true;

				if (dataType != DataType.OBJECT)
				{
					MemoryAssociation currentClassMemoryAssociation = (MemoryAssociation)(symbolTable.getEntry(currentClassEntryKey).getAttrib(AttribKey.MEMORY_ASSOCIATION));
					currentClassMemoryAssociation.addSize(varSize * varLexemeList.size());
				}
			}

			int offset = -1;

			if (isRecursion && currentClassEntryKey == null) {

				// Todos los atributos (incluidos sub-atributos) de instancias entran en este if

				varEntry.setAttrib(AttribKey.ORIGINAL_KEY, originalEntryKey);

				SymbolTableEntry originalEntry = symbolTable.getEntry(originalEntryKey);
				if (originalEntry == null)
				{
					return declared;
				}
				String higherClassEntryKey = ((originalEntry.getAttrib(AttribKey.ATTRIB_OF_CLASS) != null) ? (String)originalEntry.getAttrib(AttribKey.ATTRIB_OF_CLASS) : null);

				if (higherClassEntryKey == null)
				{
					return declared;
				}

				// Agarrar su offset de ahi

				SymbolTableEntry higherClassEntry = symbolTable.getEntry(higherClassEntryKey);

				if (higherClassEntry == null)
				{
					return declared;
				}

				HashMap<String, Integer> offsetsMap = (HashMap<String, Integer>)(higherClassEntry.getAttrib(AttribKey.ATTRIBS_OFFSETS));

				offset = offsetsMap.get(originalEntryKey);
			}

			// Si es un tipo de dato primitivo, setear el size inmediatamente

			if (dataType.hasSize() && !memorySettled)
			{
				if (offset == -1)
					varEntry.setAttrib(AttribKey.MEMORY_ASSOCIATION, new MemoryAssociation(varEntryKey, varSize, dataType));
				else
					varEntry.setAttrib(AttribKey.MEMORY_ASSOCIATION, new MemoryAssociation(offset, varSize, dataType));
			}

			// Y si es tipo objeto hacerlo recursivamente

			if (dataType == DataType.OBJECT)
			{
				// Setear el tamaño del objeto como el tamaño de todos los atributos de la clase

				if (!memorySettled)
				{
					if (offset == -1)
						varEntry.setAttrib(AttribKey.MEMORY_ASSOCIATION, new MemoryAssociation(varEntryKey, varSize, dataType));
					else
						varEntry.setAttrib(AttribKey.MEMORY_ASSOCIATION, new MemoryAssociation(offset, varSize, dataType));
				}

				varEntry.setAttrib(AttribKey.INSTANCE_OF, classEntryKey);

				// Encontrar el instance of de los atributos hijo

				for (String subVarEntryKey : subVarsSet) {
					// sub-variable a generar
					// String subVarEntryKey = subVarName + ":" + invertScope(varEntryKey);

					SymbolTableEntry subVarEntry = symbolTable.getEntry(subVarEntryKey);

					if (subVarEntry == null)
						return declared;

					String subVarName = subVarEntry.getLexeme();

					// Obtener tipo de dato de la sub-variable

					String staticSubVarEntryKey = subVarName + ":" + invertScope(classEntryKey);

					SymbolTableEntry staticSubVarEntry = symbolTable.getEntry(staticSubVarEntryKey);

					if (staticSubVarEntry == null)
						continue;

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

					if (originalScope != null) {
						if (isRecursion) {
							declared.addAll(_declareRecursive(lexeme, subVarScope, recDataTypeEntry, currentClassEntryKey, true, invertScope(varLexeme + ":" + originalScope)));
						} else {
							declared.addAll(_declareRecursive(lexeme, subVarScope, recDataTypeEntry, currentClassEntryKey, true, originalScope));
						}
					}
					else {
						declared.addAll(_declareRecursive(lexeme, subVarScope, recDataTypeEntry, currentClassEntryKey, true, null));
					}

				}
			}
		}

		return declared;
	}

	public LinkedList<String> declareRecursive(LinkedList<String> varLexemeList, String scope, SymbolTableEntry dataTypeEntry, String currentClassEntryKey)
	{
		String originalKeyScope = null;

		if (dataTypeEntry.getTokenID() == Parser.ID && currentClassEntryKey == null)
		{
			originalKeyScope = getEntryKeyByScope(dataTypeEntry.getLexeme(), scope);
			if (originalKeyScope != null)
				originalKeyScope = invertScope(originalKeyScope);
		}

		LinkedList<String> toReturn = _declareRecursive(varLexemeList, scope, dataTypeEntry, currentClassEntryKey, false, originalKeyScope);
		return toReturn;
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
		.setAttrib(AttribKey.MEMORY_ASSOCIATION, new MemoryAssociation(0))
		.setAttrib(AttribKey.ATTRIBS_OFFSETS, new HashMap<String, Integer>());

		updateForwardDeclarations(classEntryKey);

		return true;
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
		else
		{
			DataType argDataType = tokenIDtoDataType.get( ((LocatedSymbolTableEntry)argToken).getSTEntry().getTokenID() );
			return declareFunction(scope, _idTokenData)
					.setAttrib(AttribKey.ARG_TYPE, argDataType);
		}
	}

	public LinkedList<String> scopeStrToList(String scopeStr)
	{
		LinkedList<String> list = new LinkedList<>();
        Collections.addAll(list, scopeStr.substring("global:".length()).split(":"));
		return list;
	}

	public Triplet getTriplet(TripletOperand operand1, TripletOperand operand2, String operation, ListOfTriplets listOfTriplets, ICompatibilityTable compatibilityTable) {

		Triplet t = new Triplet(operation, operand1, operand2);

		DataType type1, type2;

		if (operand1.isFinal()) {
			type1 = (DataType)operand1.getstEntry().getAttrib(AttribKey.DATA_TYPE);
		} else {
			type1 = listOfTriplets.getTriplet(operand1.getIndex()).getType();
		}

		if (operand2.isFinal()) {
			type2 = (DataType)operand2.getstEntry().getAttrib(AttribKey.DATA_TYPE);
		} else {
			type2 = listOfTriplets.getTriplet(operand2.getIndex()).getType();
		}

		DataType typeAux = compatibilityTable.calcDataType(type1, type2);
		
		if (typeAux == null)
		{
			return null;
		} else {
			if (operation == "/")
				t.setDataType(DataType.DOUBLE);
			else
				t.setDataType(type1);
		}
		return t;
	}

	public void addRequestedForwardClass(ForwardData data)
	{
		forwardClasses.add(data);
	}

	public void updateForwardDeclarations(String classEntryKey)
	{
		SymbolTableEntry classEntry = symbolTable.getEntry(classEntryKey);

		LinkedList<ForwardData> clon = new LinkedList<>(forwardClasses);

		for (ForwardData data : clon)
		{
			String possibleEntryKey = getEntryKeyByScope(data.getClassLexeme(), data.getSearchScope());

			System.out.println("Possible entr key: " + possibleEntryKey);
			System.out.println("Class entry key: " + classEntryKey);


			if (possibleEntryKey != null && possibleEntryKey.equals(classEntryKey))
			{
				System.out.println("Se encuentra una forward");
				SymbolTableEntry varEntry = symbolTable.getEntry(data.getVarLexeme() + ":" + data.getSearchScope());

				// La clase que el forward buscaba es la que se declaro

				// Declarar la variable

				declareRecursive(
					new LinkedList<String>() {{ add(data.getVarLexeme()); }},
					data.getSearchScope(),
					classEntry,
					null
				);

				// Eliminarla de la lista

				forwardClasses.remove(data);
			}
		}
	}

	public boolean isForwardListEmpty()
	{
		return forwardClasses.isEmpty();
	}
}