%{

import java.util.Vector;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Arrays;

import compiler.semantic.*;
import compiler.CompatibilityTable.*;

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

acceso_memoria
    : acceso_memoria '.' ID
        {
            YACCDataUnit data1 = (YACCDataUnit)$1.obj;

            if (!data1.isValid())
            {
                $$ = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            // Chequear que el ultimo elemento sea objeto

            String lastTokenLexeme = data1.tokensData.get(data1.tokensData.size() - 1).getSTEntry().getLexeme();
            TokenLocation lastTokenLocation = data1.tokensData.get(data1.tokensData.size() - 1).getLocation();

            SymbolTableEntry lastTokenEntry = symbolTable.getEntry(data1.referencedEntryKey);

            if (lastTokenEntry == null)
            {
                compiler.reportSemanticError(
                    "No se encuentra la clave: " + data1.referencedEntryKey, lastTokenLocation
                );

                $$ = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            if (lastTokenEntry.getAttrib(AttribKey.DATA_TYPE) != DataType.OBJECT)
            {
                compiler.reportSemanticError(
                    String.format("ID no es de tipo objeto: %s", lastTokenEntry.getLexeme()),
                    lastTokenLocation
                );

                $$ = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            // Chequar que la clase tenga la propiedad

            String classEntryKey = (String)lastTokenEntry.getAttrib(AttribKey.INSTANCE_OF);

            String staticVarEntryKey = getSTEntry($3).getLexeme() + ":" + semanticHelper.invertScope(classEntryKey);

            SymbolTableEntry staticVarEntry = symbolTable.getEntry(staticVarEntryKey);

            if (staticVarEntry == null)
            {
                compiler.reportSemanticError(
                    String.format("No se encuentra el ID: %s dentro de la clase: %s", getSTEntry($3).getLexeme(), symbolTable.getEntry(classEntryKey).getLexeme()),
                    getTokenLocation($3)
                );

                $$ = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            // Si es de tipo funcion reemplazar el lexema con el nombre de la clase

            if (staticVarEntry.getAttrib(AttribKey.ID_TYPE) == IDType.FUNC_METHOD)
            {
                // Reemplazar la referencia por el método en la clase

                data1.referencedEntryKey = staticVarEntryKey;
            }
            else
            {
                // Buscar la referencia a la variable en si

                String propertyEntryKey = getSTEntry($3).getLexeme() + ":" + semanticHelper.invertScope(data1.referencedEntryKey);
                data1.referencedEntryKey = propertyEntryKey;
            }
            data1.tokensData.add((LocatedSymbolTableEntry)$3.obj);
            $$ = new ParserVal(data1);
        }
    | ID
        {
            // Chequear que exista en el ambito local

            LocatedSymbolTableEntry tokenData = (LocatedSymbolTableEntry)$1.obj;
            String lexeme = tokenData.getSTEntry().getLexeme();

            String referencedEntryKey = semanticHelper.getEntryKeyByScope(lexeme, getCurrentScopeStr());

            if (referencedEntryKey == null)
            {
                compiler.reportSemanticError(
                    String.format("ID no encontrado: %s", lexeme),
                    getTokenLocation($1)
                );

                $$ = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            // SymbolTableEntry referencedEntry = symbolTable.getEntry(referencedEntryKey);

            YACCDataUnit data = new YACCDataUnit();
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);
            data.referencedEntryKey = referencedEntryKey;

            $$ = new ParserVal(data);
        }
    ;

tipo_de_dato
    : tipo_basico
        {
            YACCDataUnit data = new YACCDataUnit();
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);
            $$ = new ParserVal(data);
        }
    | ID
        {
            List reservedDataTypeWords = Arrays.asList("uint", "double", "long", "string");

            if (reservedDataTypeWords.contains(getSTEntry($1).getLexeme().toLowerCase()))
            {
                compiler.reportSemanticError("Las palabras reservadas van en mayusculas", getTokenLocation($1));
                YACCDataUnit data = new YACCInvalidDataUnit();
                data.tokensData.add((LocatedSymbolTableEntry)$1.obj);
                $$ = new ParserVal(data);
                break;
            }

            // Ver si es accesible y si es un token de clase

            String classEntryKey = semanticHelper.getEntryKeyByScope(getSTEntry($1).getLexeme(), getCurrentScopeStr());

            if (classEntryKey == null)
            {
                compiler.reportSemanticError("No se encuentra la clase: " + getSTEntry($1).getLexeme(), getTokenLocation($1));
                YACCDataUnit data = new YACCInvalidDataUnit();
                data.tokensData.add((LocatedSymbolTableEntry)$1.obj);
                $$ = new ParserVal(data);
                break;
            }

            SymbolTableEntry classEntry = symbolTable.getEntry(classEntryKey);

            if (classEntry.getAttrib(AttribKey.ID_TYPE) != IDType.CLASSNAME)
            {
                compiler.reportSemanticError("El ID: " + getSTEntry($1).getLexeme() + " no es una clase definida", getTokenLocation($1));
                YACCDataUnit data = new YACCInvalidDataUnit();
                data.tokensData.add((LocatedSymbolTableEntry)$1.obj);
                $$ = new ParserVal(data);
                break;
            }

            getSTEntry($1).setAttrib(AttribKey.DATA_TYPE, DataType.OBJECT);
            YACCDataUnit data = new YACCDataUnit();
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);
            $$ = new ParserVal(data);
        }
    ;


declaracion_variable
    : tipo_de_dato lista_identificadores ','
        {
            YACCDataUnit data1 = (YACCDataUnit)$1.obj;

            if (!data1.isValid())
            {
                $$ = new ParserVal(data1);
                break;
            }

            LinkedList<String> lexemeList = new LinkedList<>();

            for (LocatedSymbolTableEntry tokenData : (LinkedList<LocatedSymbolTableEntry>)($2.obj))
                lexemeList.add(tokenData.getSTEntry().getLexeme());

            // Si es una declaracion dentro de clase, agregarla a la lista

            if (this.currentClassEntryKey != null)
            {
                SymbolTableEntry classEntry = symbolTable.getEntry(currentClassEntryKey);
                HashSet<String> attribsSet = (HashSet<String>)(classEntry.getAttrib(AttribKey.ATTRIBS_SET));

                for (String lexeme : lexemeList)
                    attribsSet.add(lexeme + ":" + getCurrentScopeStr());
            }

            semanticHelper.declareRecursive(lexemeList, getCurrentScopeStr(), data1.tokensData.get(0).getSTEntry());

            YACCDataUnit data = new YACCDataUnit();

            data.tokensData.add(data1.tokensData.get(0));

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

argumentos_reales
    : '(' ')'
        {
            YACCDataUnit data = new YACCDataUnit();
            $$ = new ParserVal(data);
        }
    | '(' expr ')'
        {
            YACCDataUnit data2 = (YACCDataUnit)$2.obj;

            if (!data2.isValid())
            {
                $$ = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            YACCDataUnit data = new YACCDataUnit();
            data.tripletOperand = data2.tripletOperand;

            $$ = new ParserVal(data);
        }
    ;

sentencia_ejecutable
    : acceso_memoria op_asignacion_aumentada expr ','
        {
            // Chequar que data1 y data3 no tiren error

            YACCDataUnit data1 = (YACCDataUnit)$1.obj;
            YACCDataUnit data2 = (YACCDataUnit)$2.obj;
            YACCDataUnit data3 = (YACCDataUnit)$3.obj;

            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Asignacion", data2.tokensData.get(0).getLocation())
            );

            if (!data1.isValid() || !data2.isValid() || !data3.isValid())
            {
                $$ = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            String operation = data2.lexeme;

            String referencedEntryKey = data1.referencedEntryKey;
            SymbolTableEntry referencedEntry = symbolTable.getEntry(referencedEntryKey);

            // Chequar que sea una variable

            if (referencedEntry.getAttrib(AttribKey.ID_TYPE) != IDType.VAR_ATTRIB)
            {
                LocatedSymbolTableEntry lastAttrib = data1.tokensData.get(data1.tokensData.size() - 1);
                compiler.reportSemanticError(String.format(
                    "El ID: %s no es una variable/atributo",
                    referencedEntry.getLexeme()
                ), lastAttrib.getLocation());

                $$ = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            // Chequar tipos y demas

            DataType leftDataType = (DataType)referencedEntry.getAttrib(AttribKey.DATA_TYPE);
            TripletOperand rightTripletOperand = data3.tripletOperand;

            TripletOperand equalizeTo;

            int firstTriplet = -1;

            if (operation.equals("="))
            {
                equalizeTo = rightTripletOperand;
            }
            else
            {
                Triplet subTriplet = new Triplet("-", new TripletOperand(referencedEntry), rightTripletOperand);
                int tripletID = listOfTriplets.addTriplet(subTriplet);
                equalizeTo = new TripletOperand(tripletID);
                firstTriplet = tripletID;
            }

            Triplet equalTriplet = new Triplet("=", new TripletOperand(referencedEntry), equalizeTo);
            int tripletID = listOfTriplets.addTriplet(equalTriplet);

            if (firstTriplet == -1)
                firstTriplet = tripletID;

            YACCDataUnit data = new YACCDataUnit();
            data.firstTriplet = firstTriplet;
            data.tripletQuantity = (operation.equals("=") ? 1 : 2) + data3.tripletQuantity;

            $$ = new ParserVal(data);
        }
    | acceso_memoria argumentos_reales ','
        {
            YACCDataUnit data1 = (YACCDataUnit)$1.obj;
            YACCDataUnit data2 = (YACCDataUnit)$2.obj;

            if (!data1.isValid() || !data2.isValid())
            {
                $$ = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            String functionName = data1.getLastTokenData().getSTEntry().getLexeme();
            TokenLocation functionTokenLocation = data1.getLastTokenData().getLocation();

            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Invocación a método", functionTokenLocation)
            );

            SymbolTableEntry referencedEntry = symbolTable.getEntry(data1.referencedEntryKey);

            if (referencedEntry.getAttrib(AttribKey.ID_TYPE) != IDType.FUNC_METHOD)
            {
                compiler.reportSemanticError(String.format("El identificador '%s' no es ejecutable", functionName), functionTokenLocation);

                $$ = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            // Chequear el parametro

            boolean funcHasArgument = referencedEntry.getAttrib(AttribKey.ARG_TYPE) != null;
            boolean argumentWasPassed = data2.tripletOperand != null;

            if (funcHasArgument ^ argumentWasPassed)
            {
                compiler.reportSemanticError(String.format("La función '%s' espera: %d argumentos", functionName, funcHasArgument ? 1 : 0), functionTokenLocation);

                $$ = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            if (funcHasArgument)
            {
                // Chequear que coincidan los tipos

                DataType exprDataType = null;

                if (data2.tripletOperand.isFinal())
                    exprDataType = (DataType)data2.tripletOperand.stEntry.getAttrib(AttribKey.DATA_TYPE);
                else
                    exprDataType = listOfTriplets.getTriplet(data2.tripletOperand.index).getType();

                if (referencedEntry.getAttrib(AttribKey.ARG_TYPE) != exprDataType)
                {
                    compiler.reportSemanticError(String.format("El tipo de dato no coincide"), functionTokenLocation);

                    $$ = new ParserVal(new YACCInvalidDataUnit());
                    break;
                }
            }

            // Agregar terceto de invocacion

            Triplet invokeTriplet = new Triplet(
                "INVOKE",
                new TripletOperand(referencedEntry),
                data2.tripletOperand
            );

            int tripletID = listOfTriplets.addTriplet(invokeTriplet);

            YACCDataUnit data = new YACCDataUnit();

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

            Triplet t = new Triplet("RETURN", null, null);
            int pos = listOfTriplets.addTriplet(t);
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

op_asignacion_aumentada
    : '='
        {
            YACCDataUnit data = new YACCDataUnit();
            data.lexeme = "=";
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);
            $$ = new ParserVal(data);
        }
    | SUB_ASIGN
        {
            YACCDataUnit data = new YACCDataUnit();
            data.lexeme = "-=";
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);
            $$ = new ParserVal(data);
        }
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

            Triplet convTriplet = new Triplet("TOD", data3.tripletOperand, null, DataType.DOUBLE);
            int tripletID = listOfTriplets.addTriplet(convTriplet);

            data3.tripletOperand = new TripletOperand(tripletID);
            data3.dataType = DataType.DOUBLE;

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
    : acceso_memoria
        {
            YACCDataUnit data1 = (YACCDataUnit)$1.obj;

            if (!data1.isValid())
            {
                $$ = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            // Chequear que acceso_memoria sea una variable

            String referencedEntryKey = data1.referencedEntryKey;
            SymbolTableEntry referencedEntry = symbolTable.getEntry(referencedEntryKey);

            if (referencedEntry.getAttrib(AttribKey.ID_TYPE) != IDType.VAR_ATTRIB)
            {
                compiler.reportSemanticError("Se espera una variable", null);

                $$ = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            YACCDataUnit data = new YACCDataUnit();
            data.tripletOperand = new TripletOperand(referencedEntry);
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


procedimiento_args
    : '(' tipo_basico ID ')'
        {
            YACCDataUnit data = new YACCDataUnit();

            data.tokensData.add((LocatedSymbolTableEntry)$2.obj);
            data.tokensData.add((LocatedSymbolTableEntry)$3.obj);

            $$ = new ParserVal(data);
        }
    | '(' ')'
        {
            $$ = new ParserVal(new YACCDataUnit());
        }
    ;

procedimiento_cuerpo
    : abrir_scope cerrar_scope
        {
            $$ = new ParserVal(new YACCDataUnit());
        }
    | abrir_scope lista_sentencias cerrar_scope
        {
            $$ = $2;
        }
    ;

procedimiento
    : VOID id_ambito procedimiento_args procedimiento_cuerpo
        {
            YACCDataUnit data3 = (YACCDataUnit)$3.obj;
            YACCDataUnit data4 = (YACCDataUnit)$4.obj;

            String funcLexeme = getSTEntry($2).getLexeme();

            if (semanticHelper.alreadyDeclaredInScope(funcLexeme, getCurrentScopeStr()))
            {
                compiler.reportSemanticError("Identificador ya declarado en el ámbito local", getTokenLocation($2));
                $$ = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            boolean hasArgument = data3.tokensData.size() > 0;

            LocatedSymbolTableEntry argDataType = hasArgument ? data3.tokensData.get(0) : null;
            LocatedSymbolTableEntry argName = hasArgument ? data3.tokensData.get(1) : null;

            semanticHelper.declareFunction(getCurrentScopeStr(), $2.obj, argDataType);

            if (hasArgument)
            {
                // Declarar la variable del argumento en el scope del procedimiento

                String scopeAdentro = getCurrentScopeStr() + ":" + getSTEntry($2).getLexeme();
                semanticHelper.declareArg(scopeAdentro, argName, argDataType);
            }

            YACCDataUnit data = new YACCDataUnit();
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);
            data.tripletQuantity = data4.tripletQuantity;
            data.referencedEntryKey = funcLexeme + ":" + getCurrentScopeStr();

            $$ = new ParserVal(data);
        }
    | VOID error '}'
        {
            compiler.reportSyntaxError("Error en funcion/metodo", getTokenLocation($1));

            YACCDataUnit data = new YACCInvalidDataUnit();
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


clase_con_nombre
    : CLASS id_ambito
        {
            boolean success = semanticHelper.declareClass(getCurrentScopeStr(), (LocatedSymbolTableEntry)$2.obj);

            if (!success)
            {
                $$ = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            String className = getSTEntry($2).getLexeme();

            this.currentClassEntryKey = className + ":" + getCurrentScopeStr();

            YACCDataUnit data = new YACCDataUnit();
            data.referencedEntryKey = currentClassEntryKey;
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);
            data.tokensData.add((LocatedSymbolTableEntry)$2.obj);

            $$ = new ParserVal(data);
        }
    ;

definicion_clase
    : clase_con_nombre abrir_scope cuerpo_clase cerrar_scope
        {
            this.currentClassEntryKey = null;

            YACCDataUnit data1 = (YACCDataUnit)$1.obj;

            if (!data1.isValid())
            {
                $$ = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            $$ = new ParserVal(data1);
        }
    | clase_con_nombre abrir_scope cerrar_scope
        {
            this.currentClassEntryKey = null;

            YACCDataUnit data1 = (YACCDataUnit)$1.obj;

            if (!data1.isValid())
            {
                $$ = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            $$ = new ParserVal(data1);
        }
    ;

cuerpo_clase
    : cuerpo_clase declaracion_variable
        {
            YACCDataUnit data1 = (YACCDataUnit)$1.obj;

            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Declaracion de atributo", data1.tokensData.get(0).getLocation())
            );
        }
    | declaracion_variable
        {
            YACCDataUnit data1 = (YACCDataUnit)$1.obj;

            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Declaracion de atributo", data1.tokensData.get(0).getLocation())
            );
        }
    | cuerpo_clase metodo ','
        {
            YACCDataUnit data2 = (YACCDataUnit)$2.obj;

            if (!data2.isValid())
            {
                $$ = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            SymbolTableEntry currentClass = symbolTable.getEntry(currentClassEntryKey);

            Set<String> methodsSet = (Set<String>)(currentClass.getAttrib(AttribKey.METHODS_SET));

            methodsSet.add(data2.referencedEntryKey);

            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Implementacion de metodo dentro de clase", data2.tokensData.get(0).getLocation())
            );
        }
    | metodo ','
        {
            YACCDataUnit data1 = (YACCDataUnit)$1.obj;

            if (!data1.isValid())
            {
                $$ = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            SymbolTableEntry currentClass = symbolTable.getEntry(currentClassEntryKey);

            Set<String> methodsSet = (Set<String>)(currentClass.getAttrib(AttribKey.METHODS_SET));

            methodsSet.add(data1.referencedEntryKey);

            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Implementacion de metodo dentro de clase", data1.tokensData.get(0).getLocation())
            );
        }
    | cuerpo_clase ID ','
        {
            String composLexeme = getSTEntry($2).getLexeme();

            semanticHelper.declareCompos((LocatedSymbolTableEntry)$2.obj, getCurrentScopeStr(), currentClassEntryKey);

            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Herencia por composicion", getTokenLocation($2))
            );
        }
    | ID ','
        {
            String composLexeme = getSTEntry($1).getLexeme();

            semanticHelper.declareCompos((LocatedSymbolTableEntry)$1.obj, getCurrentScopeStr(), currentClassEntryKey);

            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Herencia por composicion", getTokenLocation($1))
            );
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
String currentClassEntryKey;

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
    this.currentClassEntryKey = null;
    yydebug = false;
}