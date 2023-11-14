%{

import java.util.LinkedList;
import compiler.semantic.*;
import compiler.CompatibilityTable.*;
import java.util.Vector;

%}

%token IF ELSE END_IF PRINT CLASS VOID ID
       LONG UINT DOUBLE STRING
       CTE_LONG CTE_UINT CTE_DOUBLE CTE_STRING
       CMP_GE CMP_LE CMP_EQUAL CMP_NOT_EQUAL
       SUB_ASIGN
       DO UNTIL IMPL FOR RETURN TOD

%start programa

%%

programa
    : '{' lista_sentencias '}'
    | '{' '}'
    ;

comparador
    : CMP_GE
        {
            YACCDataUnit data = new YACCDataUnit();
            data.lexeme = ">=";
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);
            $$ = new ParserVal(data);
        }
    | CMP_LE
        {
            YACCDataUnit data = new YACCDataUnit();
            data.lexeme = "<=";
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);
            $$ = new ParserVal(data);
        }
    | CMP_EQUAL
        {
            YACCDataUnit data = new YACCDataUnit();
            data.lexeme = "==";
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);
            $$ = new ParserVal(data);
        }
    | CMP_NOT_EQUAL
        {
            YACCDataUnit data = new YACCDataUnit();
            data.lexeme = "!!";
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);
            $$ = new ParserVal(data);
        }
    | '>'
        {
            YACCDataUnit data = new YACCDataUnit();
            data.lexeme = ">";
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);
            $$ = new ParserVal(data);
        }
    | '<'
        {
            YACCDataUnit data = new YACCDataUnit();
            data.lexeme = "<";
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);
            $$ = new ParserVal(data);
        }
    ;

condicion
    : expr comparador expr
        {
            String compLexeme = ((YACCDataUnit)$2.obj).lexeme;

            YACCDataUnit data1 = (YACCDataUnit)$1.obj;
            YACCDataUnit data3 = (YACCDataUnit)$3.obj;

            Triplet triplet = semanticHelper.getTriplet(data1.tripletOperand, data3.tripletOperand, compLexeme, listOfTriplets, compCompatibilityTable);

            if (triplet.getType() == null)
            {
                compiler.reportSemanticError("No se pueden comparar tipos de datos", ((YACCDataUnit)$2.obj).tokensData.get(0).getLocation());
                break;
            }

            int tripletID = listOfTriplets.addTriplet(triplet);

            YACCDataUnit data = new YACCDataUnit();
            data.firstTriplet = tripletID;
            data.tripletQuantity = 1 + data1.tripletQuantity + data3.tripletQuantity;

            $$ = new ParserVal(data);
        }
    ;

tipo_basico
    : LONG
    | UINT
    | DOUBLE
    | STRING
    ;

lista_identificadores
    : lista_identificadores ';' ID
        {
            ((LinkedList<LocatedSymbolTableEntry>)($1.obj)).add((LocatedSymbolTableEntry)$3.obj);
        }
    | ID
        {
            LinkedList<LocatedSymbolTableEntry> lista = new LinkedList<>();
            lista.add((LocatedSymbolTableEntry)$1.obj);
            $$ = new ParserVal(lista);
        }
    ;

acceso_atributo
    : acceso_atributo '.' ID
        {
            ((LinkedList<LocatedSymbolTableEntry>)($1.obj)).add((LocatedSymbolTableEntry)$3.obj);
        }
    | ID '.' ID
        {
            LinkedList<LocatedSymbolTableEntry> lista = new LinkedList<>();
            lista.add((LocatedSymbolTableEntry)$1.obj);
            lista.add((LocatedSymbolTableEntry)$3.obj);
            $$ = new ParserVal(lista);
        }
    ;

declaracion_variable
    : tipo_basico lista_identificadores ','
        {
            semanticHelper.declarePrimitiveList($2, getCurrentScopeStr(), getSTEntry($1));

            YACCDataUnit data = new YACCDataUnit();
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);
            $$ = new ParserVal(data);
        }
    | ID lista_identificadores ','
        {
            semanticHelper.declareObjectList($2, getCurrentScopeStr(), (LocatedSymbolTableEntry)$1.obj);

            YACCDataUnit data = new YACCDataUnit();
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);
            $$ = new ParserVal(data);
        }
    ;

sentencia_declarativa
    : declaracion_variable
        {
            YACCDataUnit data1 = (YACCDataUnit)$1.obj;

            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Declaración de variable/s", data1.tokensData.get(0).getLocation())
            );
        }
    | definicion_funcion ','
        {
            YACCDataUnit data1 = (YACCDataUnit)$1.obj;

            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Definición de función", data1.tokensData.get(0).getLocation())
            );
        }
    | definicion_clase ','
        {
            YACCDataUnit data1 = (YACCDataUnit)$1.obj;

            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Definición de clase", data1.tokensData.get(0).getLocation())
            );
        }
    | implementacion ','
        {
            YACCDataUnit data1 = (YACCDataUnit)$1.obj;

            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Sentencia IMPL", data1.tokensData.get(0).getLocation())
            );
        }
    ;

sentencia_ejecutable
    : ID op_asignacion_aumentada expr ','
        {
            YACCDataUnit data3 = (YACCDataUnit)$3.obj;

            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Asignación a variable", getTokenLocation($1))
            );

            String varLexeme = getSTEntry($1).getLexeme();
            SymbolTableEntry varEntry = semanticHelper.getEntryByScope(varLexeme, getCurrentScopeStr());

            if (varEntry == null)
            {
                compiler.reportSemanticError("Variable no encontrada: " + varLexeme, getTokenLocation($1));
                break;
            }

            if (varEntry.getAttrib(AttribKey.ID_TYPE) != IDType.VAR_ATTRIB)
            {
                compiler.reportSemanticError(String.format("El identificador '%s' no es una variable", varLexeme), getTokenLocation($1));
                break;
            }

            // HELP NICO DEL FUTURO el -= hay que tenerlo en cuenta (el facu del presente no sabe por que)

            DataType leftDataType = (DataType)varEntry.getAttrib(AttribKey.DATA_TYPE);

            TripletOperand rightTripletOperand = data3.tripletOperand;

            DataType rightDataType;

            if (rightTripletOperand.isFinal())
                rightDataType = (DataType)(rightTripletOperand.getstEntry().getAttrib(AttribKey.DATA_TYPE));
            else
                rightDataType = listOfTriplets.getTriplet(rightTripletOperand.getIndex()).getType();

            if (leftDataType != rightDataType) {
                compiler.reportSemanticError("asignacion de datos invalida diferentes tipos de datos ", getTokenLocation($2));
                break;
            }

            TripletOperand leftTripletOperand = new TripletOperand(varEntry);

            Triplet t = new Triplet("=", leftTripletOperand, rightTripletOperand, rightDataType);

            int tripletID = listOfTriplets.addTriplet(t);

            YACCDataUnit data = new YACCDataUnit();
            data.firstTriplet = tripletID;
            data.tripletQuantity = 1 + data3.tripletQuantity;

            $$ = new ParserVal(data);
        }
    | acceso_atributo op_asignacion_aumentada expr ','
        {
            YACCDataUnit data3 = (YACCDataUnit)$3.obj;

            LinkedList<LocatedSymbolTableEntry> tokenListData = (LinkedList<LocatedSymbolTableEntry>)($1.obj);

            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Asignación a atributo", tokenListData.getFirst().getLocation())
            );

            LocatedSymbolTableEntry[] tokenListArray = tokenListData.toArray(new LocatedSymbolTableEntry[tokenListData.size()]);

            // La primera debe ser una variable

            String varLexeme = tokenListData.getFirst().getSTEntry().getLexeme();
            String varEntryKey = semanticHelper.getEntryKeyByScope(varLexeme, getCurrentScopeStr());
            SymbolTableEntry varEntry = symbolTable.getEntry(varEntryKey);

            if (varEntry == null)
            {
                compiler.reportSemanticError("Variable no encontrada: " + varLexeme, tokenListData.getFirst().getLocation());
                break;
            }

            if (varEntry.getAttrib(AttribKey.ID_TYPE) != IDType.VAR_ATTRIB)
            {
                compiler.reportSemanticError(String.format("El identificador '%s' no es una variable", varLexeme), tokenListData.getFirst().getLocation());
                break;
            }

            if (varEntry.getAttrib(AttribKey.DATA_TYPE) != DataType.OBJECT)
            {
                compiler.reportSemanticError(String.format("La variable '%s' no es de tipo objeto", varLexeme), getTokenLocation($1));
                break;
            }

            String lastClassEntryKey = (String)varEntry.getAttrib(AttribKey.INSTANCE_OF);
            String lastClassLexeme = symbolTable.getEntry(lastClassEntryKey).getLexeme();

            // Chequar para el resto de la cadena de atributos, si estan definidos en la clase, su tipo, etc

            for (int i = 1; i < tokenListArray.length; i++)
            {
                boolean lastAttrib = i + 1 == tokenListArray.length;

                String attribLexeme = tokenListArray[i].getSTEntry().getLexeme();
                String attribScope = semanticHelper.invertScope(lastClassEntryKey);

                // Existe en la clase ???

                SymbolTableEntry attribEntry = symbolTable.getEntry(attribLexeme + ":" + attribScope);

                if (attribEntry == null)
                {
                    compiler.reportSemanticError(String.format("El atributo '%s' no esta definido para la clase '%s'", attribLexeme, lastClassLexeme), tokenListArray[i].getLocation());
                    break;
                }

                if (attribEntry.getAttrib(AttribKey.ID_TYPE) != IDType.VAR_ATTRIB)
                {
                    compiler.reportSemanticError(String.format("El identificador '%s' no es un atributo", attribLexeme), tokenListArray[i].getLocation());
                    break;
                }

                if (!lastAttrib && attribEntry.getAttrib(AttribKey.DATA_TYPE) != DataType.OBJECT)
                {
                    compiler.reportSemanticError(String.format("El atributo '%s' no es de tipo objeto", attribLexeme), tokenListArray[i].getLocation());
                    break;
                }

                if (!lastAttrib)
                {
                    lastClassEntryKey = (String)attribEntry.getAttrib(AttribKey.INSTANCE_OF);
                    lastClassLexeme = symbolTable.getEntry(lastClassEntryKey).getLexeme();
                }

            }

            YACCDataUnit data = new YACCDataUnit();
            data.tripletQuantity = 1 + data3.tripletQuantity;

            // NECESARIO AGREGAR First Triplet

            $$ = new ParserVal(data);
        }
    | ID '.' invocacion_funcion ','
        {
            YACCDataUnit data3 = (YACCDataUnit)$3.obj;

            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Invocación a método", getTokenLocation($1))
            );

            String varLexeme = getSTEntry($1).getLexeme();
            String methodLexeme = getSTEntry($3).getLexeme();

            String varEntryKey = semanticHelper.getEntryKeyByScope(varLexeme, getCurrentScopeStr());

            if (varEntryKey == null)
            {
                compiler.reportSemanticError("Variable no encontrada: " + varLexeme, getTokenLocation($1));
                break;
            }

            SymbolTableEntry varEntry = symbolTable.getEntry(varEntryKey);

            if (varEntry.getAttrib(AttribKey.ID_TYPE) != IDType.VAR_ATTRIB)
            {
                compiler.reportSemanticError(String.format("El identificador '%s' no es una variable", varLexeme), getTokenLocation($1));
                break;
            }

            if (varEntry.getAttrib(AttribKey.DATA_TYPE) != DataType.OBJECT)
            {
                compiler.reportSemanticError(String.format("La variable '%s' no es de tipo objeto", varLexeme), getTokenLocation($1));
                break;
            }

            String classEntryKey = (String)varEntry.getAttrib(AttribKey.INSTANCE_OF);
            String classLexeme = symbolTable.getEntry(classEntryKey).getLexeme();
            String methodEntryKey = methodLexeme + ":" + semanticHelper.removeLexemeFromKey(classEntryKey) + ":" + classLexeme;
            SymbolTableEntry methodEntry = symbolTable.getEntry(methodEntryKey);

            if (methodEntry == null)
            {
                compiler.reportSemanticError(String.format("El método '%s' no está definido para la clase '%s'", methodLexeme, classLexeme), data3.tokensData.get(0).getLocation());
                break;
            }

            if (methodEntry.getAttrib(AttribKey.ID_TYPE) != IDType.FUNC_METHOD)
            {
                compiler.reportSemanticError(String.format("El identificador '%s' no es un método en la clase '%s'", methodLexeme, classLexeme), data3.tokensData.get(0).getLocation());
                break;
            }

            YACCDataUnit data = new YACCDataUnit();

            $$ = new ParserVal(data);
        }
    | acceso_atributo '.' invocacion_funcion ','
        {
            YACCDataUnit data3 = (YACCDataUnit)$3.obj;

            LinkedList<LocatedSymbolTableEntry> tokenListData = (LinkedList<LocatedSymbolTableEntry>)($1.obj);

            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Invocación a método", tokenListData.getFirst().getLocation())
            );

            LocatedSymbolTableEntry[] tokenListArray = tokenListData.toArray(new LocatedSymbolTableEntry[tokenListData.size()]);

            // La primera debe ser una variable

            String varLexeme = tokenListData.getFirst().getSTEntry().getLexeme();
            String varEntryKey = semanticHelper.getEntryKeyByScope(varLexeme, getCurrentScopeStr());
            SymbolTableEntry varEntry = symbolTable.getEntry(varEntryKey);

            if (varEntry == null)
            {
                compiler.reportSemanticError("Variable no encontrada: " + varLexeme, tokenListData.getFirst().getLocation());
                break;
            }

            if (varEntry.getAttrib(AttribKey.ID_TYPE) != IDType.VAR_ATTRIB)
            {
                compiler.reportSemanticError(String.format("El identificador '%s' no es una variable", varLexeme), tokenListData.getFirst().getLocation());
                break;
            }

            if (varEntry.getAttrib(AttribKey.DATA_TYPE) != DataType.OBJECT)
            {
                compiler.reportSemanticError(String.format("La variable '%s' no es de tipo objeto", varLexeme), getTokenLocation($1));
                break;
            }

            String lastClassEntryKey = (String)varEntry.getAttrib(AttribKey.INSTANCE_OF);
            String lastClassLexeme = symbolTable.getEntry(lastClassEntryKey).getLexeme();

            // Todos los atributos deben ser objetos

            for (int i = 1; i < tokenListArray.length; i++)
            {

                String attribLexeme = tokenListArray[i].getSTEntry().getLexeme();
                String attribScope = semanticHelper.invertScope(lastClassEntryKey);

                SymbolTableEntry attribEntry = symbolTable.getEntry(attribLexeme + ":" + attribScope);

                if (attribEntry == null)
                {
                    compiler.reportSemanticError(String.format("El atributo '%s' no esta definido para la clase '%s'", attribLexeme, lastClassLexeme), tokenListArray[i].getLocation());
                    break;
                }

                if (attribEntry.getAttrib(AttribKey.ID_TYPE) != IDType.VAR_ATTRIB)
                {
                    compiler.reportSemanticError(String.format("El identificador '%s' no es un atributo", attribLexeme), tokenListArray[i].getLocation());
                    break;
                }

                if (attribEntry.getAttrib(AttribKey.DATA_TYPE) != DataType.OBJECT)
                {
                    compiler.reportSemanticError(String.format("El atributo '%s' no es de tipo objeto", attribLexeme), tokenListArray[i].getLocation());
                    break;
                }

                lastClassEntryKey = (String)attribEntry.getAttrib(AttribKey.INSTANCE_OF);
                lastClassLexeme = symbolTable.getEntry(lastClassEntryKey).getLexeme();
            }

            // Chequear que el método esté en la clase

            String methodLexeme = getSTEntry($3).getLexeme();
            String methodEntryKey = methodLexeme + ":" + semanticHelper.invertScope(lastClassEntryKey);
            SymbolTableEntry methodEntry = symbolTable.getEntry(methodEntryKey);

            if (methodEntry == null)
            {
                compiler.reportSemanticError(String.format("No se encuentra el método '%s' en la clase '%s'", methodLexeme, lastClassLexeme), data3.tokensData.get(0).getLocation());
                break;
            }

            if (methodEntry.getAttrib(AttribKey.ID_TYPE) != IDType.FUNC_METHOD)
            {
                compiler.reportSemanticError(String.format("'%s' no es ejecutable ya que no es un método", methodLexeme), data3.tokensData.get(0).getLocation());
                break;
            }

            // Chequear que coincidan los parámetros

            YACCDataUnit data = new YACCDataUnit();

            $$ = new ParserVal(data);

        }
    | invocacion_funcion ','
        {
            YACCDataUnit data1 = (YACCDataUnit)$1.obj;

            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Invocación a función", data1.tokensData.get(0).getLocation())
            );

            String functionLexeme = data1.tokensData.get(0).getSTEntry().getLexeme();
            SymbolTableEntry functionEntry = semanticHelper.getEntryByScope(functionLexeme, getCurrentScopeStr());

            if (functionEntry == null)
            {
                compiler.reportSemanticError(String.format("La función '%s' no es alcanzable", functionLexeme), data1.tokensData.get(0).getLocation());
                break;
            }

            YACCDataUnit data = new YACCDataUnit();
            data.tripletQuantity = 0;

            $$ = new ParserVal(data);
        }
    | sentencia_if ','
        {
            YACCDataUnit data1 = (YACCDataUnit)$1.obj;

            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Sentencia IF", data1.tokensData.get(0).getLocation())
            );
        }
    | do_until ','
        {
            YACCDataUnit data1 = (YACCDataUnit)$1.obj;

            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Estructura DO UNTIL", data1.tokensData.get(0).getLocation())
            );
        }
    | PRINT imprimible ','
        {
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Sentencia PRINT", getTokenLocation($1))
            );

            Triplet triplet = new Triplet("PRINT", new TripletOperand(getSTEntry($2)), null);
            int tripletID = listOfTriplets.addTriplet(triplet);

            YACCDataUnit data = new YACCDataUnit();
            data.firstTriplet = tripletID;
            data.tripletQuantity = 1;

            $$ = new ParserVal(data);
        }
    | RETURN ','
        {
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Sentencia RETURN", getTokenLocation($1))
            );

            $$ = new ParserVal(new YACCDataUnit());
        }
    ;

imprimible
    : CTE_STRING
    | CTE_UINT
    | CTE_LONG
    ;

lista_sentencias
    : lista_sentencias sentencia_ejecutable
    | lista_sentencias sentencia_declarativa
    | sentencia_ejecutable
    | sentencia_declarativa
    ;

lista_sentencias_ejecutables
    : lista_sentencias_ejecutables sentencia_ejecutable
        {
            YACCDataUnit data1 = (YACCDataUnit)$1.obj;

            YACCDataUnit data = new YACCDataUnit();
            data.tripletQuantity = ((YACCDataUnit)$1.obj).tripletQuantity + ((YACCDataUnit)$2.obj).tripletQuantity;
            data.firstTriplet = data1.firstTriplet;

            $$ = new ParserVal(data);
        }
    | sentencia_ejecutable
    ;

invocacion_funcion
    : ID '(' ')'
        {
            // Chequar que coincidan los argumentos

            YACCDataUnit data = new YACCDataUnit();
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);
            $$ = new ParserVal(data);
        }
    | ID '(' expr ')'
        {
            // Chequar que coincidan los argumentos

            YACCDataUnit data = new YACCDataUnit();
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);
            $$ = new ParserVal(data);
        }
    | ID '(' error ')'
        {
            compiler.reportSyntaxError("Error en invocacion a metodo", getTokenLocation($1));

            YACCDataUnit data = new YACCDataUnit();
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);
            $$ = new ParserVal(data);
        }
    ;

op_asignacion_aumentada
    : '='
    | SUB_ASIGN
    ;

condicion_if_reserva
    : condicion
        {
            // Agrega el terceto del jump condicional

            Triplet triplet = new Triplet("JZ", null, null);
            int tripletID = listOfTriplets.addTriplet(triplet);

            YACCDataUnit data = (YACCDataUnit)$1.obj;
            data.reservedTriplet = tripletID;

            $$ = new ParserVal(data);
        }
    ;

cuerpo_if
    : sentencia_ejecutable
    | '{' lista_sentencias_ejecutables '}'
        {

            $$ = $2;
        }
    ;

cuerpo_else
    : sentencia_ejecutable
    | '{' lista_sentencias_ejecutables '}'
        {
            $$ = $2;
        }
    ;

cuerpo_if_reserva
    : cuerpo_if
        {
            YACCDataUnit data = (YACCDataUnit)$1.obj;
            // Agrega el terceto del jump incondicional

            Triplet triplet = new Triplet("JMP", null, null);
            int tripletID = listOfTriplets.addTriplet(triplet);

            data.tripletQuantity += 1;
            data.reservedTriplet = tripletID;

            $$ = new ParserVal(data);
        }
    ;

sentencia_if
    : IF '(' condicion_if_reserva ')' cuerpo_if END_IF
        {
            YACCDataUnit data3 = (YACCDataUnit)$3.obj;
            YACCDataUnit data5 = (YACCDataUnit)$5.obj;

            int jzToBackpatch = data3.reservedTriplet;

            listOfTriplets.replaceTriplet(
                jzToBackpatch,
                new Triplet(
                    "JZ",
                    new TripletOperand(1 + jzToBackpatch + data5.tripletQuantity),
                    null
                )
            );

            YACCDataUnit data = new YACCDataUnit();
            data.tripletQuantity = 1 + data3.tripletQuantity + data5.tripletQuantity;
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);

            $$ = new ParserVal(data);
        }
    | IF '(' condicion_if_reserva ')' cuerpo_if_reserva ELSE cuerpo_else END_IF
        {
            YACCDataUnit data3 = (YACCDataUnit)$3.obj;
            YACCDataUnit data5 = (YACCDataUnit)$5.obj;
            YACCDataUnit data7 = (YACCDataUnit)$7.obj;

            int jzToBackpatch = data3.reservedTriplet;

            listOfTriplets.replaceTriplet(
                jzToBackpatch,
                new Triplet(
                    "JZ",
                    new TripletOperand(1 + jzToBackpatch + data5.tripletQuantity),
                    null
                )
            );

            int jmpToBackpatch = data5.reservedTriplet;

            listOfTriplets.replaceTriplet(
                jmpToBackpatch,
                new Triplet(
                    "JMP",
                    new TripletOperand(1 + jmpToBackpatch + data7.tripletQuantity),
                    null
                )
            );

            YACCDataUnit data = new YACCDataUnit();
            data.tripletQuantity = 2 + data3.tripletQuantity + data5.tripletQuantity + data7.tripletQuantity;
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);

            $$ = new ParserVal(data);
        }
    | IF error END_IF ','
        {
            compiler.reportSyntaxError("Error en IF", getTokenLocation($1));

            YACCDataUnit data = new YACCDataUnit();
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);
            $$ = new ParserVal(data);
        }
    ;

constante
    : CTE_UINT
    | CTE_STRING
    | CTE_DOUBLE
    | CTE_LONG
        {
            if (!ConstantRange.isValidLONG(getSTEntry($1).getLexeme(), false))
                compiler.reportLexicalError("El rango de LONG es [-2147483648, 2147483647]", getTokenLocation($1));
        }
    | '-' CTE_LONG
        {
            getSTEntry($2).addNegativeSign();
        }
    | '-' CTE_DOUBLE
        {
            getSTEntry($2).addNegativeSign();
        }
    | '-' CTE_UINT
        {
            compiler.reportLexicalError("Las constantes tipo UINT no pueden ser negativas", getTokenLocation($1));
        }
    ;

expr
    : basic_expr
    | TOD '(' basic_expr ')'
        {
            YACCDataUnit data3 = (YACCDataUnit)$3.obj;
            data3.tripletQuantity++;
            $$ = new ParserVal(data3);
        }
    ;

basic_expr
    : expr '+' term
        {
            YACCDataUnit data1 = (YACCDataUnit)$1.obj;
            YACCDataUnit data3 = (YACCDataUnit)$3.obj;

            TripletOperand operand1 = data1.tripletOperand;
            TripletOperand operand2 = data3.tripletOperand;

            Triplet t = semanticHelper.getTriplet(operand1, operand2, "+", listOfTriplets, sumCompatibilityTable);

            int tripletIndex = listOfTriplets.addTriplet(t);

            YACCDataUnit data = new YACCDataUnit();
            data.tripletQuantity = 1 + data1.tripletQuantity + data3.tripletQuantity;
            data.tripletOperand = new TripletOperand(tripletIndex);

            $$ = new ParserVal(data);
        }
    | expr '-' term
        {
            YACCDataUnit data1 = (YACCDataUnit)$1.obj;
            YACCDataUnit data3 = (YACCDataUnit)$3.obj;

            TripletOperand operand1 = data1.tripletOperand;
            TripletOperand operand2 = data3.tripletOperand;

            Triplet t = semanticHelper.getTriplet(operand1, operand2, "-", listOfTriplets, sumCompatibilityTable);

            int tripletIndex = listOfTriplets.addTriplet(t);

            YACCDataUnit data = new YACCDataUnit();
            data.tripletQuantity = 1 + data1.tripletQuantity + data3.tripletQuantity;
            data.tripletOperand = new TripletOperand(tripletIndex);

            $$ = new ParserVal(data);
        }
    | term
    ;

term
    : term '*' factor
        {
            YACCDataUnit data1 = (YACCDataUnit)$1.obj;
            YACCDataUnit data3 = (YACCDataUnit)$3.obj;

            TripletOperand operand1 = data1.tripletOperand;
            TripletOperand operand2 = data3.tripletOperand;

            Triplet t = semanticHelper.getTriplet(operand1, operand2, "*", listOfTriplets, mulCompatibilityTable);

            int tripletIndex = listOfTriplets.addTriplet(t);

            YACCDataUnit data = new YACCDataUnit();
            data.tripletQuantity = 1 + data1.tripletQuantity;
            data.tripletOperand = new TripletOperand(tripletIndex);

            $$ = new ParserVal(data);
        }
    | term '/' factor
        {
            YACCDataUnit data1 = (YACCDataUnit)$1.obj;
            YACCDataUnit data3 = (YACCDataUnit)$3.obj;

            TripletOperand operand1 = data1.tripletOperand;
            TripletOperand operand2 = data3.tripletOperand;

            Triplet t = semanticHelper.getTriplet(operand1, operand2, "/", listOfTriplets, divCompatibilityTable);

            int tripletIndex = listOfTriplets.addTriplet(t);

            YACCDataUnit data = new YACCDataUnit();
            data.tripletQuantity = 1 + data1.tripletQuantity;
            data.tripletOperand = new TripletOperand(tripletIndex);

            $$ = new ParserVal(data);
        }
    | factor
    ;

factor
    : ID
        {
            // Chequear alcance y tipo de ID

            String varLexeme = getSTEntry($1).getLexeme();
            SymbolTableEntry varEntry = semanticHelper.getEntryByScope(varLexeme, getCurrentScopeStr());

            if (varEntry == null)
            {
                compiler.reportSemanticError("Variable no alcanzable", getTokenLocation($1));
                break;
            }

            if (varEntry.getAttrib(AttribKey.ID_TYPE) != IDType.VAR_ATTRIB)
            {
                compiler.reportSemanticError("El identificador " + varEntry.getLexeme() + "no es de tipo var/attribute", getTokenLocation($1));
                break;
            }

            YACCDataUnit data = new YACCDataUnit();
            data.tripletOperand = new TripletOperand(varEntry);
            $$ = new ParserVal(data);
        }
    | constante
        {
            YACCDataUnit data = new YACCDataUnit();
            data.tripletOperand = new TripletOperand(getSTEntry($1));
            $$ = new ParserVal(data);
        }
    ;

definicion_funcion
    : procedimiento
    ;

id_ambito
    : ID
        {
            setCurrentID(getSTEntry($1).getLexeme());
        }
    ;

abrir_scope
    :  '{'
        {
            addToCurrentScope(getCurrentID());
        }
    ;

cerrar_scope
    :  '}'
        {
            removeScope();
        }
    ;

procedimiento
    : VOID id_ambito '(' tipo_basico ID ')' abrir_scope lista_sentencias cerrar_scope
        {
            YACCDataUnit data8 = (YACCDataUnit)$8.obj;

            String funcLexeme = getSTEntry($2).getLexeme();

            if (semanticHelper.alreadyDeclaredInScope(funcLexeme, getCurrentScopeStr()))
            {
                compiler.reportSemanticError("Identificador ya declarado en el ámbito local", getTokenLocation($2));
                break;
            }

            semanticHelper.declareFunction(getCurrentScopeStr(), $2.obj, $4.obj);

            String scopeAdentro = getCurrentScopeStr() + ":" + getSTEntry($2).getLexeme();
            semanticHelper.declareArg(scopeAdentro, $5.obj, $4.obj);

            YACCDataUnit data = new YACCDataUnit();
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);
            data.tripletQuantity = data8.tripletQuantity;

            $$ = new ParserVal(data);
        }
    | VOID id_ambito '(' ')' abrir_scope lista_sentencias cerrar_scope
        {
            YACCDataUnit data6 = (YACCDataUnit)$6.obj;

            String funcLexeme = getSTEntry($2).getLexeme();

            if (semanticHelper.alreadyDeclaredInScope(funcLexeme, getCurrentScopeStr()))
            {
                compiler.reportSemanticError("Identificador ya declarado en el ámbito local", getTokenLocation($2));
                break;
            }

            semanticHelper.declareFunction(getCurrentScopeStr(), $2.obj);

            YACCDataUnit data = new YACCDataUnit();
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);
            data.tripletQuantity = data6.tripletQuantity;

            $$ = new ParserVal(data);
        }
    | VOID id_ambito '(' tipo_basico ID ')' abrir_scope cerrar_scope
        {
            String funcLexeme = getSTEntry($2).getLexeme();

            if (semanticHelper.alreadyDeclaredInScope(funcLexeme, getCurrentScopeStr()))
            {
                compiler.reportSemanticError("Identificador ya declarado en el ámbito local", getTokenLocation($2));
                break;
            }

            semanticHelper.declareFunction(getCurrentScopeStr(), $2.obj, $4.obj);

            YACCDataUnit data = new YACCDataUnit();
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);

            $$ = new ParserVal(data);
        }
    | VOID id_ambito '(' ')' abrir_scope cerrar_scope
        {
            String funcLexeme = getSTEntry($2).getLexeme();

            if (semanticHelper.alreadyDeclaredInScope(funcLexeme, getCurrentScopeStr()))
            {
                compiler.reportSemanticError("Identificador ya declarado en el ámbito local", getTokenLocation($2));
                break;
            }

            semanticHelper.declareFunction(getCurrentScopeStr(), $2.obj);

            YACCDataUnit data = new YACCDataUnit();
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);

            $$ = new ParserVal(data);
        }
    | VOID error '}'
        {
            compiler.reportSyntaxError("Error en funcion/metodo", getTokenLocation($1));

            YACCDataUnit data = new YACCDataUnit();
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);

            $$ = new ParserVal(data);
        }
    ;


cuerpo_do
    : sentencia_ejecutable
    | '{' lista_sentencias_ejecutables '}'
        {
            $$ = $2;
        }
    ;


do_until
    : DO cuerpo_do UNTIL '(' condicion ')'
        {
            YACCDataUnit data2 = (YACCDataUnit)$2.obj;
            YACCDataUnit data5 = (YACCDataUnit)$5.obj;

            // Agregar salto condicional

            int bodyStartTriplet = data2.firstTriplet;

            Triplet triplet = new Triplet("JNZ", new TripletOperand(bodyStartTriplet), null);
            int tripletID = listOfTriplets.addTriplet(triplet);

            YACCDataUnit data = new YACCDataUnit();
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);
            data.tripletQuantity = 1 + data2.tripletQuantity + data5.tripletQuantity;

            $$ = new ParserVal(data);
        }
    ;

metodo
    : procedimiento
    ;

definicion_clase
    : CLASS id_ambito abrir_scope cuerpo_clase cerrar_scope
        {
            semanticHelper.declareClass(getCurrentScopeStr(), (LocatedSymbolTableEntry)$2.obj);

            YACCDataUnit data = new YACCDataUnit();
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);
            $$ = new ParserVal(data);
        }
    | CLASS id_ambito abrir_scope cerrar_scope
        {
            semanticHelper.declareClass(getCurrentScopeStr(), (LocatedSymbolTableEntry)$2.obj);

            YACCDataUnit data = new YACCDataUnit();
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);
            $$ = new ParserVal(data);
        }
    ;

cuerpo_clase
    : cuerpo_clase declaracion_variable
        {
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Declaracion de atributo", getTokenLocation($2))
            );
        }
    | declaracion_variable
        {
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Declaracion de atributo", getTokenLocation($1))
            );
        }
    | cuerpo_clase metodo ','
        {
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Implementacion de metodo dentro de clase", getTokenLocation($2))
            );
        }
    | metodo ','
        {
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Implementacion de metodo dentro de clase", getTokenLocation($1))
            );
        }
    | cuerpo_clase ID ','
        {
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Herencia por composicion", getTokenLocation($2))
            );

            semanticHelper.declareComposition(getCurrentScopeStr(), $2.obj);
        }
    | ID ','
        {
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Herencia por composicion", getTokenLocation($1))
            );

            semanticHelper.declareComposition(getCurrentScopeStr(), $2.obj);
        }
    ;

implementacion_metodos
    : implementacion_metodos metodo ','
        {
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Método distribuido", getTokenLocation($2))
            );
        }
    | metodo ','
        {
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Método distribuido", getTokenLocation($1))
            );
        }
    ;

id_implementacion
    : ID
        {
            // Chequear alcance y tipo de ID

            String entryKey = semanticHelper.getEntryKeyByScope(getSTEntry($1).getLexeme(), getCurrentScopeStr());

            SymbolTableEntry referredSTEntry = (entryKey != null) ? symbolTable.getEntry(entryKey) : null;

            if (referredSTEntry == null)
            {
                compiler.reportSemanticError("No se encuentra la clase: " + getSTEntry($1).getLexeme(), getTokenLocation($1));
            }
            else
            {
                if (referredSTEntry.getAttrib(AttribKey.ID_TYPE) != IDType.CLASSNAME)
                    compiler.reportSemanticError("El identificador " + referredSTEntry.getLexeme() + "no es de tipo CLASE", getTokenLocation($1));
                else
                {
                    String referredClassScope = semanticHelper.removeLexemeFromKey(entryKey);

                    /*
                        El scope del metodo sera el scope de la clase + la clase en si misma
                    */

                    String methodScope = referredClassScope + ":" + getSTEntry($1).getLexeme();

                    this.implementationMethodScope = methodScope;
                }
            }
        }
    ;

implementacion_abrir_scope
    : '{'
        {
            // Guardar el current scope
            this.scopeCopy = (LinkedList<String>)(this._currentScope.clone());
            // Reemplazar por el scope de la clase referenciada
            this._currentScope = semanticHelper.scopeStrToList(this.implementationMethodScope);
        }
    ;

implementacion_cerrar_scope
    : '}'
        {
            // Recuperar el scope antes del IMPL
            this._currentScope = this.scopeCopy;
        }
    ;

implementacion
    : IMPL FOR id_implementacion ':' implementacion_abrir_scope implementacion_metodos implementacion_cerrar_scope
        {
            YACCDataUnit data = new YACCDataUnit();
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);
            $$ = new ParserVal(data);
        }
    | IMPL error '}'
        {
            compiler.reportSyntaxError("Error en implementación distribuida", getTokenLocation($1));
            YACCDataUnit data = new YACCDataUnit();
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);
            $$ = new ParserVal(data);
        }
    ;

%%

void yyerror(String msg)
{
    System.out.println("ERROR! " + msg);
}

int yylex()
{
    return compiler.yylex();
}

public void setyylval(LocatedSymbolTableEntry tokensData)
{
    this.yylval = new ParserVal(tokensData);
}

public SymbolTableEntry getSTEntry(ParserVal o)
{
    return ((LocatedSymbolTableEntry)o.obj).getSTEntry();
}

public TokenLocation getTokenLocation(ParserVal o)
{
    return ((LocatedSymbolTableEntry)o.obj).getLocation();
}

private void addToCurrentScope(String s)
{
    _currentScope.add(s);
}

private void removeScope()
{
    _currentScope.removeLast();
}

private String getCurrentScopeStr()
{
    String scope = "global";

    for (String subscope : _currentScope)
        scope += ":" + subscope;

    return scope;
}

private String getCurrentID()
{
    return _currentID;
}

private void setCurrentID(String id)
{
    _currentID = id;
}

public ListOfTriplets getListOfTriplets()
{
    return listOfTriplets;
}

String _currentID;
LinkedList<String> _currentScope;
Compiler compiler;
SemanticHelper semanticHelper;
SymbolTable symbolTable;
String implementationMethodScope;
LinkedList<String> scopeCopy;
ListOfTriplets listOfTriplets;
SumCompatibilityTable sumCompatibilityTable;
MulCompatibilityTable mulCompatibilityTable;
DivCompatibilityTable divCompatibilityTable;
CompCompatibilityTable compCompatibilityTable;

public Parser(Compiler compiler)
{
    this.compiler = compiler;
    this._currentID = "";
    this._currentScope = new LinkedList<>();
    this.semanticHelper = new SemanticHelper(compiler);
    this.symbolTable = compiler.getSymbolTable();
    this.listOfTriplets = new ListOfTriplets();
    this.sumCompatibilityTable = new SumCompatibilityTable();
    this.mulCompatibilityTable = new MulCompatibilityTable();
    this.divCompatibilityTable = new DivCompatibilityTable();
    this.compCompatibilityTable = new CompCompatibilityTable();
    yydebug = false;
}