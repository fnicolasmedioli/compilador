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
                    
tipo
    : LONG
    | UINT
    | DOUBLE
    | STRING
    ;

lista_identificadores
    : lista_identificadores ';' ID
    | ID
    ;

sentencia_declarativa
    : tipo lista_identificadores ',' { Compilador.addFoundSyntacticStructure(new SyntacticStructureResult("Declaracion de variables primitivas", getSTEntry($1).getLocation() )); }
    | ID lista_identificadores ',' { Compilador.addFoundSyntacticStructure(new SyntacticStructureResult("Declaracion de variables tipo objeto", getSTEntry($1).getLocation() )); }
    | definicion_funcion ',' { Compilador.addFoundSyntacticStructure(new SyntacticStructureResult("Definicion de funcion", getSTEntry($1).getLocation() )); }
    | definicion_clase ',' { Compilador.addFoundSyntacticStructure(new SyntacticStructureResult("Definicion de clase", getSTEntry($1).getLocation() )); }
    | implementacion ',' { Compilador.addFoundSyntacticStructure(new SyntacticStructureResult("Implementacion de metodo", getSTEntry($1).getLocation() )); }
    ;

sentencia_ejecutable
    : ID op_asignacion_aumentada expr ',' { Compilador.addFoundSyntacticStructure(new SyntacticStructureResult("Asignacion a variable", getSTEntry($1).getLocation() )); }
    | acceso_atributo op_asignacion_aumentada expr ',' { Compilador.addFoundSyntacticStructure(new SyntacticStructureResult("Asignacion a atributo", getSTEntry($1).getLocation() )); }
    | ID '.' invocacion_funcion ',' { Compilador.addFoundSyntacticStructure(new SyntacticStructureResult("Invocacion a metodo", getSTEntry($1).getLocation() )); }
    | acceso_atributo '.' invocacion_funcion ',' { Compilador.addFoundSyntacticStructure(new SyntacticStructureResult("Invocacion a metodo", getSTEntry($1).getLocation() )); }
    | invocacion_funcion ',' { Compilador.addFoundSyntacticStructure(new SyntacticStructureResult("Invocacion a funcion", getSTEntry($1).getLocation() )); }
    | sentencia_if ',' { Compilador.addFoundSyntacticStructure(new SyntacticStructureResult("Sentencia IF", getSTEntry($1).getLocation() )); }
    | do_until ',' { Compilador.addFoundSyntacticStructure(new SyntacticStructureResult("Estructura DO UNTIL", getSTEntry($1).getLocation() )); }
    | PRINT CTE_STRING ',' { Compilador.addFoundSyntacticStructure(new SyntacticStructureResult("Sentencia PRINT", getSTEntry($1).getLocation() )); }
    | RETURN ',' { Compilador.addFoundSyntacticStructure(new SyntacticStructureResult("Sentencia RETURN", getSTEntry($1).getLocation() )); }
    | PRINT CTE_UINT ',' { Compilador.reportSyntaxError("No se puede imprimir un UINT", getSTEntry($1).getLocation()); }
    | PRINT CTE_LONG ',' { Compilador.reportSyntaxError("No se puede imprimir un LONG", getSTEntry($1).getLocation()); }
    | PRINT ID ',' { Compilador.reportSyntaxError("No se puede imprimir una variable", getSTEntry($1).getLocation()); }
    | PRINT acceso_atributo ',' { Compilador.reportSyntaxError("No se puede imprimir un atributo", getSTEntry($1).getLocation()); }
    | PRINT CTE_DOUBLE ',' { Compilador.reportSyntaxError("No se puede imprimir un DOUBLE", getSTEntry($1).getLocation()); }
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
    | ID '(' error ')' { Compilador.reportSyntaxError("Error en invocacion a metodo", getSTEntry($1).getLocation()); }
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
    | IF error END_IF ',' { Compilador.reportSyntaxError("Error en IF", getSTEntry($1).getLocation()); }
    ;

constante
    : CTE_UINT
    | CTE_STRING
    | CTE_DOUBLE
    | CTE_LONG
        {
            if (!ConstantRange.isValidLONG(getSTEntry($1).getLexeme(), false))
                Compilador.reportLexicalError("El rango de LONG es [-2147483648, 2147483647]", getSTEntry($1).getLocation());
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
            Compilador.reportLexicalError("Las constantes tipo UINT no pueden ser negativas", getSTEntry($1).getLocation());
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
    : tipo ID
    ;

parametro_real
    : expr
    ;

definicion_funcion
    : procedimiento
    ;

id_ambito
    : ID { setCurrentID(getSTEntry($1).getLexeme()); }
    ;

procedimiento_a
    :  '{' { addToCurrentScope(getCurrentID()); }
    ;

procedimiento_b
    : lista_sentencias '}' { removeScope(); }
    | '}' { removeScope(); }
    ;

procedimiento
    : VOID id_ambito '(' parametro_formal ')' procedimiento_a procedimiento_b
    | VOID id_ambito '(' ')' procedimiento_a  procedimiento_b
    | VOID error '}' { Compilador.reportSyntaxError("Error en funcion/metodo", getSTEntry($1).getLocation()); }
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
    | error { Compilador.reportSyntaxError("Error en cuerpo de clase", getSTEntry($1).getLocation()); }
    ;

clase_lista_atributos
    : clase_lista_atributos tipo lista_identificadores ','
    | tipo lista_identificadores ','
    | ID lista_identificadores ','
    ;

clase_lista_metodos
    : clase_lista_metodos metodo ','
    | metodo ',' { Compilador.addFoundSyntacticStructure(new SyntacticStructureResult("Implementacion de metodo dentro de clase", getSTEntry($1).getLocation() )); }
    ;

clase_lista_composicion
    : clase_lista_composicion ID ','
    | ID ','
    ;

implementacion
    : IMPL FOR ID ':' '{' clase_lista_metodos '}' { Compilador.addFoundSyntacticStructure(new SyntacticStructureResult("Implementacion de metodo fuera de clase", getSTEntry($1).getLocation() )); }
    | IMPL error '}' { Compilador.reportSyntaxError("Error en implementación dsitribuida", getSTEntry($1).getLocation()); }
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

public void setyylval(String symbolTableEntryKey)
{
    this.yylval = new ParserVal(symbolTableEntryKey);
}

public SymbolTableEntry getSTEntry(ParserVal o)
{
    return Compilador.getSymbolTable().getEntry(o.sval);
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