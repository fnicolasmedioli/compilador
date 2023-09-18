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

lista_sentencias    :   lista_sentencias sentencia
                    |   sentencia ','
                    ;

sentencia	:	sentencia_ejecutable
			|	sentencia_declarativa
			;
					
tipo	:	LONG
		|	UINT
		|	DOUBLE
		;
		
lista_identificadores	:	lista_identificadores ';' ID
						|	ID
						;
						
sentencia_declarativa	:	tipo lista_identificadores

sentencia_ejecutable	:	ID '=' expr

constante	:	CTE_LONG
			|	CTE_UINT
			|	CTE_DOUBLE

expr	:	expr '+' term
		|	expr '-' term
		|	term
		;

term	:	term '*' factor
		|	term '/' factor
		|	factor
		;
		
factor	:	ID
		|	constante

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