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
    : tipo lista_identificadores ',' { Compilador.addFoundSyntacticStructure(new SyntacticStructureResult("Declaración de variables", ((TokenInfo)$1.obj).getLocation() )); }
    | definicion_funcion ',' { Compilador.addFoundSyntacticStructure(new SyntacticStructureResult("Definición de función", ((TokenInfo)$1.obj).getLocation() )); }
    | definicion_clase ',' { Compilador.addFoundSyntacticStructure(new SyntacticStructureResult("Definición de clase", ((TokenInfo)$1.obj).getLocation() )); }
    | implementacion ',' { Compilador.addFoundSyntacticStructure(new SyntacticStructureResult("Implementación de método", ((TokenInfo)$1.obj).getLocation() )); }
    ;

sentencia_ejecutable
    : ID op_asignacion_aumentada expr ',' { Compilador.addFoundSyntacticStructure(new SyntacticStructureResult("Asignación a variable", ((TokenInfo)$1.obj).getLocation() )); }
    | acceso_atributo op_asignacion_aumentada expr ',' { Compilador.addFoundSyntacticStructure(new SyntacticStructureResult("Asignación a atributo", ((TokenInfo)$1.obj).getLocation() )); }
    | invocacion_funcion ',' { Compilador.addFoundSyntacticStructure(new SyntacticStructureResult("Invocación a función", ((TokenInfo)$1.obj).getLocation() )); }
    | sentencia_if ',' { Compilador.addFoundSyntacticStructure(new SyntacticStructureResult("Sentencia IF", ((TokenInfo)$1.obj).getLocation() )); }
    | do_until ',' { Compilador.addFoundSyntacticStructure(new SyntacticStructureResult("Estructura DO UNTIL", ((TokenInfo)$1.obj).getLocation() )); }
    | PRINT CTE_STRING ',' { Compilador.addFoundSyntacticStructure(new SyntacticStructureResult("Sentencia PRINT", ((TokenInfo)$1.obj).getLocation() )); }
    | RETURN ',' { Compilador.addFoundSyntacticStructure(new SyntacticStructureResult("Sentencia RETURN", ((TokenInfo)$1.obj).getLocation() )); }
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
    ;

constante
    : CTE_UINT
    | CTE_STRING
    | CTE_DOUBLE
    | CTE_LONG
        {
            if (!ConstantRange.isValidLONG(((TokenInfo)$1.obj).getLexeme(), false))
                Compilador.reportLexicalError("El rango de UINT es [-2147483648, 2147483647]");
        }
    | '-' CTE_LONG
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

procedimiento
    : VOID ID '(' parametro_formal ')' '{' lista_sentencias '}'
    | VOID ID '(' ')' '{' lista_sentencias '}'
    | VOID ID '(' parametro_formal ')' '{' '}'
    | VOID ID '(' ')' '{' '}'
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
    ;

clase_lista_atributos
    : clase_lista_atributos tipo lista_identificadores ','
    | tipo lista_identificadores ','
    | ID lista_identificadores ','
    ;

clase_lista_metodos
    : clase_lista_metodos metodo ','
    | metodo ',' { Compilador.addFoundSyntacticStructure(new SyntacticStructureResult("Implementación de método dentro de clase", ((TokenInfo)$1.obj).getLocation() )); }
    ;

clase_lista_composicion
    : clase_lista_composicion ID ','
    | ID ','
    ;

implementacion
    : IMPL FOR ID ':' '{' clase_lista_metodos '}' { Compilador.addFoundSyntacticStructure(new SyntacticStructureResult("Implementación de método fuera de clase", ((TokenInfo)$1.obj).getLocation() )); }
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

public void setyylval(Object v)
{
    this.yylval = new ParserVal(v);
}