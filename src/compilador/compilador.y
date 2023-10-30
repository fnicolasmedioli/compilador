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

sentencia_declarativa
    : tipo_basico lista_identificadores ','
        {
            Compilador.addFoundSyntacticStructure(
                new SyntacticStructureResult("Declaracion de variables primitivas", getTokenLocation($1))
            );

            SemanticHelper.declarePrimitivesIfPossible($2, getCurrentScopeStr());
        }
    | ID lista_identificadores ','
        {
            Compilador.addFoundSyntacticStructure(
                new SyntacticStructureResult("Declaracion de variables tipo objeto", getTokenLocation($1))
            );

            SemanticHelper.declareObjectsIfPossible($2, getCurrentScopeStr(), (LocatedSymbolTableEntry)$1.obj);
        }
    | definicion_funcion ','
        {
            Compilador.addFoundSyntacticStructure(
                new SyntacticStructureResult("Definicion de funcion", getTokenLocation($1))
            );
        }
    | definicion_clase ','
        {
            Compilador.addFoundSyntacticStructure(
                new SyntacticStructureResult("Definicion de clase", getTokenLocation($1))
            );
        }
    | implementacion ','
        {
            Compilador.addFoundSyntacticStructure(
                new SyntacticStructureResult("Implementacion de metodo", getTokenLocation($1))
            );
        }
    ;

sentencia_ejecutable
    : ID op_asignacion_aumentada expr ','
        {
            Compilador.addFoundSyntacticStructure(
                new SyntacticStructureResult("Asignacion a variable", getTokenLocation($1))
            );
        }
    | acceso_atributo op_asignacion_aumentada expr ','
        {
            Compilador.addFoundSyntacticStructure(
                new SyntacticStructureResult("Asignacion a atributo", getTokenLocation($1))
            );
        }
    | ID '.' invocacion_funcion ','
        {
            Compilador.addFoundSyntacticStructure(
                new SyntacticStructureResult("Invocacion a metodo", getTokenLocation($1))
            );
        }
    | acceso_atributo '.' invocacion_funcion ','
        {
            Compilador.addFoundSyntacticStructure(
                new SyntacticStructureResult("Invocacion a metodo", getTokenLocation($1))
            );
        }
    | invocacion_funcion ','
        {
            Compilador.addFoundSyntacticStructure(
                new SyntacticStructureResult("Invocacion a funcion", getTokenLocation($1))
            );
        }
    | sentencia_if ','
        {
            Compilador.addFoundSyntacticStructure(
                new SyntacticStructureResult("Sentencia IF", getTokenLocation($1))
            );
        }
    | do_until ','
        {
            Compilador.addFoundSyntacticStructure(
                new SyntacticStructureResult("Estructura DO UNTIL", getTokenLocation($1))
            );
        }
    | PRINT CTE_STRING ','
        {
            Compilador.addFoundSyntacticStructure(
                new SyntacticStructureResult("Sentencia PRINT", getTokenLocation($1))
            );
        }
    | RETURN ','
        {
            Compilador.addFoundSyntacticStructure(
                new SyntacticStructureResult("Sentencia RETURN", getTokenLocation($1))
            );
        }
    | PRINT CTE_UINT ','
        {
            Compilador.reportSyntaxError("No se puede imprimir un UINT", getTokenLocation($1));
        }
    | PRINT CTE_LONG ','
        {
            Compilador.reportSyntaxError("No se puede imprimir un LONG", getTokenLocation($1));
        }
    | PRINT ID ','
        {
            Compilador.reportSyntaxError("No se puede imprimir una variable", getTokenLocation($1));
        }
    | PRINT acceso_atributo ','
        {
            Compilador.reportSyntaxError("No se puede imprimir un atributo", getTokenLocation($1));
        }
    | PRINT CTE_DOUBLE ','
        {
            Compilador.reportSyntaxError("No se puede imprimir un DOUBLE", getTokenLocation($1));
        }
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
    | ID '(' parametro_real ')'
    | ID '(' error ')'
        {
            Compilador.reportSyntaxError("Error en invocacion a metodo", getTokenLocation($1));
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
            Compilador.reportSyntaxError("Error en IF", getTokenLocation($1));
        }
    ;

constante
    : CTE_UINT
    | CTE_STRING
    | CTE_DOUBLE
    | CTE_LONG
        {
            if (!ConstantRange.isValidLONG(getSTEntry($1).getLexeme(), false))
                Compilador.reportLexicalError("El rango de LONG es [-2147483648, 2147483647]", getTokenLocation($1));
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
            Compilador.reportLexicalError("Las constantes tipo UINT no pueden ser negativas", getTokenLocation($1));
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
    | constante
    ;

parametro_formal
    : tipo_basico ID
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

procedimiento_abrir_scope
    :  '{'
        {
            addToCurrentScope(getCurrentID());
        }
    ;

procedimiento_cerrar_scope
    : lista_sentencias '}'
        {
            removeScope();
        }
    | '}'
        {
            removeScope();
        }
    ;

procedimiento
    : VOID id_ambito '(' parametro_formal ')' procedimiento_abrir_scope procedimiento_cerrar_scope
    | VOID id_ambito '(' ')' procedimiento_abrir_scope  procedimiento_cerrar_scope
    | VOID error '}'
        {
            Compilador.reportSyntaxError("Error en funcion/metodo", getTokenLocation($1));
        }
    ;

do_until
    : DO sentencia_ejecutable UNTIL '(' condicion ')'
    | DO '{' lista_sentencias_ejecutables '}' UNTIL '(' condicion ')'
    ;

metodo
    : procedimiento
    ;

acceso_atributo
    : ID '.' ID
    | acceso_atributo '.' ID
    | acceso_atributo '.' invocacion_funcion
    ;

definicion_clase
    : CLASS ID '{' cuerpo_clase '}'
    | CLASS ID '{' '}'
    ;

cuerpo_clase
    : clase_lista_atributos clase_lista_metodos clase_lista_composicion
    | clase_lista_atributos clase_lista_metodos
    | clase_lista_atributos clase_lista_composicion
    | clase_lista_metodos clase_lista_composicion
    | clase_lista_atributos
    | clase_lista_metodos
    | clase_lista_composicion
    | error
        {
            Compilador.reportSyntaxError("Error en cuerpo de clase", getTokenLocation($1));
        }
    ;

clase_lista_atributos
    : clase_lista_atributos tipo_basico lista_identificadores ','
    | tipo_basico lista_identificadores ','
    | ID lista_identificadores ','
    ;

clase_lista_metodos
    : clase_lista_metodos metodo ','
    | metodo ','
        {
            Compilador.addFoundSyntacticStructure(
                new SyntacticStructureResult("Implementacion de metodo dentro de clase", getTokenLocation($1))
            );
        }
    ;

clase_lista_composicion
    : clase_lista_composicion ID ','
    | ID ','
    ;

implementacion
    : IMPL FOR ID ':' '{' clase_lista_metodos '}'
        {
            Compilador.addFoundSyntacticStructure(
                new SyntacticStructureResult("Implementacion de metodo fuera de clase", getTokenLocation($1))
            );
        }
    | IMPL error '}'
        {
            Compilador.reportSyntaxError("Error en implementación dsitribuida", getTokenLocation($1));
        }
    ;

%%

void yyerror(String msg)
{
    // CompilerMessagePrinter.error(msg);
}

int yylex()
{
    return Compilador.yylex();
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

static String _currentID = "";
static LinkedList<String> _currentScope = new LinkedList<>();

private void addToCurrentScope(String s)
{
    if (_currentScope == null)
        _currentScope = new LinkedList<>();

    _currentScope.add(s);
}

private String getCurrentScopeStr()
{
    String scope = "global";

    for (String subscope : _currentScope)
        scope += ":" + subscope;
    
    return scope;
}

private void removeScope()
{
    _currentScope.pop();
}

private String getCurrentID()
{
    return _currentID;
}

private void setCurrentID(String id)
{
    _currentID = id;
}