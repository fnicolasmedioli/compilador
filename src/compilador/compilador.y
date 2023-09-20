%token IF ELSE END_IF PRINT CLASS VOID ID
       LONG UINT DOUBLE
       CTE_LONG CTE_UINT CTE_DOUBLE
       CMP_GE CMP_LE CMP_EQUAL CMP_NOT_EQUAL
       SUB_ASIGN
       DO UNTIL IMPL FOR RETURN

%start programa

%%

programa    :   '{' lista_sentencias '}'
            ;

comparador  :   CMP_GE
            |   CMP_LE
            |   CMP_EQUAL
            |   CMP_NOT_EQUAL
            |   '>'
            |   '<'
            ;

condicion   :   expr comparador expr
            ;

lista_sentencias    :   lista_sentencias sentencia_ej
                    |   lista_sentencias sentencia_de
                    |   sentencia_ej
                    |   sentencia_de
                    ;

lista_sentencias_ret    :   lista_sentencias_ret sentencia_ej_ret
                        |   lista_sentencias_ret sentencia_de
                        |   sentencia_ej_ret
                        |   sentencia_de
                        ;

lista_sentencias_ej :   lista_sentencias_ej sentencia_ej
                    |   sentencia_ej
                    ;

lista_sentencias_ej_ret :   lista_sentencias_ej_ret sentencia_ej_ret
                        |   sentencia_ej_ret
                        ;
                    
tipo    :   LONG
        |   UINT
        |   DOUBLE
        ;

lista_identificadores   :   lista_identificadores ';' ID
                        |   ID
                        ;
                        
sentencia_de    :   tipo lista_identificadores ','
                |   definicion_funcion ','
                ;

invocacion_funcion  :   ID '(' ')'
                    |   ID '(' parametro_real ')'
                    ;

op_asignacion_aumentada :   '='
                        |   SUB_ASIGN
                        ;

sentencia_ej    :   ID op_asignacion_aumentada expr ','
                |   invocacion_funcion ','
                |   sentencia_if ','
                |   do_until ','
                ;

sentencia_ej_ret    :   ID op_asignacion_aumentada expr ','
                    |   invocacion_funcion ','
                    |   sentencia_if_ret ','
                    |   RETURN ','
                    |   do_until_ret ','
                    ;

sentencia_if    :   IF '(' condicion ')' sentencia_ej END_IF
                |   IF '(' condicion ')' '{' lista_sentencias_ej '}' END_IF
                |   IF '(' condicion ')' sentencia_ej ELSE sentencia_ej END_IF
                |   IF '(' condicion ')' sentencia_ej ELSE '{' lista_sentencias_ej '}' END_IF
                |   IF '(' condicion ')' '{' lista_sentencias_ej '}' ELSE sentencia_ej END_IF
                |   IF '(' condicion ')' '{' lista_sentencias_ej '}' ELSE '{' lista_sentencias_ej '}' END_IF
                ;

sentencia_if_ret    :   IF '(' condicion ')' sentencia_ej_ret END_IF
                    |   IF '(' condicion ')' '{' lista_sentencias_ej_ret '}' END_IF
                    |   IF '(' condicion ')' sentencia_ej_ret ELSE sentencia_ej_ret END_IF
                    |   IF '(' condicion ')' sentencia_ej_ret ELSE '{' lista_sentencias_ej_ret '}' END_IF
                    |   IF '(' condicion ')' '{' lista_sentencias_ej_ret '}' ELSE sentencia_ej_ret END_IF
                    |   IF '(' condicion ')' '{' lista_sentencias_ej_ret '}' ELSE '{' lista_sentencias_ej_ret '}' END_IF
                    ;

constante   :   CTE_LONG
            |   CTE_UINT
            |   CTE_DOUBLE
            ;

expr    :   expr '+' term
        |   expr '-' term
        |   term
        ;

term    :   term '*' factor
        |   term '/' factor
        |   factor
        ;
        
factor  :   ID
        |   constante
        ;

parametro_formal    :   tipo ID
                    ;

parametro_real  :   expr
                ;

definicion_funcion  :   VOID ID '(' parametro_formal ')' '{' lista_sentencias_ret '}'
                    |   VOID ID '(' ')' '{' lista_sentencias_ret '}'
                    ;


do_until    :   DO sentencia_ej UNTIL '(' condicion ')'
            |   DO '{' lista_sentencias_ej '}' UNTIL '(' condicion ')'
            ;

do_until_ret    :   DO sentencia_ej UNTIL '(' condicion ')'
                |   DO '{' lista_sentencias_ej_ret '}' UNTIL '(' condicion ')'
                ;



%%

void yyerror(String msg)
{
    System.out.println("Parser error: " + msg);
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