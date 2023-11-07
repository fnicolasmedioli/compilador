%{
 
import java.util.LinkedList;

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
    | CMP_LE
    | CMP_EQUAL
    | CMP_NOT_EQUAL
    | '>'
    | '<'
    ;

condicion
    : expr comparador expr
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
        }
    | ID lista_identificadores ','
        {
            semanticHelper.declareObjectList($2, getCurrentScopeStr(), (LocatedSymbolTableEntry)$1.obj);
        }
    ;

sentencia_declarativa
    : declaracion_variable
        {
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Declaración de variable/s", getTokenLocation($1))
            );
        }
    | definicion_funcion ','
        {
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Definición de función", getTokenLocation($1))
            );
        }
    | definicion_clase ','
        {
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Definición de clase", getTokenLocation($1))
            );
        }
    | implementacion ','
        {
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Sentencia IMPL", getTokenLocation($1))
            );
        }
    ;

sentencia_ejecutable
    : ID op_asignacion_aumentada expr ','
        {
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
        }
    | acceso_atributo op_asignacion_aumentada expr ','
        {
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
        }
    | ID '.' invocacion_funcion ','
        {
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
                compiler.reportSemanticError(String.format("El método '%s' no está definido para la clase '%s'", methodLexeme, classLexeme), getTokenLocation($3));
                break;
            }

            if (methodEntry.getAttrib(AttribKey.ID_TYPE) != IDType.FUNC_METHOD)
            {
                compiler.reportSemanticError(String.format("El identificador '%s' no es un método en la clase '%s'", methodLexeme, classLexeme), getTokenLocation($3));
                break;
            }
        }
    | acceso_atributo '.' invocacion_funcion ','
        {
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Invocación a método", getTokenLocation($1))
            );
        }
    | invocacion_funcion ','
        {
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Invocación a función", getTokenLocation($1))
            );

            String functionLexeme = getSTEntry($1).getLexeme();
            SymbolTableEntry functionEntry = semanticHelper.getEntryByScope(functionLexeme, getCurrentScopeStr());

            if (functionEntry == null)
            {
                compiler.reportSemanticError(String.format("La función '%s' no es alcanzable", functionLexeme), getTokenLocation($1));
                break;
            }
        }
    | sentencia_if ','
        {
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Sentencia IF", getTokenLocation($1))
            );
        }
    | do_until ','
        {
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Estructura DO UNTIL", getTokenLocation($1))
            );
        }
    | PRINT imprimible ','
        {
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Sentencia PRINT", getTokenLocation($1))
            );
        }
    | RETURN ','
        {
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Sentencia RETURN", getTokenLocation($1))
            );
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
    | sentencia_ejecutable
    ;

invocacion_funcion
    : ID '(' ')'
        {
            // Chequar que coincidan los argumentos
        }
    | ID '(' parametro_real ')'
        {
            // Chequar que coincidan los argumentos
        }
    | ID '(' error ')'
        {
            compiler.reportSyntaxError("Error en invocacion a metodo", getTokenLocation($1));
        }
    ;

op_asignacion_aumentada
    : '='
    | SUB_ASIGN
    ;

sentencia_if
    : IF '(' condicion ')' sentencia_ejecutable END_IF
    | IF '(' condicion ')' '{' lista_sentencias_ejecutables '}' END_IF
    | IF '(' condicion ')' sentencia_ejecutable ELSE sentencia_ejecutable END_IF
    | IF '(' condicion ')' sentencia_ejecutable ELSE '{' lista_sentencias_ejecutables '}' END_IF
    | IF '(' condicion ')' '{' lista_sentencias_ejecutables '}' ELSE sentencia_ejecutable END_IF
    | IF '(' condicion ')' '{' lista_sentencias_ejecutables '}' ELSE '{' lista_sentencias_ejecutables '}' END_IF
    | IF error END_IF ','
        {
            compiler.reportSyntaxError("Error en IF", getTokenLocation($1));
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
    ;

basic_expr
    : expr '+' term
    | expr '-' term
    | term
    ;

term
    : term '*' factor
    | term '/' factor
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
        }
    | constante
    ;

parametro_real
    : expr
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
            String funcLexeme = getSTEntry($2).getLexeme();

            if (semanticHelper.alreadyDeclaredInScope(funcLexeme, getCurrentScopeStr()))
            {
                compiler.reportSemanticError("Identificador ya declarado en el ámbito local", getTokenLocation($2));
                break;
            }

            semanticHelper.declareFunction(getCurrentScopeStr(), $2.obj);

            String scopeAdentro = getCurrentScopeStr() + ":" + getSTEntry($2).getLexeme();
            semanticHelper.declareArg(scopeAdentro, $5.obj, $4.obj);
        }
    | VOID id_ambito '(' ')' abrir_scope lista_sentencias cerrar_scope
        {
            String funcLexeme = getSTEntry($2).getLexeme();

            if (semanticHelper.alreadyDeclaredInScope(funcLexeme, getCurrentScopeStr()))
            {
                compiler.reportSemanticError("Identificador ya declarado en el ámbito local", getTokenLocation($2));
                break;
            }

            semanticHelper.declareFunction(getCurrentScopeStr(), $2.obj);
        }
    | VOID id_ambito '(' tipo_basico ID ')' abrir_scope cerrar_scope
        {
            String funcLexeme = getSTEntry($2).getLexeme();

            if (semanticHelper.alreadyDeclaredInScope(funcLexeme, getCurrentScopeStr()))
            {
                compiler.reportSemanticError("Identificador ya declarado en el ámbito local", getTokenLocation($2));
                break;
            }

            semanticHelper.declareFunction(getCurrentScopeStr(), $2.obj);
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
        }
    | VOID error '}'
        {
            compiler.reportSyntaxError("Error en funcion/metodo", getTokenLocation($1));
        }
    ;

do_until
    : DO sentencia_ejecutable UNTIL '(' condicion ')'
    | DO '{' lista_sentencias_ejecutables '}' UNTIL '(' condicion ')'
    ;

metodo
    : procedimiento
    ;



definicion_clase
    : CLASS id_ambito abrir_scope cuerpo_clase cerrar_scope
        {
            semanticHelper.declareClass(getCurrentScopeStr(), (LocatedSymbolTableEntry)$2.obj);
        }
    | CLASS id_ambito abrir_scope cerrar_scope
        {
            semanticHelper.declareClass(getCurrentScopeStr(), (LocatedSymbolTableEntry)$2.obj);
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
    | IMPL error '}'
        {
            compiler.reportSyntaxError("Error en implementación distribuida", getTokenLocation($1));
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

public void setyylval(LocatedSymbolTableEntry tokenData)
{
    this.yylval = new ParserVal(tokenData);
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

String _currentID;
LinkedList<String> _currentScope;
Compiler compiler;
SemanticHelper semanticHelper;
SymbolTable symbolTable;
String implementationMethodScope;
LinkedList<String> scopeCopy;

public Parser(Compiler compiler)
{
    this.compiler = compiler;
    this._currentID = "";
    this._currentScope = new LinkedList<>();
    this.semanticHelper = new SemanticHelper(compiler);
    this.symbolTable = compiler.getSymbolTable();
    yydebug = false;
}