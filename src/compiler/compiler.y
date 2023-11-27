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
                $$ = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            int tripletID = listOfTriplets.addTriplet(triplet);

            YACCDataUnit data = new YACCDataUnit();
            data.firstTriplet = tripletID;
            data.tripletQuantity = 1 + data1.tripletQuantity + data3.tripletQuantity;
            data.lexeme = compLexeme;
            data.dataType = triplet.getType();

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
                break;

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

            boolean addThisPush = (data1.tokensData.size() == 1) && currentClassEntryKey == null;

            if (addThisPush)
            {
                // Agregar terceto de push

                Triplet pushTriplet = new Triplet(
                    "THIS",
                    new TripletOperand(lastTokenEntry, listOfTriplets),
                    null
                );

                listOfTriplets.addTriplet(pushTriplet);
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

                YACCDataUnit data = new YACCInvalidDataUnit();
                data.tokensData.add((LocatedSymbolTableEntry)$1.obj);
                $$ = new ParserVal(data);
                break;
            }

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

            semanticHelper.declareRecursive(lexemeList, getCurrentScopeStr(), data1.tokensData.get(0).getSTEntry(), currentClassEntryKey);

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

            boolean areSameDataType = true;

            if (rightTripletOperand.isFinal())
            {
                if (leftDataType != rightTripletOperand.getstEntry().getAttrib(AttribKey.DATA_TYPE))
                    areSameDataType = false;
            }
            else
                if (leftDataType != listOfTriplets.getTriplet(rightTripletOperand.getIndex()).getType())
                    areSameDataType = false;


            if (!areSameDataType)
            {
                compiler.reportSemanticError("Los tipos de datos no son compatibles para la asignacion", data2.tokensData.get(0).getLocation());
                $$ = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            TripletOperand equalizeTo;

            int firstTriplet = -1;

            if (operation.equals("="))
            {
                equalizeTo = rightTripletOperand;
            }
            else
            {
                Triplet subTriplet = new Triplet("-", new TripletOperand(referencedEntry, listOfTriplets), rightTripletOperand);
                int tripletID = listOfTriplets.addTriplet(subTriplet);
                equalizeTo = new TripletOperand(tripletID, listOfTriplets);
                firstTriplet = tripletID;
            }

            Triplet equalTriplet = new Triplet("=", new TripletOperand(referencedEntry, listOfTriplets), equalizeTo);
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
                "CALL",
                new TripletOperand(referencedEntry, listOfTriplets),
                data2.tripletOperand
            );

            int tripletID = listOfTriplets.addTriplet(invokeTriplet);

            YACCDataUnit data = new YACCDataUnit();
            data.tripletQuantity = 1;
            
            if (data1.tokensData.size() > 1) data.tripletQuantity++;

            data.firstTriplet = tripletID;

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
            YACCDataUnit data2 = (YACCDataUnit)$2.obj;

            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Sentencia PRINT", getTokenLocation($1))
            );

            if (!data2.isValid())
            {
                $$ = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            // SymbolTableEntry entry = symbolTable.getEntry(data2.referencedEntryKey);

            Triplet triplet = new Triplet("PRINT", data2.tripletOperand, null);
            int tripletID = listOfTriplets.addTriplet(triplet);

            YACCDataUnit data = new YACCDataUnit();
            data.firstTriplet = tripletID;
            data.tripletQuantity = 1 + data2.tripletQuantity;
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);

            $$ = new ParserVal(data);
        }
    | PRINT ','
        {
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Sentencia PRINT", getTokenLocation($1))
            );

            Triplet triplet = new Triplet("PRINT", null, null);
            int tripletID = listOfTriplets.addTriplet(triplet);

            YACCDataUnit data = new YACCDataUnit();
            data.firstTriplet = tripletID;
            data.tripletQuantity = 1;
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);

            $$ = new ParserVal(data);
        }
    | RETURN ','
        {
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Sentencia RETURN", getTokenLocation($1))
            );

            Triplet t = new Triplet("RETURN", null, null);
            int tripletID = listOfTriplets.addTriplet(t);

            YACCDataUnit data = new YACCDataUnit();
            data.tripletQuantity = 1;
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);
            data.firstTriplet = tripletID;

            $$ = new ParserVal(data);
        }
    ;

imprimible
    : expr
    ;

lista_sentencias
    : lista_sentencias sentencia_ejecutable
        {
            YACCDataUnit data1 = (YACCDataUnit)$1.obj;
            YACCDataUnit data2 = (YACCDataUnit)$2.obj;

            YACCDataUnit data = new YACCDataUnit();

            data.tripletQuantity = data1.tripletQuantity + data2.tripletQuantity;
            data.firstTriplet = (data1.firstTriplet != null ? data1.firstTriplet : data2.firstTriplet);

            $$ = new ParserVal(data);
        }
    | lista_sentencias sentencia_declarativa
        {
            YACCDataUnit data1 = (YACCDataUnit)$1.obj;
            YACCDataUnit data2 = (YACCDataUnit)$2.obj;

            YACCDataUnit data = new YACCDataUnit();

            data.tripletQuantity = data1.tripletQuantity + data2.tripletQuantity;
            data.firstTriplet = (data1.firstTriplet != null ? data1.firstTriplet : data2.firstTriplet);

            $$ = new ParserVal(data);
        }
    | sentencia_ejecutable
    | sentencia_declarativa
    ;

lista_sentencias_ejecutables
    : lista_sentencias_ejecutables sentencia_ejecutable
        {
            YACCDataUnit data1 = (YACCDataUnit)$1.obj;
            YACCDataUnit data2 = (YACCDataUnit)$2.obj;

            YACCDataUnit data = new YACCDataUnit();
            data.tripletQuantity = data1.tripletQuantity + data2.tripletQuantity;
            data.firstTriplet = (data1.firstTriplet != null ? data1.firstTriplet : data2.firstTriplet);

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


            System.out.println("IF simple:");
            System.out.println("Dentro de condicion: " + data3.tripletQuantity);
            System.out.println("Dentro del cuerpo: " + data5.tripletQuantity);


            int jzToBackpatch = data3.reservedTriplet;

            String comp = data3.lexeme;

            int end_if = 1 + jzToBackpatch + data5.tripletQuantity;

            listOfTriplets.replaceTriplet(
                jzToBackpatch,
                new Triplet(
                    "NEG_CJUMP",
                    new TripletOperand(end_if, listOfTriplets),
                    null
                )
            );

            listOfTriplets.addTag(end_if, listOfTriplets.getNewIfTag());

            YACCDataUnit data = new YACCDataUnit();
            data.tripletQuantity = 1 + data3.tripletQuantity + data5.tripletQuantity;
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);
            data.firstTriplet = data3.reservedTriplet;

            $$ = new ParserVal(data);
        }
    | IF '(' condicion_if_reserva ')' cuerpo_if_reserva ELSE cuerpo_else END_IF
        {



            YACCDataUnit data3 = (YACCDataUnit)$3.obj;
            YACCDataUnit data5 = (YACCDataUnit)$5.obj;
            YACCDataUnit data7 = (YACCDataUnit)$7.obj;


            System.out.println("IF compuesto:");
            System.out.println("Dentro de condicion: " + data3.tripletQuantity);
            System.out.println("Dentro del cuerpo: " + data5.tripletQuantity);
            System.out.println("Dentro del else: " + data7.tripletQuantity);


            int jzToBackpatch = data3.reservedTriplet;

            String comp = data3.lexeme;

            int end_if = 1 + jzToBackpatch + data5.tripletQuantity;

            listOfTriplets.replaceTriplet(
                jzToBackpatch,
                new Triplet(
                    "NEG_CJUMP",
                    new TripletOperand(end_if, listOfTriplets),
                    null
                )
            );

            listOfTriplets.addTag(end_if, listOfTriplets.getNewIfTag());

            int jmpToBackpatch = data5.reservedTriplet;

            int end_if_else = 1 + jmpToBackpatch + data7.tripletQuantity;

            listOfTriplets.replaceTriplet(
                jmpToBackpatch,
                new Triplet(
                    "JMP",
                    new TripletOperand(end_if_else, listOfTriplets),
                    null
                )
            );

            listOfTriplets.addTag(end_if_else, listOfTriplets.getNewIfTag());

            YACCDataUnit data = new YACCDataUnit();
            data.tripletQuantity = 1 + data3.tripletQuantity + data5.tripletQuantity + data7.tripletQuantity;
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);
            data.firstTriplet = data3.reservedTriplet;

            $$ = new ParserVal(data);
        }
    | IF error END_IF ','
        {
            compiler.reportSyntaxError("Error en IF", getTokenLocation($1));

            YACCDataUnit data = new YACCInvalidDataUnit();
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);
            $$ = new ParserVal(data);
        }
    ;

constante
    : CTE_UINT
        {
            YACCDataUnit data = new YACCDataUnit();
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);
            $$ = new ParserVal(data);
        }
    | CTE_STRING
        {
            YACCDataUnit data = new YACCDataUnit();
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);
            $$ = new ParserVal(data);
        }
    | CTE_DOUBLE
        {
            YACCDataUnit data = new YACCDataUnit();
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);
            $$ = new ParserVal(data);
        }
    | CTE_LONG
        {
            if (!ConstantRange.isValidLONG(getSTEntry($1).getLexeme(), false))
            {
                compiler.reportLexicalError("El rango de LONG es [-2147483648, 2147483647]", getTokenLocation($1));
                $$ = new ParserVal(new YACCInvalidDataUnit());
                break;
            }
            YACCDataUnit data = new YACCDataUnit();
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);
            $$ = new ParserVal(data);
        }
    | '-' CTE_LONG
        {
            String lexeme = "-" + getSTEntry($2).getLexeme();
            SymbolTableEntry existsAlready = symbolTable.getEntry(lexeme);

            if (existsAlready != null)
            {
                YACCDataUnit data = new YACCDataUnit();
                data.tokensData.add(new LocatedSymbolTableEntry(existsAlready, getTokenLocation($1)));
                $$ = new ParserVal(data);
                break;
            }

            SymbolTableEntry newEntry = symbolTable.addNewEntry(
                new SymbolTableEntry(
                    Parser.CTE_LONG,
                    lexeme
                )
                .setAttrib(AttribKey.DATA_TYPE, DataType.LONG)
                .setAttrib(AttribKey.IS_CONSTANT, true)
                .setAttrib(AttribKey.MEMORY_ASSOCIATION, new MemoryAssociation(lexeme, DataType.LONG.getSize(), DataType.LONG))
            );

            YACCDataUnit data = new YACCDataUnit();
            data.tokensData.add(new LocatedSymbolTableEntry(newEntry, getTokenLocation($1)));
            $$ = new ParserVal(data);
        }
    | '-' CTE_DOUBLE
        {
            String lexeme = "-" + getSTEntry($2).getLexeme();
            SymbolTableEntry existsAlready = symbolTable.getEntry(lexeme);

            if (existsAlready != null)
            {
                YACCDataUnit data = new YACCDataUnit();
                data.tokensData.add(new LocatedSymbolTableEntry(existsAlready, getTokenLocation($1)));
                $$ = new ParserVal(data);
                break;
            }

            SymbolTableEntry newEntry = symbolTable.addNewEntry(
                new SymbolTableEntry(
                    Parser.CTE_DOUBLE,
                    lexeme
                )
                .setAttrib(AttribKey.DATA_TYPE, DataType.DOUBLE)
                .setAttrib(AttribKey.IS_CONSTANT, true)
                .setAttrib(AttribKey.MEMORY_ASSOCIATION, new MemoryAssociation(lexeme, DataType.DOUBLE.getSize(), DataType.DOUBLE))
            );

            YACCDataUnit data = new YACCDataUnit();
            data.tokensData.add(new LocatedSymbolTableEntry(newEntry, getTokenLocation($1)));
            $$ = new ParserVal(data);
        }
    | '-' CTE_UINT
        {
            compiler.reportLexicalError("Las constantes tipo UINT no pueden ser negativas", getTokenLocation($1));
            $$ = new ParserVal(new YACCInvalidDataUnit());
        }
    ;

expr
    : basic_expr
    | TOD '(' basic_expr ')'
        {
            YACCDataUnit data3 = (YACCDataUnit)$3.obj;

            if (!data3.isValid())
            {
                data3.setValid(false);
                $$ = new ParserVal(data3);
                break;
            }

            DataType exprDataType = getTripletOperandDataType(data3.tripletOperand);

            if (exprDataType == DataType.DOUBLE)
            {
                compiler.generateWarning("Se elimina TOD innecesario", getTokenLocation($1));
                $$ = new ParserVal(data3);
                break;
            }

            String tripletOP = null;

            switch (exprDataType)
            {
                case UINT:
                    tripletOP = "UITOD";
                    break;
                case LONG:
                    tripletOP = "LTOD";
                    break;
            }

            if (tripletOP == null)
            {
                compiler.reportSemanticError(
                    "No se puede convertir el tipo: " + exprDataType + " a DOUBLE",
                    getTokenLocation($2)
                );
                data3.setValid(false);
                $$ = new ParserVal(data3);
                break;
            }

            Triplet convTriplet = new Triplet(tripletOP, data3.tripletOperand, null, DataType.DOUBLE);
            convTriplet.setMemoryAssociation(new MemoryAssociation(symbolTable.createAuxVar(DataType.DOUBLE), DataType.DOUBLE.getSize(), DataType.DOUBLE));

            int tripletID = listOfTriplets.addTriplet(convTriplet);

            data3.tripletOperand = new TripletOperand(tripletID, listOfTriplets);
            data3.dataType = DataType.DOUBLE;
            data3.tripletQuantity++;

            $$ = new ParserVal(data3);
        }
    ;

basic_expr
    : expr '+' term
        {
            YACCDataUnit data1 = (YACCDataUnit)$1.obj;
            YACCDataUnit data3 = (YACCDataUnit)$3.obj;

            if (!data1.isValid() || !data3.isValid())
            {
                $$ = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            TripletOperand operand1 = data1.tripletOperand;
            TripletOperand operand2 = data3.tripletOperand;

            Triplet t = semanticHelper.getTriplet(operand1, operand2, "+", listOfTriplets, sumCompatibilityTable);

            if (t == null) {
                compiler.reportSemanticError("No se pueden sumar variables de distinto tipo", getTokenLocation($2));
                $$ = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            t.setMemoryAssociation(new MemoryAssociation(symbolTable.createAuxVar(t.getType()), t.getType().getSize(), t.getType()));

            int tripletIndex = listOfTriplets.addTriplet(t);

            YACCDataUnit data = new YACCDataUnit();
            data.tripletQuantity = 1 + data1.tripletQuantity + data3.tripletQuantity;
            data.tripletOperand = new TripletOperand(tripletIndex, listOfTriplets);

            $$ = new ParserVal(data);
        }
    | expr '-' term
        {
            YACCDataUnit data1 = (YACCDataUnit)$1.obj;
            YACCDataUnit data3 = (YACCDataUnit)$3.obj;

            if (!data1.isValid() || !data3.isValid())
            {
                $$ = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            TripletOperand operand1 = data1.tripletOperand;
            TripletOperand operand2 = data3.tripletOperand;

            Triplet t = semanticHelper.getTriplet(operand1, operand2, "-", listOfTriplets, sumCompatibilityTable);

            if (t == null) {
                compiler.reportSemanticError("No se pueden restar variables de distinto tipo", getTokenLocation($2));
                $$ = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            t.setMemoryAssociation(new MemoryAssociation(symbolTable.createAuxVar(t.getType()), t.getType().getSize(), t.getType()));

            int tripletIndex = listOfTriplets.addTriplet(t);

            YACCDataUnit data = new YACCDataUnit();
            data.tripletQuantity = 1 + data1.tripletQuantity + data3.tripletQuantity;
            data.tripletOperand = new TripletOperand(tripletIndex, listOfTriplets);

            $$ = new ParserVal(data);
        }
    | term
    ;

term
    : term '*' factor
        {
            YACCDataUnit data1 = (YACCDataUnit)$1.obj;
            YACCDataUnit data3 = (YACCDataUnit)$3.obj;

            if (!data1.isValid() || !data3.isValid())
            {
                $$ = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            TripletOperand operand1 = data1.tripletOperand;
            TripletOperand operand2 = data3.tripletOperand;

            Triplet t = semanticHelper.getTriplet(operand1, operand2, "*", listOfTriplets, mulCompatibilityTable);

            if (t == null) {
                compiler.reportSemanticError("No se pueden multiplicar variables de distinto tipo", getTokenLocation($2));
                $$ = new ParserVal(new YACCInvalidDataUnit());
                break;
            }


            t.setMemoryAssociation(new MemoryAssociation(symbolTable.createAuxVar(t.getType()), t.getType().getSize(), t.getType()));

            int tripletIndex = listOfTriplets.addTriplet(t);

            YACCDataUnit data = new YACCDataUnit();
            data.tripletQuantity = 1 + data1.tripletQuantity;
            data.tripletOperand = new TripletOperand(tripletIndex, listOfTriplets);

            $$ = new ParserVal(data);
        }
    | term '/' factor
        {
            YACCDataUnit data1 = (YACCDataUnit)$1.obj;
            YACCDataUnit data3 = (YACCDataUnit)$3.obj;

            if (!data1.isValid() || !data3.isValid())
            {
                $$ = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            TripletOperand operand1 = data1.tripletOperand;
            TripletOperand operand2 = data3.tripletOperand;

            Triplet t = semanticHelper.getTriplet(operand1, operand2, "/", listOfTriplets, divCompatibilityTable);

            if (t == null) {
                compiler.reportSemanticError("No se pueden dividir variables de distinto tipo", getTokenLocation($2));
                $$ = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            t.setMemoryAssociation(new MemoryAssociation(symbolTable.createAuxVar(t.getType()), t.getType().getSize(), t.getType()));

            int tripletIndex = listOfTriplets.addTriplet(t);

            YACCDataUnit data = new YACCDataUnit();
            data.tripletQuantity = 1 + data1.tripletQuantity;
            data.tripletOperand = new TripletOperand(tripletIndex, listOfTriplets);

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
            data.tripletOperand = new TripletOperand(referencedEntry, listOfTriplets);
            $$ = new ParserVal(data);
        }
    | constante
        {
            YACCDataUnit data1 = (YACCDataUnit)$1.obj;

            if (!data1.isValid())
            {
                $$ = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            YACCDataUnit data = new YACCDataUnit();
            data.tripletOperand = new TripletOperand(data1.tokensData.get(0).getSTEntry(), listOfTriplets);
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

void_con_reserva
    : VOID
        {
            Triplet t = new Triplet(null, null, null);
            int tripletID = listOfTriplets.addTriplet(t);

            YACCDataUnit data = new YACCDataUnit();
            data.reservedTriplet = tripletID;
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);

            $$ = new ParserVal(data);
        }
    ;

procedimiento_args
    : '(' tipo_basico ID ')'
        {
            YACCDataUnit data = new YACCDataUnit();

            data.tokensData.add((LocatedSymbolTableEntry)$2.obj);
            data.tokensData.add((LocatedSymbolTableEntry)$3.obj);

            // Agregar argumento a la tabla de simbolos

            LocatedSymbolTableEntry argDataTypeTokenData = (LocatedSymbolTableEntry)$2.obj;
            LocatedSymbolTableEntry argNameTokenData = (LocatedSymbolTableEntry)$3.obj;

            DataType argDataType = semanticHelper.tokenIDtoDataType.get(argDataTypeTokenData.getSTEntry().getTokenID());
            String argName = argNameTokenData.getSTEntry().getLexeme();
            String entryKey = argName + ":" + getCurrentScopeStr() + ":" + getCurrentID();

            symbolTable.addNewEntry(
                new SymbolTableEntry(
                    Parser.ID,
                    argName
                ),
                entryKey
            )
            .setAttrib(AttribKey.ID_TYPE, IDType.VAR_ATTRIB)
            .setAttrib(AttribKey.DATA_TYPE, argDataType)
            .setAttrib(AttribKey.MEMORY_ASSOCIATION, (new MemoryAssociation(12, argDataType.getSize(), argDataType)).setOffsetRespectEBP());

            $$ = new ParserVal(data);
        }
    | '(' ')'
        {
            $$ = new ParserVal(new YACCDataUnit());
        }
    ;

procedimiento
    : void_con_reserva id_ambito procedimiento_args procedimiento_cuerpo
        {
            YACCDataUnit data1 = (YACCDataUnit)$1.obj;
            YACCDataUnit data3 = (YACCDataUnit)$3.obj;
            YACCDataUnit data4 = (YACCDataUnit)$4.obj;

            if (!data3.isValid() || !data4.isValid())
            {
                YACCDataUnit data = new YACCInvalidDataUnit();
                data.tokensData.add((LocatedSymbolTableEntry)$1.obj);
                $$ = new ParserVal(data);
                break;
            }

            String funcLexeme = getSTEntry($2).getLexeme();

            String funcEntryKey = funcLexeme + ":" + getCurrentScopeStr();

            if (semanticHelper.alreadyDeclaredInScope(funcLexeme, getCurrentScopeStr()))
            {
                compiler.reportSemanticError("Identificador ya declarado en el ámbito local", getTokenLocation($2));
                $$ = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            boolean hasArgument = data3.tokensData.size() > 0;

            LocatedSymbolTableEntry argDataType = hasArgument ? data3.tokensData.get(0) : null;
            LocatedSymbolTableEntry argName = hasArgument ? data3.tokensData.get(1) : null;

            // Agregar tag de inicio

            int bodyFirstTripletID = listOfTriplets.getSize() - data4.tripletQuantity;

            String assemblyTag = SymbolTable.encodeString("@@" + funcEntryKey);

            listOfTriplets.addTag(bodyFirstTripletID, assemblyTag);

            semanticHelper.declareFunction(getCurrentScopeStr(), $2.obj, argDataType);

            SymbolTableEntry funcEntry = symbolTable.getEntry(funcEntryKey);

            if (currentClassEntryKey != null)
                funcEntry.setAttrib(AttribKey.METHOD_OF, currentClassEntryKey);

            // Agregar atributo del tag a la tabla de simbolos

            funcEntry.setAttrib(AttribKey.ASSEMBLY_TAG, assemblyTag);

            if (hasArgument)
            {
                // Agregar la entryKey del argumento a un attrib de esta funcion

                String argEntryKey = argName.getSTEntry().getLexeme() + ":" + semanticHelper.invertScope(funcEntryKey);
                funcEntry.setAttrib(AttribKey.ARG_ENTRY_KEY, argEntryKey);
            }

            YACCDataUnit data = new YACCDataUnit();
            data.tokensData.add(data1.tokensData.get(0));
            data.tripletQuantity = data4.tripletQuantity;
            data.referencedEntryKey = funcLexeme + ":" + getCurrentScopeStr();

            Triplet lastTriplet = listOfTriplets.getLastTriplet();

            if (lastTriplet.getOperation() == null || lastTriplet.getOperation().equals("RETURN") == false)
            {
                // Entonces agregar un RETURN implicitamente

                Triplet returnTriplet = new Triplet("RETURN", null, null);
                listOfTriplets.addTriplet(returnTriplet);
                data.tripletQuantity++;
            }

            // Backpatch del salto

            int jumpToTriplet = 1 + data1.reservedTriplet + data.tripletQuantity;

            Triplet patchedTriplet = new Triplet(
                "JMP",
                new TripletOperand(jumpToTriplet, listOfTriplets),
                null
            );

            listOfTriplets.replaceTriplet(data1.reservedTriplet, patchedTriplet);

            listOfTriplets.addTag(jumpToTriplet, SymbolTable.encodeString("@@" + funcEntryKey + "_end"));

            if (data4.tripletQuantity > 0)
            {
                // Setear al primer triplet, que es el inicio del procedimiento
                Triplet q = listOfTriplets.getTriplet(data4.firstTriplet);
                q.setTripletInfo((new TripletInfo()).setProcedureInit());
            }

            // Agregar a la tabla de simbolos el terceto donde empieza la funcion

            symbolTable.getEntry(funcEntryKey).setAttrib(AttribKey.FIRST_TRIPLET, data4.firstTriplet);

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

            int bodyStartTriplet = listOfTriplets.getSize() - data2.tripletQuantity - data5.tripletQuantity;

            Triplet triplet = new Triplet("NEG_CJUMP", new TripletOperand(bodyStartTriplet, listOfTriplets), null);
            int tripletID = listOfTriplets.addTriplet(triplet);

            YACCDataUnit data = new YACCDataUnit();
            data.tokensData.add((LocatedSymbolTableEntry)$1.obj);
            data.tripletQuantity = 1 + data2.tripletQuantity + data5.tripletQuantity;
            data.firstTriplet = tripletID;

            listOfTriplets.addTag(bodyStartTriplet, "do_until_" + listOfTriplets.getIncrementalNum());

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
    listOfTriplets.addTriplet(new Triplet("END", null, null));
    return listOfTriplets;
}


DataType getTripletOperandDataType(TripletOperand o)
{
    if (o.isFinal())
        return (DataType)(o.getstEntry().getAttrib(AttribKey.DATA_TYPE));
    else
        return listOfTriplets.getTriplet(o.getIndex()).getType();
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
    this.semanticHelper = compiler.getSemanticHelper();
    this.symbolTable = compiler.getSymbolTable();
    this.listOfTriplets = new ListOfTriplets();
    this.sumCompatibilityTable = new SumCompatibilityTable();
    this.mulCompatibilityTable = new MulCompatibilityTable();
    this.divCompatibilityTable = new DivCompatibilityTable();
    this.compCompatibilityTable = new CompCompatibilityTable();
    this.currentClassEntryKey = null;
    yydebug = false;
}