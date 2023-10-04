%token IF ELSE END_IF PRINT CLASS VOID ID
       LONG UINT DOUBLE STRING
       CTE_LONG CTE_UINT CTE_DOUBLE CTE_STRING
       CMP_GE CMP_LE CMP_EQUAL CMP_NOT_EQUAL
       SUB_ASIGN
       DO UNTIL IMPL FOR RETURN TOD

%start programa

%%

programa
    : '{' lista_sentencias '}'  { Compilador.setSyntacticTree((SyntacticTreeNode)$2.obj); }
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
    : tipo lista_identificadores ','    { $$.obj = new SyntacticTreeNode("Declaración"); }
    | definicion_funcion ','            { $$.obj = new SyntacticTreeNode("Definición de función"); }
    | definicion_clase ','              { $$.obj = new SyntacticTreeNode("Definición de clase"); }
    | implementacion ','                { $$.obj = new SyntacticTreeNode("Implementación de métodos"); }
    ;

sentencia_ejecutable
    : ID op_asignacion_aumentada expr ','               { $$.obj = new SyntacticTreeNode("Asignación"); }
    | acceso_atributo op_asignacion_aumentada expr ','  { $$.obj = new SyntacticTreeNode("Asignación"); }
    | invocacion_funcion ','                            { $$.obj = new SyntacticTreeNode("Invocación"); }
    | sentencia_if ','                                  { $$.obj = new SyntacticTreeNode("Sentencia IF"); }
    | do_until ','                                      { $$.obj = new SyntacticTreeNode("Sentencia DO UNTIL"); }
    | PRINT CTE_STRING ','                              { $$.obj = new SyntacticTreeNode("Sentencia PRINT"); }
    | RETURN ','                                        { $$.obj = new SyntacticTreeNode("Sentencia RETURN"); }
    ;

lista_sentencias
    : lista_sentencias sentencia_ejecutable     { $$.obj = ((SyntacticTreeNode)$1.obj).addChild($2.obj); }
    | lista_sentencias sentencia_declarativa    { $$.obj = ((SyntacticTreeNode)$1.obj).addChild($2.obj); }
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
            if (!ConstantRange.isValidLONG($1.sval, false))
                CompilerMessagePrinter.error("El rango de UINT es [-2147483648, 2147483647]");
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
    : definicion_funcion
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
    | metodo ','
    ;

clase_lista_composicion
    : clase_lista_composicion ID ','
    | ID ','
    ;

implementacion
    : IMPL FOR ID ':' '{' clase_lista_metodos '}'
    ;

%%

void yyerror(String msg)
{
    CompilerMessagePrinter.error(msg);
}

int yylex()
{
    return Compilador.yylex();
}

public void setyylval(String v)
{
    this.yylval = new ParserVal(v);
}

public String getyylval()
{
    return this.yylval.sval;
}