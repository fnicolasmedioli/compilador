%{
import syntacticTree.*;
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
    : '{' lista_sentencias '}'  { Compilador.setSyntacticTree((PrintableSyntacticTree)$2.obj); }
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
    : tipo lista_identificadores ','    { $$.obj = new SyntacticTreeNode("Declaración variable/s"); }
    | definicion_funcion ','
    | definicion_clase ','
    | implementacion ','
    ;

sentencia_ejecutable
    : ID op_asignacion_aumentada expr ','               { $$.obj = new SyntacticTreeNode("Asignación"); }
    | acceso_atributo op_asignacion_aumentada expr ','  { $$.obj = new SyntacticTreeNode("Asignación"); }
    | invocacion_funcion ','                            { $$.obj = new SyntacticTreeNode("Invocación"); }
    | sentencia_if ','
    | do_until ','
    | PRINT CTE_STRING ','                              { $$.obj = new SyntacticTreeNode("Sentencia PRINT"); }
    | RETURN ','                                        { $$.obj = new SyntacticTreeNode("Sentencia RETURN"); }
    ;

lista_sentencias
    : lista_sentencias sentencia_ejecutable     { $$.obj = ((SyntacticTreeList)$1.obj).add($2.obj); }
    | lista_sentencias sentencia_declarativa    { $$.obj = ((SyntacticTreeList)$1.obj).add($2.obj); }
    | sentencia_ejecutable						{ $$.obj = new SyntacticTreeList($1.obj); }
    | sentencia_declarativa						{ $$.obj = new SyntacticTreeList($1.obj); }
    ;

lista_sentencias_ejecutables
    : lista_sentencias_ejecutables sentencia_ejecutable     { $$.obj = ((SyntacticTreeList)$1.obj).add($2.obj); }
    | sentencia_ejecutable                                  { $$.obj = new SyntacticTreeList().add($1.obj); }
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
        { $$.obj = new SyntacticTreeNode("Sentencia IF", $5.obj); }
    | IF '(' condicion ')' '{' lista_sentencias_ejecutables '}' END_IF
        { $$.obj = new SyntacticTreeNode("Sentencia IF", $6.obj); }
    | IF '(' condicion ')' sentencia_ejecutable ELSE sentencia_ejecutable END_IF
        { $$.obj = new SyntacticTreeList(
                new SyntacticTreeNode("Sentencia IF", $5.obj),
                new SyntacticTreeNode("Sentencia ELSE", $7.obj)
          );
        }
    | IF '(' condicion ')' sentencia_ejecutable ELSE '{' lista_sentencias_ejecutables '}' END_IF
        { $$.obj = new SyntacticTreeList(
                new SyntacticTreeNode("Sentencia IF", $5.obj),
                new SyntacticTreeNode("Sentencia ELSE", $8.obj)
          );
        }
    | IF '(' condicion ')' '{' lista_sentencias_ejecutables '}' ELSE sentencia_ejecutable END_IF
        { $$.obj = new SyntacticTreeList(
                new SyntacticTreeNode("Sentencia IF", $6.obj),
                new SyntacticTreeNode("Sentencia ELSE", $9.obj)
          );
        }
    | IF '(' condicion ')' '{' lista_sentencias_ejecutables '}' ELSE '{' lista_sentencias_ejecutables '}' END_IF
        { $$.obj = new SyntacticTreeList(
                new SyntacticTreeNode("Sentencia IF", $6.obj),
                new SyntacticTreeNode("Sentencia ELSE", $10.obj)
          );
        }
    ;

constante
    : CTE_UINT
    | CTE_STRING
    | CTE_DOUBLE
    | CTE_LONG
        {
            if (!ConstantRange.isValidLONG($1.sval, false))
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
    : procedimiento { $$.obj = new SyntacticTreeNode("Definición función", $1.obj); }
    ;

procedimiento
    : VOID ID '(' parametro_formal ')' '{' lista_sentencias '}' {
            $$.obj = $7.obj;
        }
    | VOID ID '(' ')' '{' lista_sentencias '}'  {
            $$.obj = $6.obj;
        }
    | VOID ID '(' parametro_formal ')' '{' '}'
    | VOID ID '(' ')' '{' '}'
    ;

do_until
    : DO sentencia_ejecutable UNTIL '(' condicion ')' {
            $$.obj = new SyntacticTreeNode("Sentencia DO UNTIL", $2.obj);
        }
    | DO '{' lista_sentencias_ejecutables '}' UNTIL '(' condicion ')' {
            $$.obj = new SyntacticTreeNode("Sentencia DO UNTIL", $3.obj);
        }
    ;

metodo
    : procedimiento { $$.obj = new SyntacticTreeNode("Definición método", $1.obj); }
    ;

acceso_atributo
    : ID '.' ID
    | acceso_atributo '.' ID
    | acceso_atributo '.' invocacion_funcion
    ;

definicion_clase
    : CLASS ID '{' cuerpo_clase '}' { $$.obj = new SyntacticTreeNode("Definicion clase", $4.obj); }
    | CLASS ID '{' '}' { $$.obj = new SyntacticTreeNode("Definicion clase"); }
    ;

cuerpo_clase
    : clase_lista_atributos clase_lista_metodos clase_lista_composicion
        { $$.obj = new SyntacticTreeList().add($1.obj).add($2.obj).add($3.obj); }
    | clase_lista_atributos clase_lista_metodos
        { $$.obj = new SyntacticTreeList().add($1.obj).add($2.obj); }
    | clase_lista_atributos clase_lista_composicion
        { $$.obj = new SyntacticTreeList().add($1.obj).add($2.obj); }
    | clase_lista_metodos clase_lista_composicion
        { $$.obj = new SyntacticTreeList().add($1.obj).add($2.obj); }
    | clase_lista_atributos
        { $$.obj = new SyntacticTreeList().add($1.obj); }
    | clase_lista_metodos
        { $$.obj = new SyntacticTreeList().add($1.obj); }
    | clase_lista_composicion
        { $$.obj = new SyntacticTreeList().add($1.obj); }
    ;

clase_lista_atributos
    : clase_lista_atributos tipo lista_identificadores ','
        { $$.obj = new SyntacticTreeNode("Definición de atributo/s"); }
    | tipo lista_identificadores ','
        { $$.obj = new SyntacticTreeNode("Definición de atributo/s"); }
    | ID lista_identificadores ','
        { $$.obj = new SyntacticTreeNode("Definición de atributo/s"); }
    ;

clase_lista_metodos
    : clase_lista_metodos metodo ',' { $$.obj = ((SyntacticTreeList)$1.obj).add($2.obj); }
    | metodo ',' { $$.obj = new SyntacticTreeList().add($1.obj); }
    ;

clase_lista_composicion
    : clase_lista_composicion ID ',' { $$.obj = ((SyntacticTreeList)$1.obj).add($2.obj); }
    | ID ',' { $$.obj = new SyntacticTreeList().add($1.obj); }
    ;

implementacion
    : IMPL FOR ID ':' '{' clase_lista_metodos '}' { $$.obj = new SyntacticTreeNode("Implementación de metodos IMPL", $6.obj); }
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