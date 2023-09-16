%token IF ELSE END_IF PRINT CLASS VOID ID
       LONG UINT DOUBLE
       CTE_LONG CTE_UINT CTE_DOUBLE
       CMP_GE CMP_LE CMP_EQUAL CMP_NOT_EQUAL
       SUB_ASIGN
       DO UNTIL IMPL FOR

%start programa

%%

programa    :   VOID

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