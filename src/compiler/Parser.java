//### This file created by BYACC 1.8(/Java extension  1.15)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";



package compiler;



//#line 2 "compiler.y"

import java.util.Vector;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Arrays;

import compiler.semantic.*;
import compiler.CompatibilityTable.*;

//#line 30 "Parser.java"




public class Parser
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[] = new int[YYSTACKSIZE]; //state stack
int stateptr;
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
final void state_push(int state)
{
  try {
		stateptr++;
		statestk[stateptr]=state;
	 }
	 catch (ArrayIndexOutOfBoundsException e) {
     int oldsize = statestk.length;
     int newsize = oldsize * 2;
     int[] newstack = new int[newsize];
     System.arraycopy(statestk,0,newstack,0,oldsize);
     statestk = newstack;
     statestk[stateptr]=state;
  }
}
final int state_pop()
{
  return statestk[stateptr--];
}
final void state_drop(int cnt)
{
  stateptr -= cnt; 
}
final int state_peek(int relative)
{
  return statestk[stateptr-relative];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
final boolean init_stacks()
{
  stateptr = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//public class ParserVal is defined in ParserVal.java


String   yytext;//user variable to return contextual strings
ParserVal yyval; //used to return semantic vals from action routines
ParserVal yylval;//the 'lval' (result) I got from yylex()
ParserVal valstk[];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
void val_init()
{
  valstk=new ParserVal[YYSTACKSIZE];
  yyval=new ParserVal();
  yylval=new ParserVal();
  valptr=-1;
}
void val_push(ParserVal val)
{
  if (valptr>=YYSTACKSIZE)
    return;
  valstk[++valptr]=val;
}
ParserVal val_pop()
{
  if (valptr<0)
    return new ParserVal();
  return valstk[valptr--];
}
void val_drop(int cnt)
{
int ptr;
  ptr=valptr-cnt;
  if (ptr<0)
    return;
  valptr = ptr;
}
ParserVal val_peek(int relative)
{
int ptr;
  ptr=valptr-relative;
  if (ptr<0)
    return new ParserVal();
  return valstk[ptr];
}
final ParserVal dup_yyval(ParserVal val)
{
  ParserVal dup = new ParserVal();
  dup.ival = val.ival;
  dup.dval = val.dval;
  dup.sval = val.sval;
  dup.obj = val.obj;
  return dup;
}
//#### end semantic value section ####
public final static short IF=257;
public final static short ELSE=258;
public final static short END_IF=259;
public final static short PRINT=260;
public final static short CLASS=261;
public final static short VOID=262;
public final static short ID=263;
public final static short LONG=264;
public final static short UINT=265;
public final static short DOUBLE=266;
public final static short STRING=267;
public final static short CTE_LONG=268;
public final static short CTE_UINT=269;
public final static short CTE_DOUBLE=270;
public final static short CTE_STRING=271;
public final static short CMP_GE=272;
public final static short CMP_LE=273;
public final static short CMP_EQUAL=274;
public final static short CMP_NOT_EQUAL=275;
public final static short SUB_ASIGN=276;
public final static short DO=277;
public final static short UNTIL=278;
public final static short IMPL=279;
public final static short FOR=280;
public final static short RETURN=281;
public final static short TOD=282;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    0,    2,    2,    2,    2,    2,    2,    3,    5,
    5,    5,    5,    6,    6,    7,    7,    8,    8,    9,
   10,   10,   10,   10,   14,   14,   15,   15,   15,   15,
   15,   15,   19,   19,   19,    1,    1,    1,    1,   20,
   20,   16,   16,   21,   22,   22,   23,   23,   24,   17,
   17,   17,   25,   25,   25,   25,   25,   25,   25,    4,
    4,   26,   26,   26,   27,   27,   27,   28,   28,   11,
   30,   31,   32,   33,   33,   34,   34,   35,   29,   29,
   36,   36,   18,   37,   38,   12,   12,   39,   39,   39,
   39,   39,   39,   40,   40,   41,   42,   43,   13,   13,
};
final static short yylen[] = {                            2,
    3,    2,    1,    1,    1,    1,    1,    1,    3,    1,
    1,    1,    1,    3,    1,    3,    1,    1,    1,    3,
    1,    2,    2,    2,    2,    3,    4,    3,    2,    2,
    3,    2,    1,    1,    1,    2,    2,    1,    1,    2,
    1,    1,    1,    1,    1,    3,    1,    3,    1,    6,
    8,    4,    1,    1,    1,    1,    2,    2,    2,    1,
    4,    3,    3,    1,    3,    3,    1,    1,    1,    1,
    1,    1,    1,    4,    2,    2,    3,    1,    4,    3,
    1,    3,    6,    1,    2,    4,    3,    2,    1,    3,
    2,    3,    2,    3,    2,    1,    1,    1,    7,    3,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    0,    0,    0,    0,   10,   11,   12,
   13,    0,    0,    0,    2,    0,   18,    0,    0,   21,
   39,    0,    0,    0,   38,    0,    0,   70,    0,    0,
    0,    0,   35,   34,   33,    0,   71,   85,    0,   17,
    0,   81,    0,    0,    0,   32,    1,   37,   36,   43,
    0,    0,   42,    0,    0,   15,    0,   22,   23,   24,
   29,   30,    0,   72,    0,    0,   56,   53,   55,   54,
    0,    0,   44,    0,    0,    0,   69,   60,    0,   67,
   31,   80,   41,    0,    0,  100,   96,    0,   16,   25,
    0,   28,    0,    0,   20,    0,    0,    0,   73,   89,
   84,   87,    0,    0,   52,    0,   57,   59,   58,    3,
    4,    5,    6,    7,    8,    0,    0,    0,    0,    0,
    0,   82,   40,    0,    0,   26,   27,   14,   75,    0,
    0,   79,   93,   91,    0,   88,   86,    0,    0,    0,
    0,    0,    0,    0,   45,    0,    0,   65,   66,    0,
   97,    0,    0,    0,   76,   92,   90,   61,    0,   50,
    0,   83,    0,    0,   74,   77,   46,    0,   47,    0,
   95,   98,    0,   99,    0,   51,   94,   48,
};
final static short yydgoto[] = {                          2,
   16,  118,   73,   74,   17,   57,   18,   19,   20,   21,
   22,   23,   24,   54,   83,   55,   26,   27,   36,   84,
   76,  146,  170,  147,   77,   78,   79,   80,   28,   38,
   65,  102,   97,  132,   29,   43,  103,   30,  104,  164,
   88,  152,  174,
};
final static short yysindex[] = {                       -92,
  -75,    0,  -38, -173, -214, -204,    0,    0,    0,    0,
    0, -120, -205,   14,    0,   38,    0,  -34, -195,    0,
    0,   37,   50,   62,    0,   63,   64,    0, -214,  -13,
 -150,  -37,    0,    0,    0,   70,    0,    0,  -10,    0,
 -184,    0, -162,   -8, -145,    0,    0,    0,    0,    0,
 -144,  -41,    0,   76,  -37,    0,   23,    0,    0,    0,
    0,    0,   81,    0,  -55,   78,    0,    0,    0,    0,
   85, -179,    0,    3,   82,   88,    0,    0,   27,    0,
    0,    0,    0, -111,   86,    0,    0,   72,    0,    0,
   43,    0,   56, -132,    0,   25,  -13,   89,    0,    0,
    0,    0,   90,  -12,    0,  -37,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   17,   17,  -37, -113,   17,
   17,    0,    0,  -37,    9,    0,    0,    0,    0, -128,
   68,    0,    0,    0,   92,    0,    0,   94,   35,  100,
   27,   27,   35, -184,    0, -117, -105,    0,    0,  104,
    0, -108,  114,   68,    0,    0,    0,    0,   80,    0,
  -82,    0,  112, -114,    0,    0,    0, -184,    0, -101,
    0,    0,  115,    0,   91,    0,    0,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,    0, -103,  -39,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  -26,    0,    0,    0,  -15,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  -98,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  -98,    0,    0,    0,    0,   42,
   -6,   -1,  130,    0,    0,  -86,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
   45,    0,   49,    5,   83,    0,  261,    0,  -32,   -3,
    0,    0,    0,    0,    8,    0,    0,    0,    0,  -91,
    0,    0,    0,    0,    0,   71,  -14,  -16,  -40,  145,
   87,  -99,    0,    0,    0,    0,  -81,    0,    0,    0,
    0,    0,    0,
};
final static int YYTABLESIZE=385;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         90,
   17,   32,   41,   72,  137,   52,   17,   72,   25,  144,
  172,   51,   48,  122,   68,   68,   68,   68,   68,   42,
   68,   17,  138,   49,  101,   64,   53,   64,   64,   64,
    1,  155,  100,   68,   63,   68,   63,   63,   63,   62,
  168,   62,   62,   62,   64,  117,   64,  116,   37,   15,
   44,   39,  159,   63,  166,   63,   91,   46,   62,   93,
   62,   72,  115,  101,  114,  129,   95,   56,  120,   99,
  163,  136,    3,  121,   45,    4,  175,  117,   40,  116,
   58,   94,  173,  126,   60,  117,   60,  116,  107,  108,
  109,  123,   12,   59,   33,   34,   14,   35,  117,  127,
  116,  141,  142,  148,  149,   60,   61,   62,   66,   64,
  139,  101,   99,   81,   82,   85,   86,   87,   89,   92,
   96,  105,  143,  101,  106,  124,  145,   51,  119,  125,
  128,  151,  133,  134,  153,  156,    3,  157,   25,    4,
  158,  160,   40,    3,  162,    3,    4,    6,    4,   40,
   48,   40,  161,    6,  165,  171,   12,  176,  177,   78,
   14,   49,   47,   12,   19,   12,  123,   14,  169,   14,
    9,   49,  150,   63,    3,  154,  140,    4,  130,    0,
   40,    3,  123,  131,    4,    5,    6,    7,    8,    9,
   10,   11,   99,    0,   12,    0,    0,    0,   14,    0,
    0,   12,    0,   13,  167,   14,    6,   98,    8,    9,
   10,   11,    0,    0,    0,  178,    0,   31,    0,    0,
    0,   40,    0,   19,    0,   40,   67,   68,   69,   70,
   67,   68,   69,   70,    0,    0,   17,    0,    0,    0,
   71,   50,    0,    0,   71,   68,   68,   68,   68,    6,
  135,    8,    9,   10,   11,    0,   64,   64,   64,   64,
    0,    0,    0,    0,    0,   63,   63,   63,   63,    0,
   62,   62,   62,   62,  110,  111,  112,  113,    0,   40,
    0,    0,    0,    0,   67,   68,   69,   70,    8,    9,
   10,   11,   75,    0,    3,    0,    0,    4,    5,    6,
    7,    8,    9,   10,   11,    0,    0,    0,    0,    0,
    0,    0,   75,    0,   12,   75,   13,    0,   14,    0,
    0,    0,    0,    0,    3,    0,    0,    4,    5,    6,
    7,    8,    9,   10,   11,    0,    3,    0,    0,    4,
    0,    0,   40,    0,   12,    0,   13,    3,   14,    0,
    4,    0,    0,   40,    0,    0,   12,    0,    0,    0,
   14,    0,    0,    0,    0,    0,   75,   12,    0,    0,
    0,   14,    0,    0,    0,    0,   75,   75,   75,    0,
   75,   75,    0,    0,   75,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         41,
   40,   40,  123,   45,  104,   40,   46,   45,    1,  123,
  125,   46,   16,  125,   41,   42,   43,   44,   45,   12,
   47,   61,  104,   16,   65,   41,   61,   43,   44,   45,
  123,  131,   65,   60,   41,   62,   43,   44,   45,   41,
  123,   43,   44,   45,   60,   43,   62,   45,  263,  125,
  256,  256,  144,   60,  154,   62,   52,   44,   60,   55,
   62,   45,   60,  104,   62,   41,   44,  263,   42,  125,
  152,  104,  257,   47,  280,  260,  168,   43,  263,   45,
   44,   59,  164,   41,   43,   43,   45,   45,  268,  269,
  270,   84,  277,   44,  268,  269,  281,  271,   43,   44,
   45,  116,  117,  120,  121,   44,   44,   44,  259,  123,
  106,  152,  125,   44,  125,  278,  125,  263,  263,   44,
   40,   44,  118,  164,   40,   40,  119,   46,   41,   58,
  263,  123,   44,   44,  263,   44,  257,   44,  131,  260,
   41,  259,  263,  257,   41,  257,  260,  262,  260,  263,
  154,  263,  258,  262,   41,   44,  277,  259,   44,  263,
  281,  154,  125,  277,  263,  277,  159,  281,  161,  281,
   41,  258,  124,   29,  257,  131,  106,  260,   96,   -1,
  263,  257,  175,   97,  260,  261,  262,  263,  264,  265,
  266,  267,  125,   -1,  277,   -1,   -1,   -1,  281,   -1,
   -1,  277,   -1,  279,  125,  281,  262,  263,  264,  265,
  266,  267,   -1,   -1,   -1,  125,   -1,  256,   -1,   -1,
   -1,  263,   -1,  263,   -1,  263,  268,  269,  270,  271,
  268,  269,  270,  271,   -1,   -1,  276,   -1,   -1,   -1,
  282,  276,   -1,   -1,  282,  272,  273,  274,  275,  262,
  263,  264,  265,  266,  267,   -1,  272,  273,  274,  275,
   -1,   -1,   -1,   -1,   -1,  272,  273,  274,  275,   -1,
  272,  273,  274,  275,  272,  273,  274,  275,   -1,  263,
   -1,   -1,   -1,   -1,  268,  269,  270,  271,  264,  265,
  266,  267,   32,   -1,  257,   -1,   -1,  260,  261,  262,
  263,  264,  265,  266,  267,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   52,   -1,  277,   55,  279,   -1,  281,   -1,
   -1,   -1,   -1,   -1,  257,   -1,   -1,  260,  261,  262,
  263,  264,  265,  266,  267,   -1,  257,   -1,   -1,  260,
   -1,   -1,  263,   -1,  277,   -1,  279,  257,  281,   -1,
  260,   -1,   -1,  263,   -1,   -1,  277,   -1,   -1,   -1,
  281,   -1,   -1,   -1,   -1,   -1,  106,  277,   -1,   -1,
   -1,  281,   -1,   -1,   -1,   -1,  116,  117,  118,   -1,
  120,  121,   -1,   -1,  124,
};
}
final static short YYFINAL=2;
final static short YYMAXTOKEN=282;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,"'('","')'","'*'","'+'","','",
"'-'","'.'","'/'",null,null,null,null,null,null,null,null,null,null,"':'","';'",
"'<'","'='","'>'",null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
"'{'",null,"'}'",null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,"IF","ELSE","END_IF","PRINT","CLASS","VOID",
"ID","LONG","UINT","DOUBLE","STRING","CTE_LONG","CTE_UINT","CTE_DOUBLE",
"CTE_STRING","CMP_GE","CMP_LE","CMP_EQUAL","CMP_NOT_EQUAL","SUB_ASIGN","DO",
"UNTIL","IMPL","FOR","RETURN","TOD",
};
final static String yyrule[] = {
"$accept : programa",
"programa : '{' lista_sentencias '}'",
"programa : '{' '}'",
"comparador : CMP_GE",
"comparador : CMP_LE",
"comparador : CMP_EQUAL",
"comparador : CMP_NOT_EQUAL",
"comparador : '>'",
"comparador : '<'",
"condicion : expr comparador expr",
"tipo_basico : LONG",
"tipo_basico : UINT",
"tipo_basico : DOUBLE",
"tipo_basico : STRING",
"lista_identificadores : lista_identificadores ';' ID",
"lista_identificadores : ID",
"acceso_memoria : acceso_memoria '.' ID",
"acceso_memoria : ID",
"tipo_de_dato : tipo_basico",
"tipo_de_dato : ID",
"declaracion_variable : tipo_de_dato lista_identificadores ','",
"sentencia_declarativa : declaracion_variable",
"sentencia_declarativa : definicion_funcion ','",
"sentencia_declarativa : definicion_clase ','",
"sentencia_declarativa : implementacion ','",
"argumentos_reales : '(' ')'",
"argumentos_reales : '(' expr ')'",
"sentencia_ejecutable : acceso_memoria op_asignacion_aumentada expr ','",
"sentencia_ejecutable : acceso_memoria argumentos_reales ','",
"sentencia_ejecutable : sentencia_if ','",
"sentencia_ejecutable : do_until ','",
"sentencia_ejecutable : PRINT imprimible ','",
"sentencia_ejecutable : RETURN ','",
"imprimible : CTE_STRING",
"imprimible : CTE_UINT",
"imprimible : CTE_LONG",
"lista_sentencias : lista_sentencias sentencia_ejecutable",
"lista_sentencias : lista_sentencias sentencia_declarativa",
"lista_sentencias : sentencia_ejecutable",
"lista_sentencias : sentencia_declarativa",
"lista_sentencias_ejecutables : lista_sentencias_ejecutables sentencia_ejecutable",
"lista_sentencias_ejecutables : sentencia_ejecutable",
"op_asignacion_aumentada : '='",
"op_asignacion_aumentada : SUB_ASIGN",
"condicion_if_reserva : condicion",
"cuerpo_if : sentencia_ejecutable",
"cuerpo_if : '{' lista_sentencias_ejecutables '}'",
"cuerpo_else : sentencia_ejecutable",
"cuerpo_else : '{' lista_sentencias_ejecutables '}'",
"cuerpo_if_reserva : cuerpo_if",
"sentencia_if : IF '(' condicion_if_reserva ')' cuerpo_if END_IF",
"sentencia_if : IF '(' condicion_if_reserva ')' cuerpo_if_reserva ELSE cuerpo_else END_IF",
"sentencia_if : IF error END_IF ','",
"constante : CTE_UINT",
"constante : CTE_STRING",
"constante : CTE_DOUBLE",
"constante : CTE_LONG",
"constante : '-' CTE_LONG",
"constante : '-' CTE_DOUBLE",
"constante : '-' CTE_UINT",
"expr : basic_expr",
"expr : TOD '(' basic_expr ')'",
"basic_expr : expr '+' term",
"basic_expr : expr '-' term",
"basic_expr : term",
"term : term '*' factor",
"term : term '/' factor",
"term : factor",
"factor : acceso_memoria",
"factor : constante",
"definicion_funcion : procedimiento",
"id_ambito : ID",
"abrir_scope : '{'",
"cerrar_scope : '}'",
"procedimiento_args : '(' tipo_basico ID ')'",
"procedimiento_args : '(' ')'",
"procedimiento_cuerpo : abrir_scope cerrar_scope",
"procedimiento_cuerpo : abrir_scope lista_sentencias cerrar_scope",
"void_con_reserva : VOID",
"procedimiento : void_con_reserva id_ambito procedimiento_args procedimiento_cuerpo",
"procedimiento : VOID error '}'",
"cuerpo_do : sentencia_ejecutable",
"cuerpo_do : '{' lista_sentencias_ejecutables '}'",
"do_until : DO cuerpo_do UNTIL '(' condicion ')'",
"metodo : procedimiento",
"clase_con_nombre : CLASS id_ambito",
"definicion_clase : clase_con_nombre abrir_scope cuerpo_clase cerrar_scope",
"definicion_clase : clase_con_nombre abrir_scope cerrar_scope",
"cuerpo_clase : cuerpo_clase declaracion_variable",
"cuerpo_clase : declaracion_variable",
"cuerpo_clase : cuerpo_clase metodo ','",
"cuerpo_clase : metodo ','",
"cuerpo_clase : cuerpo_clase ID ','",
"cuerpo_clase : ID ','",
"implementacion_metodos : implementacion_metodos metodo ','",
"implementacion_metodos : metodo ','",
"id_implementacion : ID",
"implementacion_abrir_scope : '{'",
"implementacion_cerrar_scope : '}'",
"implementacion : IMPL FOR id_implementacion ':' implementacion_abrir_scope implementacion_metodos implementacion_cerrar_scope",
"implementacion : IMPL error '}'",
};

//#line 1415 "compiler.y"

void yyerror(String msg)
{
    System.out.println("ERROR! " + msg);
}

int yylex()
{
    return compiler.yylex();
}

public void setyylval(LocatedSymbolTableEntry tokensData)
{
    this.yylval = new ParserVal(tokensData);
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

public ListOfTriplets getListOfTriplets()
{
    return listOfTriplets;
}


DataType getTripletOperandDataType(TripletOperand o)
{
    if (o.isFinal())
        return (DataType)(o.getstEntry().getAttrib(AttribKey.DATA_TYPE));
    else
        return listOfTriplets.getTriplet(o.getIndex()).getType();
}


String _currentID;
LinkedList<String> _currentScope;
Compiler compiler;
SemanticHelper semanticHelper;
SymbolTable symbolTable;
String implementationMethodScope;
LinkedList<String> scopeCopy;
ListOfTriplets listOfTriplets;
SumCompatibilityTable sumCompatibilityTable;
MulCompatibilityTable mulCompatibilityTable;
DivCompatibilityTable divCompatibilityTable;
CompCompatibilityTable compCompatibilityTable;
String currentClassEntryKey;

public Parser(Compiler compiler)
{
    this.compiler = compiler;
    this._currentID = "";
    this._currentScope = new LinkedList<>();
    this.semanticHelper = compiler.getSemanticHelper();
    this.symbolTable = compiler.getSymbolTable();
    this.listOfTriplets = new ListOfTriplets();
    this.sumCompatibilityTable = new SumCompatibilityTable();
    this.mulCompatibilityTable = new MulCompatibilityTable();
    this.divCompatibilityTable = new DivCompatibilityTable();
    this.compCompatibilityTable = new CompCompatibilityTable();
    this.currentClassEntryKey = null;
    yydebug = false;
}
//#line 544 "Parser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  val_push(yylval);     //save empty value
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        if (yydebug) debug(" next yychar:"+yychar);
        //#### ERROR CHECK ####
        if (yychar < 0)    //it it didn't work/error
          {
          yychar = 0;      //change it to default string (no -1!)
          if (yydebug)
            yylexdebug(yystate,yychar);
          }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        if (yydebug)
          debug("state "+yystate+", shifting to state "+yytable[yyn]);
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
      if (yyerrflag==0)
        {
        yyerror("syntax error");
        yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0)   //check for under & overflow here
            {
            yyerror("stack underflow. aborting...");  //note lower case 's'
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            if (yydebug)
              debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            if (yydebug)
              debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0)   //check for under & overflow here
              {
              yyerror("Stack underflow. aborting...");  //capital 'S'
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        if (yydebug)
          {
          yys = null;
          if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          if (yys == null) yys = "illegal-symbol";
          debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          }
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    if (yydebug)
      debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    yyval = dup_yyval(yyval); //duplicate yyval if ParserVal is used as semantic value
    switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 3:
//#line 34 "compiler.y"
{
            YACCDataUnit data = new YACCDataUnit();
            data.lexeme = ">=";
            data.tokensData.add((LocatedSymbolTableEntry)val_peek(0).obj);
            yyval = new ParserVal(data);
        }
break;
case 4:
//#line 41 "compiler.y"
{
            YACCDataUnit data = new YACCDataUnit();
            data.lexeme = "<=";
            data.tokensData.add((LocatedSymbolTableEntry)val_peek(0).obj);
            yyval = new ParserVal(data);
        }
break;
case 5:
//#line 48 "compiler.y"
{
            YACCDataUnit data = new YACCDataUnit();
            data.lexeme = "==";
            data.tokensData.add((LocatedSymbolTableEntry)val_peek(0).obj);
            yyval = new ParserVal(data);
        }
break;
case 6:
//#line 55 "compiler.y"
{
            YACCDataUnit data = new YACCDataUnit();
            data.lexeme = "!!";
            data.tokensData.add((LocatedSymbolTableEntry)val_peek(0).obj);
            yyval = new ParserVal(data);
        }
break;
case 7:
//#line 62 "compiler.y"
{
            YACCDataUnit data = new YACCDataUnit();
            data.lexeme = ">";
            data.tokensData.add((LocatedSymbolTableEntry)val_peek(0).obj);
            yyval = new ParserVal(data);
        }
break;
case 8:
//#line 69 "compiler.y"
{
            YACCDataUnit data = new YACCDataUnit();
            data.lexeme = "<";
            data.tokensData.add((LocatedSymbolTableEntry)val_peek(0).obj);
            yyval = new ParserVal(data);
        }
break;
case 9:
//#line 79 "compiler.y"
{
            String compLexeme = ((YACCDataUnit)val_peek(1).obj).lexeme;

            YACCDataUnit data1 = (YACCDataUnit)val_peek(2).obj;
            YACCDataUnit data3 = (YACCDataUnit)val_peek(0).obj;

            Triplet triplet = semanticHelper.getTriplet(data1.tripletOperand, data3.tripletOperand, compLexeme, listOfTriplets, compCompatibilityTable);

            if (triplet.getType() == null)
            {
                compiler.reportSemanticError("No se pueden comparar tipos de datos", ((YACCDataUnit)val_peek(1).obj).tokensData.get(0).getLocation());
                break;
            }

            int tripletID = listOfTriplets.addTriplet(triplet);

            YACCDataUnit data = new YACCDataUnit();
            data.firstTriplet = tripletID;
            data.tripletQuantity = 1 + data1.tripletQuantity + data3.tripletQuantity;

            yyval = new ParserVal(data);
        }
break;
case 14:
//#line 112 "compiler.y"
{
            ((LinkedList<LocatedSymbolTableEntry>)(val_peek(2).obj)).add((LocatedSymbolTableEntry)val_peek(0).obj);
        }
break;
case 15:
//#line 116 "compiler.y"
{
            LinkedList<LocatedSymbolTableEntry> lista = new LinkedList<>();
            lista.add((LocatedSymbolTableEntry)val_peek(0).obj);
            yyval = new ParserVal(lista);
        }
break;
case 16:
//#line 125 "compiler.y"
{
            YACCDataUnit data1 = (YACCDataUnit)val_peek(2).obj;

            if (!data1.isValid())
            {
                yyval = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            /* Chequear que el ultimo elemento sea objeto*/

            String lastTokenLexeme = data1.tokensData.get(data1.tokensData.size() - 1).getSTEntry().getLexeme();
            TokenLocation lastTokenLocation = data1.tokensData.get(data1.tokensData.size() - 1).getLocation();

            SymbolTableEntry lastTokenEntry = symbolTable.getEntry(data1.referencedEntryKey);

            if (lastTokenEntry == null)
            {
                compiler.reportSemanticError(
                    "No se encuentra la clave: " + data1.referencedEntryKey, lastTokenLocation
                );

                yyval = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            if (lastTokenEntry.getAttrib(AttribKey.DATA_TYPE) != DataType.OBJECT)
            {
                compiler.reportSemanticError(
                    String.format("ID no es de tipo objeto: %s", lastTokenEntry.getLexeme()),
                    lastTokenLocation
                );

                yyval = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            /* Chequar que la clase tenga la propiedad*/

            String classEntryKey = (String)lastTokenEntry.getAttrib(AttribKey.INSTANCE_OF);

            String staticVarEntryKey = getSTEntry(val_peek(0)).getLexeme() + ":" + semanticHelper.invertScope(classEntryKey);

            SymbolTableEntry staticVarEntry = symbolTable.getEntry(staticVarEntryKey);

            if (staticVarEntry == null)
            {
                compiler.reportSemanticError(
                    String.format("No se encuentra el ID: %s dentro de la clase: %s", getSTEntry(val_peek(0)).getLexeme(), symbolTable.getEntry(classEntryKey).getLexeme()),
                    getTokenLocation(val_peek(0))
                );

                yyval = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            /* Si es de tipo funcion reemplazar el lexema con el nombre de la clase*/

            if (staticVarEntry.getAttrib(AttribKey.ID_TYPE) == IDType.FUNC_METHOD)
            {
                /* Reemplazar la referencia por el método en la clase*/

                data1.referencedEntryKey = staticVarEntryKey;
            }
            else
            {
                /* Buscar la referencia a la variable en si*/

                String propertyEntryKey = getSTEntry(val_peek(0)).getLexeme() + ":" + semanticHelper.invertScope(data1.referencedEntryKey);
                data1.referencedEntryKey = propertyEntryKey;
            }
            data1.tokensData.add((LocatedSymbolTableEntry)val_peek(0).obj);
            yyval = new ParserVal(data1);
        }
break;
case 17:
//#line 200 "compiler.y"
{
            /* Chequear que exista en el ambito local*/

            LocatedSymbolTableEntry tokenData = (LocatedSymbolTableEntry)val_peek(0).obj;
            String lexeme = tokenData.getSTEntry().getLexeme();

            String referencedEntryKey = semanticHelper.getEntryKeyByScope(lexeme, getCurrentScopeStr());

            if (referencedEntryKey == null)
            {
                compiler.reportSemanticError(
                    String.format("ID no encontrado: %s", lexeme),
                    getTokenLocation(val_peek(0))
                );

                yyval = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            /* SymbolTableEntry referencedEntry = symbolTable.getEntry(referencedEntryKey);*/

            YACCDataUnit data = new YACCDataUnit();
            data.tokensData.add((LocatedSymbolTableEntry)val_peek(0).obj);
            data.referencedEntryKey = referencedEntryKey;

            yyval = new ParserVal(data);
        }
break;
case 18:
//#line 231 "compiler.y"
{
            YACCDataUnit data = new YACCDataUnit();
            data.tokensData.add((LocatedSymbolTableEntry)val_peek(0).obj);
            yyval = new ParserVal(data);
        }
break;
case 19:
//#line 237 "compiler.y"
{
            List reservedDataTypeWords = Arrays.asList("uint", "double", "long", "string");

            if (reservedDataTypeWords.contains(getSTEntry(val_peek(0)).getLexeme().toLowerCase()))
            {
                compiler.reportSemanticError("Las palabras reservadas van en mayusculas", getTokenLocation(val_peek(0)));
                YACCDataUnit data = new YACCInvalidDataUnit();
                data.tokensData.add((LocatedSymbolTableEntry)val_peek(0).obj);
                yyval = new ParserVal(data);
                break;
            }

            /* Ver si es accesible y si es un token de clase*/

            String classEntryKey = semanticHelper.getEntryKeyByScope(getSTEntry(val_peek(0)).getLexeme(), getCurrentScopeStr());

            if (classEntryKey == null)
            {
                compiler.reportSemanticError("No se encuentra la clase: " + getSTEntry(val_peek(0)).getLexeme(), getTokenLocation(val_peek(0)));
                YACCDataUnit data = new YACCInvalidDataUnit();
                data.tokensData.add((LocatedSymbolTableEntry)val_peek(0).obj);
                yyval = new ParserVal(data);
                break;
            }

            SymbolTableEntry classEntry = symbolTable.getEntry(classEntryKey);

            if (classEntry.getAttrib(AttribKey.ID_TYPE) != IDType.CLASSNAME)
            {
                compiler.reportSemanticError("El ID: " + getSTEntry(val_peek(0)).getLexeme() + " no es una clase definida", getTokenLocation(val_peek(0)));
                YACCDataUnit data = new YACCInvalidDataUnit();
                data.tokensData.add((LocatedSymbolTableEntry)val_peek(0).obj);
                yyval = new ParserVal(data);
                break;
            }

            getSTEntry(val_peek(0)).setAttrib(AttribKey.DATA_TYPE, DataType.OBJECT);
            YACCDataUnit data = new YACCDataUnit();
            data.tokensData.add((LocatedSymbolTableEntry)val_peek(0).obj);
            yyval = new ParserVal(data);
        }
break;
case 20:
//#line 283 "compiler.y"
{
            YACCDataUnit data1 = (YACCDataUnit)val_peek(2).obj;

            if (!data1.isValid())
            {
                yyval = new ParserVal(data1);
                break;
            }

            LinkedList<String> lexemeList = new LinkedList<>();

            for (LocatedSymbolTableEntry tokenData : (LinkedList<LocatedSymbolTableEntry>)(val_peek(1).obj))
                lexemeList.add(tokenData.getSTEntry().getLexeme());

            /* Si es una declaracion dentro de clase, agregarla a la lista*/

            if (this.currentClassEntryKey != null)
            {
                SymbolTableEntry classEntry = symbolTable.getEntry(currentClassEntryKey);
                HashSet<String> attribsSet = (HashSet<String>)(classEntry.getAttrib(AttribKey.ATTRIBS_SET));

                for (String lexeme : lexemeList)
                    attribsSet.add(lexeme + ":" + getCurrentScopeStr());
            }

            semanticHelper.declareRecursive(lexemeList, getCurrentScopeStr(), data1.tokensData.get(0).getSTEntry(), currentClassEntryKey, false);

            YACCDataUnit data = new YACCDataUnit();

            data.tokensData.add(data1.tokensData.get(0));

            yyval = new ParserVal(data);
        }
break;
case 21:
//#line 320 "compiler.y"
{
            YACCDataUnit data1 = (YACCDataUnit)val_peek(0).obj;

            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Declaración de variable/s", data1.tokensData.get(0).getLocation())
            );
        }
break;
case 22:
//#line 328 "compiler.y"
{
            YACCDataUnit data1 = (YACCDataUnit)val_peek(1).obj;

            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Definición de función", data1.tokensData.get(0).getLocation())
            );
        }
break;
case 23:
//#line 336 "compiler.y"
{
            YACCDataUnit data1 = (YACCDataUnit)val_peek(1).obj;

            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Definición de clase", data1.tokensData.get(0).getLocation())
            );
        }
break;
case 24:
//#line 344 "compiler.y"
{
            YACCDataUnit data1 = (YACCDataUnit)val_peek(1).obj;

            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Sentencia IMPL", data1.tokensData.get(0).getLocation())
            );
        }
break;
case 25:
//#line 355 "compiler.y"
{
            YACCDataUnit data = new YACCDataUnit();
            yyval = new ParserVal(data);
        }
break;
case 26:
//#line 360 "compiler.y"
{
            YACCDataUnit data2 = (YACCDataUnit)val_peek(1).obj;

            if (!data2.isValid())
            {
                yyval = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            YACCDataUnit data = new YACCDataUnit();
            data.tripletOperand = data2.tripletOperand;

            yyval = new ParserVal(data);
        }
break;
case 27:
//#line 378 "compiler.y"
{
            /* Chequar que data1 y data3 no tiren error*/

            YACCDataUnit data1 = (YACCDataUnit)val_peek(3).obj;
            YACCDataUnit data2 = (YACCDataUnit)val_peek(2).obj;
            YACCDataUnit data3 = (YACCDataUnit)val_peek(1).obj;

            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Asignacion", data2.tokensData.get(0).getLocation())
            );

            if (!data1.isValid() || !data2.isValid() || !data3.isValid())
            {
                yyval = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            String operation = data2.lexeme;

            String referencedEntryKey = data1.referencedEntryKey;
            SymbolTableEntry referencedEntry = symbolTable.getEntry(referencedEntryKey);

            /* Chequar que sea una variable*/

            if (referencedEntry.getAttrib(AttribKey.ID_TYPE) != IDType.VAR_ATTRIB)
            {
                LocatedSymbolTableEntry lastAttrib = data1.tokensData.get(data1.tokensData.size() - 1);
                compiler.reportSemanticError(String.format(
                    "El ID: %s no es una variable/atributo",
                    referencedEntry.getLexeme()
                ), lastAttrib.getLocation());

                yyval = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            /* Chequar tipos y demas*/

            DataType leftDataType = (DataType)referencedEntry.getAttrib(AttribKey.DATA_TYPE);
            TripletOperand rightTripletOperand = data3.tripletOperand;

            TripletOperand equalizeTo;

            int firstTriplet = -1;

            if (operation.equals("="))
            {
                equalizeTo = rightTripletOperand;
            }
            else
            {
                Triplet subTriplet = new Triplet("-", new TripletOperand(referencedEntry), rightTripletOperand);
                int tripletID = listOfTriplets.addTriplet(subTriplet);
                equalizeTo = new TripletOperand(tripletID);
                firstTriplet = tripletID;
            }

            Triplet equalTriplet = new Triplet("=", new TripletOperand(referencedEntry), equalizeTo);
            int tripletID = listOfTriplets.addTriplet(equalTriplet);

            if (firstTriplet == -1)
                firstTriplet = tripletID;

            YACCDataUnit data = new YACCDataUnit();
            data.firstTriplet = firstTriplet;
            data.tripletQuantity = (operation.equals("=") ? 1 : 2) + data3.tripletQuantity;

            yyval = new ParserVal(data);
        }
break;
case 28:
//#line 448 "compiler.y"
{
            YACCDataUnit data1 = (YACCDataUnit)val_peek(2).obj;
            YACCDataUnit data2 = (YACCDataUnit)val_peek(1).obj;

            if (!data1.isValid() || !data2.isValid())
            {
                yyval = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            String functionName = data1.getLastTokenData().getSTEntry().getLexeme();
            TokenLocation functionTokenLocation = data1.getLastTokenData().getLocation();

            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Invocación a método", functionTokenLocation)
            );

            SymbolTableEntry referencedEntry = symbolTable.getEntry(data1.referencedEntryKey);

            if (referencedEntry.getAttrib(AttribKey.ID_TYPE) != IDType.FUNC_METHOD)
            {
                compiler.reportSemanticError(String.format("El identificador '%s' no es ejecutable", functionName), functionTokenLocation);

                yyval = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            /* Chequear el parametro*/

            boolean funcHasArgument = referencedEntry.getAttrib(AttribKey.ARG_TYPE) != null;
            boolean argumentWasPassed = data2.tripletOperand != null;

            if (funcHasArgument ^ argumentWasPassed)
            {
                compiler.reportSemanticError(String.format("La función '%s' espera: %d argumentos", functionName, funcHasArgument ? 1 : 0), functionTokenLocation);

                yyval = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            if (funcHasArgument)
            {
                /* Chequear que coincidan los tipos*/

                DataType exprDataType = null;

                if (data2.tripletOperand.isFinal())
                    exprDataType = (DataType)data2.tripletOperand.stEntry.getAttrib(AttribKey.DATA_TYPE);
                else
                    exprDataType = listOfTriplets.getTriplet(data2.tripletOperand.index).getType();

                if (referencedEntry.getAttrib(AttribKey.ARG_TYPE) != exprDataType)
                {
                    compiler.reportSemanticError(String.format("El tipo de dato no coincide"), functionTokenLocation);

                    yyval = new ParserVal(new YACCInvalidDataUnit());
                    break;
                }
            }

            /* Agregar terceto de invocacion*/

            Triplet invokeTriplet = new Triplet(
                "INVOKE",
                new TripletOperand(referencedEntry),
                data2.tripletOperand
            );

            int tripletID = listOfTriplets.addTriplet(invokeTriplet);

            YACCDataUnit data = new YACCDataUnit();
            data.tripletQuantity = 1;
            data.firstTriplet = tripletID;

            yyval = new ParserVal(data);
        }
break;
case 29:
//#line 525 "compiler.y"
{
            YACCDataUnit data1 = (YACCDataUnit)val_peek(1).obj;

            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Sentencia IF", data1.tokensData.get(0).getLocation())
            );
        }
break;
case 30:
//#line 533 "compiler.y"
{
            YACCDataUnit data1 = (YACCDataUnit)val_peek(1).obj;

            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Estructura DO UNTIL", data1.tokensData.get(0).getLocation())
            );
        }
break;
case 31:
//#line 541 "compiler.y"
{
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Sentencia PRINT", getTokenLocation(val_peek(2)))
            );

            Triplet triplet = new Triplet("PRINT", new TripletOperand(getSTEntry(val_peek(1))), null);
            int tripletID = listOfTriplets.addTriplet(triplet);

            YACCDataUnit data = new YACCDataUnit();
            data.firstTriplet = tripletID;
            data.tripletQuantity = 1;
            data.tokensData.add((LocatedSymbolTableEntry)val_peek(2).obj);

            yyval = new ParserVal(data);
        }
break;
case 32:
//#line 557 "compiler.y"
{
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Sentencia RETURN", getTokenLocation(val_peek(1)))
            );

            Triplet t = new Triplet("RETURN", null, null);
            int tripletID = listOfTriplets.addTriplet(t);

            YACCDataUnit data = new YACCDataUnit();
            data.tripletQuantity = 1;
            data.tokensData.add((LocatedSymbolTableEntry)val_peek(1).obj);
            data.firstTriplet = tripletID;

            yyval = new ParserVal(data);
        }
break;
case 36:
//#line 582 "compiler.y"
{
            YACCDataUnit data1 = (YACCDataUnit)val_peek(1).obj;
            YACCDataUnit data2 = (YACCDataUnit)val_peek(0).obj;

            YACCDataUnit data = new YACCDataUnit();

            data.tripletQuantity = data1.tripletQuantity + data2.tripletQuantity;
            data.firstTriplet = (data1.firstTriplet != null ? data1.firstTriplet : data2.firstTriplet);

            yyval = new ParserVal(data);
        }
break;
case 37:
//#line 594 "compiler.y"
{
            YACCDataUnit data1 = (YACCDataUnit)val_peek(1).obj;
            YACCDataUnit data2 = (YACCDataUnit)val_peek(0).obj;

            YACCDataUnit data = new YACCDataUnit();

            data.tripletQuantity = data1.tripletQuantity + data2.tripletQuantity;
            data.firstTriplet = (data1.firstTriplet != null ? data1.firstTriplet : data2.firstTriplet);

            yyval = new ParserVal(data);
        }
break;
case 40:
//#line 611 "compiler.y"
{
            YACCDataUnit data1 = (YACCDataUnit)val_peek(1).obj;
            YACCDataUnit data2 = (YACCDataUnit)val_peek(0).obj;

            YACCDataUnit data = new YACCDataUnit();
            data.tripletQuantity = data1.tripletQuantity + data2.tripletQuantity;
            data.firstTriplet = (data1.firstTriplet != null ? data1.firstTriplet : data2.firstTriplet);

            yyval = new ParserVal(data);
        }
break;
case 42:
//#line 626 "compiler.y"
{
            YACCDataUnit data = new YACCDataUnit();
            data.lexeme = "=";
            data.tokensData.add((LocatedSymbolTableEntry)val_peek(0).obj);
            yyval = new ParserVal(data);
        }
break;
case 43:
//#line 633 "compiler.y"
{
            YACCDataUnit data = new YACCDataUnit();
            data.lexeme = "-=";
            data.tokensData.add((LocatedSymbolTableEntry)val_peek(0).obj);
            yyval = new ParserVal(data);
        }
break;
case 44:
//#line 643 "compiler.y"
{
            /* Agrega el terceto del jump condicional*/

            Triplet triplet = new Triplet("JZ", null, null);
            int tripletID = listOfTriplets.addTriplet(triplet);

            YACCDataUnit data = (YACCDataUnit)val_peek(0).obj;
            data.reservedTriplet = tripletID;

            yyval = new ParserVal(data);
        }
break;
case 46:
//#line 659 "compiler.y"
{

            yyval = val_peek(1);
        }
break;
case 48:
//#line 668 "compiler.y"
{
            yyval = val_peek(1);
        }
break;
case 49:
//#line 675 "compiler.y"
{
            YACCDataUnit data = (YACCDataUnit)val_peek(0).obj;
            /* Agrega el terceto del jump incondicional*/

            Triplet triplet = new Triplet("JMP", null, null);
            int tripletID = listOfTriplets.addTriplet(triplet);

            data.tripletQuantity += 1;
            data.reservedTriplet = tripletID;

            yyval = new ParserVal(data);
        }
break;
case 50:
//#line 691 "compiler.y"
{
            YACCDataUnit data3 = (YACCDataUnit)val_peek(3).obj;
            YACCDataUnit data5 = (YACCDataUnit)val_peek(1).obj;

            int jzToBackpatch = data3.reservedTriplet;

            listOfTriplets.replaceTriplet(
                jzToBackpatch,
                new Triplet(
                    "JZ",
                    new TripletOperand(1 + jzToBackpatch + data5.tripletQuantity),
                    null
                )
            );

            YACCDataUnit data = new YACCDataUnit();
            data.tripletQuantity = 1 + data3.tripletQuantity + data5.tripletQuantity;
            data.tokensData.add((LocatedSymbolTableEntry)val_peek(5).obj);
            data.firstTriplet = data3.reservedTriplet;

            yyval = new ParserVal(data);
        }
break;
case 51:
//#line 714 "compiler.y"
{
            YACCDataUnit data3 = (YACCDataUnit)val_peek(5).obj;
            YACCDataUnit data5 = (YACCDataUnit)val_peek(3).obj;
            YACCDataUnit data7 = (YACCDataUnit)val_peek(1).obj;

            int jzToBackpatch = data3.reservedTriplet;

            listOfTriplets.replaceTriplet(
                jzToBackpatch,
                new Triplet(
                    "JZ",
                    new TripletOperand(1 + jzToBackpatch + data5.tripletQuantity),
                    null
                )
            );

            int jmpToBackpatch = data5.reservedTriplet;

            listOfTriplets.replaceTriplet(
                jmpToBackpatch,
                new Triplet(
                    "JMP",
                    new TripletOperand(1 + jmpToBackpatch + data7.tripletQuantity),
                    null
                )
            );

            YACCDataUnit data = new YACCDataUnit();
            data.tripletQuantity = 2 + data3.tripletQuantity + data5.tripletQuantity + data7.tripletQuantity;
            data.tokensData.add((LocatedSymbolTableEntry)val_peek(7).obj);
            data.firstTriplet = data3.reservedTriplet;

            yyval = new ParserVal(data);
        }
break;
case 52:
//#line 749 "compiler.y"
{
            compiler.reportSyntaxError("Error en IF", getTokenLocation(val_peek(3)));

            YACCDataUnit data = new YACCInvalidDataUnit();
            data.tokensData.add((LocatedSymbolTableEntry)val_peek(3).obj);
            yyval = new ParserVal(data);
        }
break;
case 56:
//#line 763 "compiler.y"
{
            if (!ConstantRange.isValidLONG(getSTEntry(val_peek(0)).getLexeme(), false))
                compiler.reportLexicalError("El rango de LONG es [-2147483648, 2147483647]", getTokenLocation(val_peek(0)));
        }
break;
case 57:
//#line 768 "compiler.y"
{
            getSTEntry(val_peek(0)).addNegativeSign();
        }
break;
case 58:
//#line 772 "compiler.y"
{
            getSTEntry(val_peek(0)).addNegativeSign();
        }
break;
case 59:
//#line 776 "compiler.y"
{
            compiler.reportLexicalError("Las constantes tipo UINT no pueden ser negativas", getTokenLocation(val_peek(1)));
        }
break;
case 61:
//#line 784 "compiler.y"
{
            YACCDataUnit data3 = (YACCDataUnit)val_peek(1).obj;

            if (!data3.isValid())
            {
                data3.setValid(false);
                yyval = new ParserVal(data3);
                break;
            }

            DataType exprDataType = getTripletOperandDataType(data3.tripletOperand);

            if (exprDataType == DataType.DOUBLE)
            {
                compiler.generateWarning("Se elimina TOD innecesario", getTokenLocation(val_peek(3)));
                yyval = new ParserVal(data3);
                break;
            }

            data3.tripletQuantity++;

            String tripletOP = null;

            switch (exprDataType)
            {
                case UINT:
                    tripletOP = "UITOD";
                    break;
                case LONG:
                    tripletOP = "LTOD";
                    break;
            }

            if (tripletOP == null)
            {
                compiler.reportSemanticError(
                    "No se puede convertir el tipo: " + exprDataType + " a DOUBLE",
                    getTokenLocation(val_peek(2))
                );
                data3.setValid(false);
                yyval = new ParserVal(data3);
                break;
            }

            Triplet convTriplet = new Triplet(tripletOP, data3.tripletOperand, null, DataType.DOUBLE);
            int tripletID = listOfTriplets.addTriplet(convTriplet);

            data3.tripletOperand = new TripletOperand(tripletID);
            data3.dataType = DataType.DOUBLE;

            yyval = new ParserVal(data3);
        }
break;
case 62:
//#line 840 "compiler.y"
{
            YACCDataUnit data1 = (YACCDataUnit)val_peek(2).obj;
            YACCDataUnit data3 = (YACCDataUnit)val_peek(0).obj;

            TripletOperand operand1 = data1.tripletOperand;
            TripletOperand operand2 = data3.tripletOperand;

            Triplet t = semanticHelper.getTriplet(operand1, operand2, "+", listOfTriplets, sumCompatibilityTable);

            if (t.getType() == null){
                compiler.reportSemanticError("No se pueden sumar variables de distinto tipo", getTokenLocation(val_peek(1)));
            }

            DataType type;
                
            if (operand1.isFinal()) {
                type = (DataType)operand1.getstEntry().getAttrib(AttribKey.DATA_TYPE);
            } else {
                type = listOfTriplets.getTriplet(operand1.getIndex()).getType();
            }

            t.setDataType(type);

            int tripletIndex = listOfTriplets.addTriplet(t);

            YACCDataUnit data = new YACCDataUnit();
            data.tripletQuantity = 1 + data1.tripletQuantity + data3.tripletQuantity;
            data.tripletOperand = new TripletOperand(tripletIndex);

            yyval = new ParserVal(data);
        }
break;
case 63:
//#line 872 "compiler.y"
{
            YACCDataUnit data1 = (YACCDataUnit)val_peek(2).obj;
            YACCDataUnit data3 = (YACCDataUnit)val_peek(0).obj;

            TripletOperand operand1 = data1.tripletOperand;
            TripletOperand operand2 = data3.tripletOperand;

            Triplet t = semanticHelper.getTriplet(operand1, operand2, "-", listOfTriplets, sumCompatibilityTable);

            if (t.getType() == null){
                compiler.reportSemanticError("No se pueden restar variables de distinto tipo", getTokenLocation(val_peek(1)));
            }

            DataType type;
                
            if (operand1.isFinal()) {
                type = (DataType)operand1.getstEntry().getAttrib(AttribKey.DATA_TYPE);
            } else {
                type = listOfTriplets.getTriplet(operand1.getIndex()).getType();
            }

            t.setDataType(type);

            int tripletIndex = listOfTriplets.addTriplet(t);

            YACCDataUnit data = new YACCDataUnit();
            data.tripletQuantity = 1 + data1.tripletQuantity + data3.tripletQuantity;
            data.tripletOperand = new TripletOperand(tripletIndex);

            yyval = new ParserVal(data);
        }
break;
case 65:
//#line 908 "compiler.y"
{
            YACCDataUnit data1 = (YACCDataUnit)val_peek(2).obj;
            YACCDataUnit data3 = (YACCDataUnit)val_peek(0).obj;

            TripletOperand operand1 = data1.tripletOperand;
            TripletOperand operand2 = data3.tripletOperand;

            Triplet t = semanticHelper.getTriplet(operand1, operand2, "*", listOfTriplets, mulCompatibilityTable);

            if (t.getType() == null){
                compiler.reportSemanticError("No se pueden multiplcar variables de distinto tipo", getTokenLocation(val_peek(1)));
            }

            DataType type;
                
            if (operand1.isFinal()) {
                type = (DataType)operand1.getstEntry().getAttrib(AttribKey.DATA_TYPE);
            } else {
                type = listOfTriplets.getTriplet(operand1.getIndex()).getType();
            }

            t.setDataType(type);

            int tripletIndex = listOfTriplets.addTriplet(t);

            YACCDataUnit data = new YACCDataUnit();
            data.tripletQuantity = 1 + data1.tripletQuantity;
            data.tripletOperand = new TripletOperand(tripletIndex);

            yyval = new ParserVal(data);
        }
break;
case 66:
//#line 940 "compiler.y"
{
            YACCDataUnit data1 = (YACCDataUnit)val_peek(2).obj;
            YACCDataUnit data3 = (YACCDataUnit)val_peek(0).obj;

            TripletOperand operand1 = data1.tripletOperand;
            TripletOperand operand2 = data3.tripletOperand;

            Triplet t = semanticHelper.getTriplet(operand1, operand2, "/", listOfTriplets, divCompatibilityTable);

            if (t.getType() == null){
                compiler.reportSemanticError("No se pueden dividir variables de distinto tipo", getTokenLocation(val_peek(1)));
            }
            DataType type;
                
            if (operand1.isFinal()) {
                type = (DataType)operand1.getstEntry().getAttrib(AttribKey.DATA_TYPE);
            } else {
                type = listOfTriplets.getTriplet(operand1.getIndex()).getType();
            }

            t.setDataType(type);

            int tripletIndex = listOfTriplets.addTriplet(t);

            YACCDataUnit data = new YACCDataUnit();
            data.tripletQuantity = 1 + data1.tripletQuantity;
            data.tripletOperand = new TripletOperand(tripletIndex);

            yyval = new ParserVal(data);
        }
break;
case 68:
//#line 975 "compiler.y"
{
            YACCDataUnit data1 = (YACCDataUnit)val_peek(0).obj;

            if (!data1.isValid())
            {
                yyval = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            /* Chequear que acceso_memoria sea una variable*/

            String referencedEntryKey = data1.referencedEntryKey;
            SymbolTableEntry referencedEntry = symbolTable.getEntry(referencedEntryKey);

            if (referencedEntry.getAttrib(AttribKey.ID_TYPE) != IDType.VAR_ATTRIB)
            {
                compiler.reportSemanticError("Se espera una variable", null);

                yyval = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            YACCDataUnit data = new YACCDataUnit();
            data.tripletOperand = new TripletOperand(referencedEntry);
            yyval = new ParserVal(data);
        }
break;
case 69:
//#line 1002 "compiler.y"
{
            YACCDataUnit data = new YACCDataUnit();
            data.tripletOperand = new TripletOperand(getSTEntry(val_peek(0)));
            yyval = new ParserVal(data);
        }
break;
case 71:
//#line 1015 "compiler.y"
{
            setCurrentID(getSTEntry(val_peek(0)).getLexeme());
        }
break;
case 72:
//#line 1022 "compiler.y"
{
            addToCurrentScope(getCurrentID());
        }
break;
case 73:
//#line 1029 "compiler.y"
{
            removeScope();
        }
break;
case 74:
//#line 1037 "compiler.y"
{
            YACCDataUnit data = new YACCDataUnit();

            data.tokensData.add((LocatedSymbolTableEntry)val_peek(2).obj);
            data.tokensData.add((LocatedSymbolTableEntry)val_peek(1).obj);

            yyval = new ParserVal(data);
        }
break;
case 75:
//#line 1046 "compiler.y"
{
            yyval = new ParserVal(new YACCDataUnit());
        }
break;
case 76:
//#line 1053 "compiler.y"
{
            yyval = new ParserVal(new YACCDataUnit());
        }
break;
case 77:
//#line 1057 "compiler.y"
{
            yyval = val_peek(1);
        }
break;
case 78:
//#line 1065 "compiler.y"
{
            Triplet t = new Triplet(null, null, null);
            int tripletID = listOfTriplets.addTriplet(t);

            YACCDataUnit data = new YACCDataUnit();
            data.reservedTriplet = tripletID;
            data.tokensData.add((LocatedSymbolTableEntry)val_peek(0).obj);

            yyval = new ParserVal(data);
        }
break;
case 79:
//#line 1079 "compiler.y"
{
            YACCDataUnit data1 = (YACCDataUnit)val_peek(3).obj;
            YACCDataUnit data3 = (YACCDataUnit)val_peek(1).obj;
            YACCDataUnit data4 = (YACCDataUnit)val_peek(0).obj;

            if (!data3.isValid() || !data4.isValid())
            {
                YACCDataUnit data = new YACCInvalidDataUnit();
                data.tokensData.add((LocatedSymbolTableEntry)val_peek(3).obj);
                yyval = new ParserVal(data);
                break;
            }

            System.out.println("La cantidad de tercetos dentro de la funcion es: " + data4.tripletQuantity);

            String funcLexeme = getSTEntry(val_peek(2)).getLexeme();

            String funcEntryKey = funcLexeme + ":" + getCurrentScopeStr();

            if (semanticHelper.alreadyDeclaredInScope(funcLexeme, getCurrentScopeStr()))
            {
                compiler.reportSemanticError("Identificador ya declarado en el ámbito local", getTokenLocation(val_peek(2)));
                yyval = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            boolean hasArgument = data3.tokensData.size() > 0;

            LocatedSymbolTableEntry argDataType = hasArgument ? data3.tokensData.get(0) : null;
            LocatedSymbolTableEntry argName = hasArgument ? data3.tokensData.get(1) : null;

            semanticHelper.declareFunction(getCurrentScopeStr(), val_peek(2).obj, argDataType);

            if (hasArgument)
            {
                /* Declarar la variable del argumento en el scope del procedimiento*/

                String scopeAdentro = getCurrentScopeStr() + ":" + getSTEntry(val_peek(2)).getLexeme();
                semanticHelper.declareArg(scopeAdentro, argName, argDataType);
            }

            YACCDataUnit data = new YACCDataUnit();
            data.tokensData.add(data1.tokensData.get(0));
            data.tripletQuantity = data4.tripletQuantity;
            data.referencedEntryKey = funcLexeme + ":" + getCurrentScopeStr();

            /* Backpatch del salto*/

            listOfTriplets.replaceTriplet(data1.reservedTriplet, new Triplet(
                "JMP",
                new TripletOperand(1 + data1.reservedTriplet + data.tripletQuantity),
                null
            ));

            Triplet lastTriplet = listOfTriplets.getLastTriplet();

            if (lastTriplet.getOperation().equals("RETURN") == false)
            {
                /* Entonces agregar un RETURN implicitamente*/

                Triplet returnTriplet = new Triplet("RETURN", null, null);
                listOfTriplets.addTriplet(returnTriplet);
                System.out.println("Se agrego un terceto return implicitamente");
            }

            /* Agregar a la tabla de simbolos el terceto donde empieza la funcion*/

            symbolTable.getEntry(funcEntryKey).setAttrib(AttribKey.FIRST_TRIPLET, data4.firstTriplet);

            yyval = new ParserVal(data);
        }
break;
case 80:
//#line 1151 "compiler.y"
{
            compiler.reportSyntaxError("Error en funcion/metodo", getTokenLocation(val_peek(2)));

            YACCDataUnit data = new YACCInvalidDataUnit();
            data.tokensData.add((LocatedSymbolTableEntry)val_peek(2).obj);

            yyval = new ParserVal(data);
        }
break;
case 82:
//#line 1165 "compiler.y"
{
            yyval = val_peek(1);
        }
break;
case 83:
//#line 1173 "compiler.y"
{
            YACCDataUnit data2 = (YACCDataUnit)val_peek(4).obj;
            YACCDataUnit data5 = (YACCDataUnit)val_peek(1).obj;

            /* Agregar salto condicional*/

            int bodyStartTriplet = data2.firstTriplet;

            Triplet triplet = new Triplet("JNZ", new TripletOperand(bodyStartTriplet), null);
            int tripletID = listOfTriplets.addTriplet(triplet);

            YACCDataUnit data = new YACCDataUnit();
            data.tokensData.add((LocatedSymbolTableEntry)val_peek(5).obj);
            data.tripletQuantity = 1 + data2.tripletQuantity + data5.tripletQuantity;
            data.firstTriplet = tripletID;

            yyval = new ParserVal(data);
        }
break;
case 85:
//#line 1200 "compiler.y"
{
            boolean success = semanticHelper.declareClass(getCurrentScopeStr(), (LocatedSymbolTableEntry)val_peek(0).obj);

            if (!success)
            {
                yyval = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            String className = getSTEntry(val_peek(0)).getLexeme();

            this.currentClassEntryKey = className + ":" + getCurrentScopeStr();

            YACCDataUnit data = new YACCDataUnit();
            data.referencedEntryKey = currentClassEntryKey;
            data.tokensData.add((LocatedSymbolTableEntry)val_peek(1).obj);
            data.tokensData.add((LocatedSymbolTableEntry)val_peek(0).obj);

            yyval = new ParserVal(data);
        }
break;
case 86:
//#line 1224 "compiler.y"
{
            this.currentClassEntryKey = null;

            YACCDataUnit data1 = (YACCDataUnit)val_peek(3).obj;

            if (!data1.isValid())
            {
                yyval = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            yyval = new ParserVal(data1);
        }
break;
case 87:
//#line 1238 "compiler.y"
{
            this.currentClassEntryKey = null;

            YACCDataUnit data1 = (YACCDataUnit)val_peek(2).obj;

            if (!data1.isValid())
            {
                yyval = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            yyval = new ParserVal(data1);
        }
break;
case 88:
//#line 1255 "compiler.y"
{
            YACCDataUnit data1 = (YACCDataUnit)val_peek(1).obj;

            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Declaracion de atributo", data1.tokensData.get(0).getLocation())
            );
        }
break;
case 89:
//#line 1263 "compiler.y"
{
            YACCDataUnit data1 = (YACCDataUnit)val_peek(0).obj;

            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Declaracion de atributo", data1.tokensData.get(0).getLocation())
            );
        }
break;
case 90:
//#line 1271 "compiler.y"
{
            YACCDataUnit data2 = (YACCDataUnit)val_peek(1).obj;

            if (!data2.isValid())
            {
                yyval = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            SymbolTableEntry currentClass = symbolTable.getEntry(currentClassEntryKey);

            Set<String> methodsSet = (Set<String>)(currentClass.getAttrib(AttribKey.METHODS_SET));

            methodsSet.add(data2.referencedEntryKey);

            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Implementacion de metodo dentro de clase", data2.tokensData.get(0).getLocation())
            );
        }
break;
case 91:
//#line 1291 "compiler.y"
{
            YACCDataUnit data1 = (YACCDataUnit)val_peek(1).obj;

            if (!data1.isValid())
            {
                yyval = new ParserVal(new YACCInvalidDataUnit());
                break;
            }

            SymbolTableEntry currentClass = symbolTable.getEntry(currentClassEntryKey);

            Set<String> methodsSet = (Set<String>)(currentClass.getAttrib(AttribKey.METHODS_SET));

            methodsSet.add(data1.referencedEntryKey);

            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Implementacion de metodo dentro de clase", data1.tokensData.get(0).getLocation())
            );
        }
break;
case 92:
//#line 1311 "compiler.y"
{
            String composLexeme = getSTEntry(val_peek(1)).getLexeme();

            semanticHelper.declareCompos((LocatedSymbolTableEntry)val_peek(1).obj, getCurrentScopeStr(), currentClassEntryKey);

            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Herencia por composicion", getTokenLocation(val_peek(1)))
            );
        }
break;
case 93:
//#line 1321 "compiler.y"
{
            String composLexeme = getSTEntry(val_peek(1)).getLexeme();

            semanticHelper.declareCompos((LocatedSymbolTableEntry)val_peek(1).obj, getCurrentScopeStr(), currentClassEntryKey);

            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Herencia por composicion", getTokenLocation(val_peek(1)))
            );
        }
break;
case 94:
//#line 1334 "compiler.y"
{
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Método distribuido", getTokenLocation(val_peek(1)))
            );
        }
break;
case 95:
//#line 1340 "compiler.y"
{
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Método distribuido", getTokenLocation(val_peek(1)))
            );
        }
break;
case 96:
//#line 1349 "compiler.y"
{
            /* Chequear alcance y tipo de ID*/

            String entryKey = semanticHelper.getEntryKeyByScope(getSTEntry(val_peek(0)).getLexeme(), getCurrentScopeStr());

            SymbolTableEntry referredSTEntry = (entryKey != null) ? symbolTable.getEntry(entryKey) : null;

            if (referredSTEntry == null)
            {
                compiler.reportSemanticError("No se encuentra la clase: " + getSTEntry(val_peek(0)).getLexeme(), getTokenLocation(val_peek(0)));
            }
            else
            {
                if (referredSTEntry.getAttrib(AttribKey.ID_TYPE) != IDType.CLASSNAME)
                    compiler.reportSemanticError("El identificador " + referredSTEntry.getLexeme() + "no es de tipo CLASE", getTokenLocation(val_peek(0)));
                else
                {
                    String referredClassScope = semanticHelper.removeLexemeFromKey(entryKey);

                    /*
                        El scope del metodo sera el scope de la clase + la clase en si misma
                    */

                    String methodScope = referredClassScope + ":" + getSTEntry(val_peek(0)).getLexeme();

                    this.implementationMethodScope = methodScope;
                }
            }
        }
break;
case 97:
//#line 1382 "compiler.y"
{
            /* Guardar el current scope*/
            this.scopeCopy = (LinkedList<String>)(this._currentScope.clone());
            /* Reemplazar por el scope de la clase referenciada*/
            this._currentScope = semanticHelper.scopeStrToList(this.implementationMethodScope);
        }
break;
case 98:
//#line 1392 "compiler.y"
{
            /* Recuperar el scope antes del IMPL*/
            this._currentScope = this.scopeCopy;
        }
break;
case 99:
//#line 1400 "compiler.y"
{
            YACCDataUnit data = new YACCDataUnit();
            data.tokensData.add((LocatedSymbolTableEntry)val_peek(6).obj);
            yyval = new ParserVal(data);
        }
break;
case 100:
//#line 1406 "compiler.y"
{
            compiler.reportSyntaxError("Error en implementación distribuida", getTokenLocation(val_peek(2)));
            YACCDataUnit data = new YACCDataUnit();
            data.tokensData.add((LocatedSymbolTableEntry)val_peek(2).obj);
            yyval = new ParserVal(data);
        }
break;
//#line 2073 "Parser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      if (yydebug) debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        if (yychar<0) yychar=0;  //clean, if necessary
        if (yydebug)
          yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      if (yydebug) debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
/**
 * A default run method, used for operating this parser
 * object in the background.  It is intended for extending Thread
 * or implementing Runnable.  Turn off with -Jnorun .
 */
public void run()
{
  yyparse();
}
//## end of method run() ########################################



//## Constructors ###############################################
//## The -Jnoconstruct option was used ##
//###############################################################



}
//################### END OF CLASS ##############################
