%token IF ELSE END_IF PRINT CLASS VOID ID
       LONG UINT DOUBLE
       CTE_LONG CTE_UINT CTE_DOUBLE
       CMP_GE CMP_LE CMP_EQUAL CMP_NOT_EQUAL
       SUB_ASIGN
       DO UNTIL IMPL FOR

%start programa

%%

programa    :   '{' lista_sentencias '}'
            ;

comparador  :   CMP_GE
            |   CMP_LE
            |   CMP_EQUAL
            |   CMP_NOT_EQUAL
            ;

condicion   :   expr comparador expr
            ;

lista_sentencias    :   lista_sentencias sentencia_ej
                    |   lista_sentencias sentencia_de
                    |   sentencia_ej
                    |   sentencia_de
                    ;

lista_sentencias_ej :   lista_sentencias_ej sentencia_ej
                    |   sentencia_ej
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

sentencia_ej    :   ID '=' expr ','
                |   invocacion_funcion ','
                |   sentencia_if ','
                ;

sentencia_if    :   IF '(' condicion ')' sentencia_ej END_IF
                |   IF '(' condicion ')' '{' lista_sentencias_ej '}' END_IF
                |   IF '(' condicion ')' sentencia_ej ELSE sentencia_ej END_IF
                |   IF '(' condicion ')' sentencia_ej ELSE '{' lista_sentencias_ej '}' END_IF
                |   IF '(' condicion ')' '{' lista_sentencias_ej '}' ELSE sentencia_ej END_IF
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

definicion_funcion  :   VOID ID '(' parametro_formal ')' '{' lista_sentencias '}'
                    |   VOID ID '(' ')' '{' lista_sentencias '}'
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