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
 
import java.util.LinkedList;

//#line 21 "Parser.java"




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
    5,    5,    5,    6,    6,    7,    7,    8,    8,    8,
    8,   12,   12,   12,   12,   12,   12,   12,   12,   12,
   12,   12,   12,   12,   12,    1,    1,    1,    1,   18,
   18,   15,   15,   15,   13,   13,   16,   16,   16,   16,
   16,   16,   16,   20,   20,   20,   20,   20,   20,   20,
    4,    4,   21,   21,   21,   22,   22,   22,   23,   23,
   19,    9,   25,   26,   27,   24,   24,   24,   24,   24,
   17,   17,   28,   14,   14,   14,   10,   10,   29,   29,
   29,   29,   29,   29,   30,   30,   31,   32,   33,   11,
   11,
};
final static short yylen[] = {                            2,
    3,    2,    1,    1,    1,    1,    1,    1,    3,    1,
    1,    1,    1,    3,    1,    3,    3,    1,    2,    2,
    2,    4,    4,    4,    4,    2,    2,    2,    3,    2,
    3,    3,    3,    3,    3,    2,    2,    1,    1,    2,
    1,    3,    4,    4,    1,    1,    6,    8,    8,   10,
   10,   12,    4,    1,    1,    1,    1,    2,    2,    2,
    1,    4,    3,    3,    1,    3,    3,    1,    1,    1,
    1,    1,    1,    1,    1,    9,    7,    8,    6,    3,
    6,    8,    1,    3,    3,    3,    5,    4,    2,    1,
    3,    2,    3,    2,    3,    2,    1,    1,    1,    7,
    3,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    0,    0,    0,    0,   10,   11,   12,
   13,    0,    0,    0,    2,    0,    0,   18,   39,    0,
    0,    0,   38,    0,    0,    0,    0,   72,    0,    0,
    0,    0,    0,    0,    0,    0,   73,    0,    0,    0,
   15,   46,    0,    0,   45,    0,    0,    0,    0,    0,
    0,    0,   30,    1,   37,   36,    0,   19,   20,   21,
    0,    0,   26,   27,   28,    0,   69,   57,   54,   56,
   55,    0,    0,    0,    0,   70,   61,    0,   68,   33,
    0,   32,   31,   35,   29,   34,    0,   74,    0,   80,
    0,    0,    0,    0,   42,    0,    0,    0,   17,    0,
   41,    0,    0,  101,   97,    0,   16,    0,    0,    0,
   53,    0,   58,   60,   59,    0,    3,    4,    5,    6,
    7,    8,    0,    0,    0,    0,    0,   84,   86,    0,
   75,   90,   83,   88,    0,    0,    0,    0,   24,   44,
   43,   14,   22,    0,   40,    0,    0,   25,   23,    0,
    0,    0,    0,    0,    0,    0,   66,   67,   94,   92,
    0,   89,   87,    0,    0,    0,    0,    0,   98,    0,
   62,    0,    0,   47,   93,   91,    0,   79,    0,    0,
   81,    0,    0,    0,    0,    0,   77,    0,    0,   96,
   99,    0,  100,    0,   48,    0,   49,    0,   78,   82,
   95,    0,    0,    0,   76,    0,   51,   50,    0,   52,
};
final static short yydgoto[] = {                          2,
   16,  125,   74,   75,   17,   46,   18,   19,   20,   21,
   22,  101,   47,   24,   25,   26,   27,  102,   97,   76,
   77,   78,   79,   28,   38,   89,  134,  135,  136,  183,
  106,  170,  193,
};
final static short yysindex[] = {                      -112,
  -70,    0,  -31, -151, -193, -181,  -39,    0,    0,    0,
    0, -118, -187,   36,    0,   31, -180,    0,    0,   48,
   58,   84,    0,   -8,   90,   94,   96,    0, -133,  -37,
   64,   97,   99,  100,  105,   77,    0,   30,   27,  115,
    0,    0, -106,  -41,    0,   29,  -37,  -34, -192, -120,
   35, -102,    0,    0,    0,    0,   32,    0,    0,    0,
 -101,  -37,    0,    0,    0,  121,    0,    0,    0,    0,
    0,  126, -170,  128,  -10,    0,    0,   37,    0,    0,
  -96,    0,    0,    0,    0,    0, -101,    0,   51,    0,
   20,  130,  127,  132,    0,   79,  133,  -88,    0,   61,
    0,   74,  137,    0,    0,  124,    0,  130,  135,   70,
    0,  -37,    0,    0,    0, -109,    0,    0,    0,    0,
    0,    0,   12,   12,  -37,   12,   12,    0,    0,  -42,
    0,    0,    0,    0,  144,  118,   30,  -60,    0,    0,
    0,    0,    0,  -89,    0,  -37,   85,    0,    0,   79,
  164, -192, -172,   37,   37,   79,    0,    0,    0,    0,
  -28,    0,    0,  166,   81,  171,  173,  175,    0,  -48,
    0,   76,  -79,    0,    0,    0,   81,    0,   30,  -37,
    0,  174, -115, -128, -192,  -40,    0,   81,  176,    0,
    0,  179,    0,  -77,    0,  113,    0,   81,    0,    0,
    0, -192,  -19,    2,    0,  114,    0,    0,    7,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  -26,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,   -5,    0,    0,    0,  203,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,   -7,   -3,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   82,    0,    0,  -20,  -15,  210,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
  -93,    0,  -83,    4,  129,  239,  -69,  -13,    0,    0,
    0,  134,  243,  266,   16,    0,    0, -121,    0,    0,
  160,    9,   10,  -76,  268, -105,  -87,  -74,    0,    0,
    0,    0,    0,
};
final static int YYTABLESIZE=395;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         95,
   44,  159,   55,   73,   49,   44,   43,   73,   30,  191,
    1,   43,  133,  152,   65,  175,   65,   65,   65,  132,
   64,   45,   64,   64,   64,   63,   45,   63,   63,   63,
  172,  165,  124,   65,  123,   65,   85,   61,   85,   64,
   84,   64,   86,  185,   63,  202,   63,   96,  163,  122,
  100,  121,   45,   85,   15,   84,   73,   86,   93,  133,
  137,  164,  168,  196,    3,  110,  162,    4,   51,   37,
   48,  177,   99,  188,   39,  107,  109,  178,  126,   53,
  206,   37,   41,  127,   12,  173,  174,   98,   14,  187,
   98,   58,   52,  133,  198,  182,  189,  113,  114,  115,
  199,   59,  129,  124,  143,  123,  133,   80,  192,   81,
  205,   31,  124,  149,  123,  150,   32,   33,   34,   35,
   86,  124,   87,  123,   61,   66,   61,   60,  156,  194,
  195,  154,  155,   63,   23,  157,  158,   64,    3,   65,
   82,    4,   83,   84,   48,   50,    6,    3,   85,   56,
    4,   90,   88,   48,   91,   54,   92,  103,   12,  104,
  105,  108,   14,   55,  111,  112,  128,   12,  116,   44,
  139,   14,  140,  141,  142,  131,  146,    3,  148,    3,
    4,  147,    4,   48,   55,   48,    3,  160,  167,    4,
    5,    6,    7,    8,    9,   10,   11,   12,  144,   12,
  184,   14,  166,   14,  171,  131,   12,  169,   13,  176,
   14,  179,  180,    6,   94,  181,  200,  190,  197,  138,
   41,   67,  201,   41,   29,   67,   68,   69,   70,   71,
   68,   69,   70,   71,   41,  145,   42,  204,  209,  207,
   72,   42,  131,   71,   72,   65,   65,   65,   65,  153,
    9,   64,   64,   64,   64,   57,   63,   63,   63,   63,
  208,  117,  118,  119,  120,  210,   62,   42,   85,   36,
   84,  151,   86,   40,   67,    0,    0,    0,    0,   68,
   69,   70,   71,    8,    9,   10,   11,    3,    0,    0,
    4,    5,    6,    7,    8,    9,   10,   11,   23,    0,
    0,    0,    0,    0,    0,  145,  186,   12,    0,   13,
   56,   14,    6,  130,    8,    9,   10,   11,    0,    0,
    0,   23,    0,    0,    0,    0,    0,  203,    0,  145,
    3,   56,    3,    4,    0,    4,   48,    3,   48,  145,
    4,    5,    6,    7,    8,    9,   10,   11,    0,    0,
   12,    0,   12,    0,   14,    0,   14,   12,    0,   13,
    0,   14,    0,    0,    0,    0,    0,    0,    0,    3,
    3,    0,    4,    4,    0,   48,   48,    0,    0,    6,
  161,    8,    9,   10,   11,    0,    0,    0,    0,   12,
   12,    0,    0,   14,   14,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         41,
   40,   44,   16,   45,  123,   40,   46,   45,   40,  125,
  123,   46,   89,  123,   41,   44,   43,   44,   45,   89,
   41,   61,   43,   44,   45,   41,   61,   43,   44,   45,
  152,  137,   43,   60,   45,   62,   44,   46,   46,   60,
   46,   62,   46,  123,   60,  123,   62,   44,  136,   60,
   47,   62,   61,   61,  125,   61,   45,   61,   43,  136,
   41,  136,  146,  185,  257,   62,  136,  260,  256,  263,
  263,  165,   44,  179,  256,   44,   61,  165,   42,   44,
  202,  263,  263,   47,  277,  258,  259,   59,  281,  177,
   59,   44,  280,  170,  188,  170,  180,  268,  269,  270,
  188,   44,   87,   43,   44,   45,  183,   44,  183,   46,
  198,  263,   43,   44,   45,  112,  268,  269,  270,  271,
   44,   43,   46,   45,   43,  259,   45,   44,  125,  258,
  259,  123,  124,   44,    1,  126,  127,   44,  257,   44,
   44,  260,   44,   44,  263,   12,  262,  257,   44,   16,
  260,  125,  123,  263,   40,  125,  263,  278,  277,  125,
  263,  263,  281,  177,   44,   40,  263,  277,   41,   40,
   44,  281,   41,   41,  263,  125,   40,  257,   44,  257,
  260,   58,  260,  263,  198,  263,  257,   44,  278,  260,
  261,  262,  263,  264,  265,  266,  267,  277,  125,  277,
  125,  281,  263,  281,   41,  125,  277,  123,  279,   44,
  281,   41,   40,  262,  256,   41,   41,   44,  259,   91,
  263,  263,   44,  263,  256,  263,  268,  269,  270,  271,
  268,  269,  270,  271,  263,  102,  276,  125,  125,  259,
  282,  276,  125,   41,  282,  272,  273,  274,  275,  116,
   41,  272,  273,  274,  275,   17,  272,  273,  274,  275,
  259,  272,  273,  274,  275,  259,   24,  276,  276,    4,
  276,  112,  276,    6,  263,   -1,   -1,   -1,   -1,  268,
  269,  270,  271,  264,  265,  266,  267,  257,   -1,   -1,
  260,  261,  262,  263,  264,  265,  266,  267,  165,   -1,
   -1,   -1,   -1,   -1,   -1,  172,  173,  277,   -1,  279,
  177,  281,  262,  263,  264,  265,  266,  267,   -1,   -1,
   -1,  188,   -1,   -1,   -1,   -1,   -1,  194,   -1,  196,
  257,  198,  257,  260,   -1,  260,  263,  257,  263,  206,
  260,  261,  262,  263,  264,  265,  266,  267,   -1,   -1,
  277,   -1,  277,   -1,  281,   -1,  281,  277,   -1,  279,
   -1,  281,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  257,
  257,   -1,  260,  260,   -1,  263,  263,   -1,   -1,  262,
  263,  264,  265,  266,  267,   -1,   -1,   -1,   -1,  277,
  277,   -1,   -1,  281,  281,
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
"declaracion_variable : tipo_basico lista_identificadores ','",
"declaracion_variable : ID lista_identificadores ','",
"sentencia_declarativa : declaracion_variable",
"sentencia_declarativa : definicion_funcion ','",
"sentencia_declarativa : definicion_clase ','",
"sentencia_declarativa : implementacion ','",
"sentencia_ejecutable : ID op_asignacion_aumentada expr ','",
"sentencia_ejecutable : acceso_atributo op_asignacion_aumentada expr ','",
"sentencia_ejecutable : ID '.' invocacion_funcion ','",
"sentencia_ejecutable : acceso_atributo '.' invocacion_funcion ','",
"sentencia_ejecutable : invocacion_funcion ','",
"sentencia_ejecutable : sentencia_if ','",
"sentencia_ejecutable : do_until ','",
"sentencia_ejecutable : PRINT CTE_STRING ','",
"sentencia_ejecutable : RETURN ','",
"sentencia_ejecutable : PRINT CTE_UINT ','",
"sentencia_ejecutable : PRINT CTE_LONG ','",
"sentencia_ejecutable : PRINT ID ','",
"sentencia_ejecutable : PRINT acceso_atributo ','",
"sentencia_ejecutable : PRINT CTE_DOUBLE ','",
"lista_sentencias : lista_sentencias sentencia_ejecutable",
"lista_sentencias : lista_sentencias sentencia_declarativa",
"lista_sentencias : sentencia_ejecutable",
"lista_sentencias : sentencia_declarativa",
"lista_sentencias_ejecutables : lista_sentencias_ejecutables sentencia_ejecutable",
"lista_sentencias_ejecutables : sentencia_ejecutable",
"invocacion_funcion : ID '(' ')'",
"invocacion_funcion : ID '(' parametro_real ')'",
"invocacion_funcion : ID '(' error ')'",
"op_asignacion_aumentada : '='",
"op_asignacion_aumentada : SUB_ASIGN",
"sentencia_if : IF '(' condicion ')' sentencia_ejecutable END_IF",
"sentencia_if : IF '(' condicion ')' '{' lista_sentencias_ejecutables '}' END_IF",
"sentencia_if : IF '(' condicion ')' sentencia_ejecutable ELSE sentencia_ejecutable END_IF",
"sentencia_if : IF '(' condicion ')' sentencia_ejecutable ELSE '{' lista_sentencias_ejecutables '}' END_IF",
"sentencia_if : IF '(' condicion ')' '{' lista_sentencias_ejecutables '}' ELSE sentencia_ejecutable END_IF",
"sentencia_if : IF '(' condicion ')' '{' lista_sentencias_ejecutables '}' ELSE '{' lista_sentencias_ejecutables '}' END_IF",
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
"factor : ID",
"factor : constante",
"parametro_real : expr",
"definicion_funcion : procedimiento",
"id_ambito : ID",
"abrir_scope : '{'",
"cerrar_scope : '}'",
"procedimiento : VOID id_ambito '(' tipo_basico ID ')' abrir_scope lista_sentencias cerrar_scope",
"procedimiento : VOID id_ambito '(' ')' abrir_scope lista_sentencias cerrar_scope",
"procedimiento : VOID id_ambito '(' tipo_basico ID ')' abrir_scope cerrar_scope",
"procedimiento : VOID id_ambito '(' ')' abrir_scope cerrar_scope",
"procedimiento : VOID error '}'",
"do_until : DO sentencia_ejecutable UNTIL '(' condicion ')'",
"do_until : DO '{' lista_sentencias_ejecutables '}' UNTIL '(' condicion ')'",
"metodo : procedimiento",
"acceso_atributo : ID '.' ID",
"acceso_atributo : acceso_atributo '.' ID",
"acceso_atributo : acceso_atributo '.' invocacion_funcion",
"definicion_clase : CLASS id_ambito abrir_scope cuerpo_clase cerrar_scope",
"definicion_clase : CLASS id_ambito abrir_scope cerrar_scope",
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

//#line 502 "compiler.y"

void yyerror(String msg)
{
    System.out.println("ERROR! " + msg);
}

int yylex()
{
    return compiler.yylex();
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

String _currentID;
LinkedList<String> _currentScope;
Compiler compiler;
SemanticHelper semanticHelper;
SymbolTable symbolTable;
String implementationMethodScope;
LinkedList<String> scopeCopy;

public Parser(Compiler compiler)
{
    this.compiler = compiler;
    this._currentID = "";
    this._currentScope = new LinkedList<>();
    this.semanticHelper = new SemanticHelper(compiler);
    this.symbolTable = compiler.getSymbolTable();
    yydebug = false;
}
//#line 520 "Parser.java"
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
case 14:
//#line 45 "compiler.y"
{
            ((LinkedList<LocatedSymbolTableEntry>)(val_peek(2).obj)).add((LocatedSymbolTableEntry)val_peek(0).obj);            
        }
break;
case 15:
//#line 49 "compiler.y"
{
            LinkedList<LocatedSymbolTableEntry> lista = new LinkedList<>();
            lista.add((LocatedSymbolTableEntry)val_peek(0).obj);
            yyval = new ParserVal(lista);
        }
break;
case 16:
//#line 58 "compiler.y"
{
            semanticHelper.declarePrimitiveList(val_peek(1), getCurrentScopeStr(), getSTEntry(val_peek(2)));
        }
break;
case 17:
//#line 62 "compiler.y"
{
            semanticHelper.declareObjectList(val_peek(1), getCurrentScopeStr(), (LocatedSymbolTableEntry)val_peek(2).obj);
        }
break;
case 18:
//#line 69 "compiler.y"
{
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Declaracion de variables", getTokenLocation(val_peek(0)))
            );
        }
break;
case 19:
//#line 75 "compiler.y"
{
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Definicion de funcion", getTokenLocation(val_peek(1)))
            );
        }
break;
case 20:
//#line 81 "compiler.y"
{
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Definicion de clase", getTokenLocation(val_peek(1)))
            );
        }
break;
case 21:
//#line 87 "compiler.y"
{
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Sentencia IMPL", getTokenLocation(val_peek(1)))
            );
        }
break;
case 22:
//#line 96 "compiler.y"
{
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Asignacion a variable", getTokenLocation(val_peek(3)))
            );
        }
break;
case 23:
//#line 102 "compiler.y"
{
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Asignacion a atributo", getTokenLocation(val_peek(3)))
            );
        }
break;
case 24:
//#line 108 "compiler.y"
{
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Invocacion a metodo", getTokenLocation(val_peek(3)))
            );
        }
break;
case 25:
//#line 114 "compiler.y"
{
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Invocacion a metodo", getTokenLocation(val_peek(3)))
            );
        }
break;
case 26:
//#line 120 "compiler.y"
{
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Invocacion a funcion", getTokenLocation(val_peek(1)))
            );
        }
break;
case 27:
//#line 126 "compiler.y"
{
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Sentencia IF", getTokenLocation(val_peek(1)))
            );
        }
break;
case 28:
//#line 132 "compiler.y"
{
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Estructura DO UNTIL", getTokenLocation(val_peek(1)))
            );
        }
break;
case 29:
//#line 138 "compiler.y"
{
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Sentencia PRINT", getTokenLocation(val_peek(2)))
            );
        }
break;
case 30:
//#line 144 "compiler.y"
{
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Sentencia RETURN", getTokenLocation(val_peek(1)))
            );
        }
break;
case 31:
//#line 150 "compiler.y"
{
            compiler.reportSyntaxError("No se puede imprimir un UINT", getTokenLocation(val_peek(2)));
        }
break;
case 32:
//#line 154 "compiler.y"
{
            compiler.reportSyntaxError("No se puede imprimir un LONG", getTokenLocation(val_peek(2)));
        }
break;
case 33:
//#line 158 "compiler.y"
{
            compiler.reportSyntaxError("No se puede imprimir una variable", getTokenLocation(val_peek(2)));
        }
break;
case 34:
//#line 162 "compiler.y"
{
            compiler.reportSyntaxError("No se puede imprimir un atributo", getTokenLocation(val_peek(2)));
        }
break;
case 35:
//#line 166 "compiler.y"
{
            compiler.reportSyntaxError("No se puede imprimir un DOUBLE", getTokenLocation(val_peek(2)));
        }
break;
case 44:
//#line 187 "compiler.y"
{
            compiler.reportSyntaxError("Error en invocacion a metodo", getTokenLocation(val_peek(3)));
        }
break;
case 53:
//#line 205 "compiler.y"
{
            compiler.reportSyntaxError("Error en IF", getTokenLocation(val_peek(3)));
        }
break;
case 57:
//#line 215 "compiler.y"
{
            if (!ConstantRange.isValidLONG(getSTEntry(val_peek(0)).getLexeme(), false))
                compiler.reportLexicalError("El rango de LONG es [-2147483648, 2147483647]", getTokenLocation(val_peek(0)));
        }
break;
case 58:
//#line 220 "compiler.y"
{
            getSTEntry(val_peek(0)).addNegativeSign();
        }
break;
case 59:
//#line 224 "compiler.y"
{
            getSTEntry(val_peek(0)).addNegativeSign();
        }
break;
case 60:
//#line 228 "compiler.y"
{
            compiler.reportLexicalError("Las constantes tipo UINT no pueden ser negativas", getTokenLocation(val_peek(1)));
        }
break;
case 69:
//#line 252 "compiler.y"
{
            /* Chequear alcance y tipo de ID*/

            SymbolTableEntry referredSTEntry = semanticHelper.getEntryByScope(getSTEntry(val_peek(0)).getLexeme(), getCurrentScopeStr());

            if (referredSTEntry == null)
            {
                compiler.reportSemanticError("Variable/atributo no alcanzable", getTokenLocation(val_peek(0)));
            }
            else
            {
                if (referredSTEntry.getAttrib(AttribKey.ID_TYPE) != IDType.VAR_ATTRIB)
                    compiler.reportSemanticError("El identificador " + referredSTEntry.getLexeme() + "no es de tipo var/attribute", getTokenLocation(val_peek(0)));
                else
                {
                    /* Todo ok */
                }
            }
        }
break;
case 73:
//#line 284 "compiler.y"
{
            setCurrentID(getSTEntry(val_peek(0)).getLexeme());
        }
break;
case 74:
//#line 291 "compiler.y"
{
            addToCurrentScope(getCurrentID());
        }
break;
case 75:
//#line 298 "compiler.y"
{
            removeScope();
        }
break;
case 76:
//#line 305 "compiler.y"
{
            String idLexeme = getSTEntry(val_peek(7)).getLexeme();

            if (semanticHelper.alreadyDeclaredInScope(idLexeme, getCurrentScopeStr()))
                compiler.reportSemanticError("Identificador ya declarado en el ámbito local", getTokenLocation(val_peek(7)));
            else
            {
                semanticHelper.declareFunction(getCurrentScopeStr(), val_peek(7).obj);

                String scopeAdentro = getCurrentScopeStr() + ":" + getSTEntry(val_peek(7)).getLexeme();
                semanticHelper.declareArg(scopeAdentro, val_peek(4).obj, val_peek(5).obj);
            }
        }
break;
case 77:
//#line 319 "compiler.y"
{
            String idLexeme = getSTEntry(val_peek(5)).getLexeme();

            if (semanticHelper.alreadyDeclaredInScope(idLexeme, getCurrentScopeStr()))
                compiler.reportSemanticError("Identificador ya declarado en el ámbito local", getTokenLocation(val_peek(5)));
            else
            {
                semanticHelper.declareFunction(getCurrentScopeStr(), val_peek(5).obj);
            }
        }
break;
case 78:
//#line 330 "compiler.y"
{
            String idLexeme = getSTEntry(val_peek(6)).getLexeme();

            if (semanticHelper.alreadyDeclaredInScope(idLexeme, getCurrentScopeStr()))
                compiler.reportSemanticError("Identificador ya declarado en el ámbito local", getTokenLocation(val_peek(6)));
            else
            {
                semanticHelper.declareFunction(getCurrentScopeStr(), val_peek(6).obj);
            }
        }
break;
case 79:
//#line 341 "compiler.y"
{
            String idLexeme = getSTEntry(val_peek(4)).getLexeme();

            if (semanticHelper.alreadyDeclaredInScope(idLexeme, getCurrentScopeStr()))
                compiler.reportSemanticError("Identificador ya declarado en el ámbito local", getTokenLocation(val_peek(4)));
            else
            {
                semanticHelper.declareFunction(getCurrentScopeStr(), val_peek(4).obj);
            }
        }
break;
case 80:
//#line 352 "compiler.y"
{
            compiler.reportSyntaxError("Error en funcion/metodo", getTokenLocation(val_peek(2)));
        }
break;
case 87:
//#line 374 "compiler.y"
{
            semanticHelper.declareClass(getCurrentScopeStr(), (LocatedSymbolTableEntry)val_peek(3).obj);
        }
break;
case 88:
//#line 378 "compiler.y"
{
            semanticHelper.declareClass(getCurrentScopeStr(), (LocatedSymbolTableEntry)val_peek(2).obj);
        }
break;
case 89:
//#line 385 "compiler.y"
{
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Declaracion de atributo", getTokenLocation(val_peek(0)))
            );
        }
break;
case 90:
//#line 391 "compiler.y"
{
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Declaracion de atributo", getTokenLocation(val_peek(0)))
            );
        }
break;
case 91:
//#line 397 "compiler.y"
{
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Implementacion de metodo dentro de clase", getTokenLocation(val_peek(1)))
            );
        }
break;
case 92:
//#line 403 "compiler.y"
{
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Implementacion de metodo dentro de clase", getTokenLocation(val_peek(1)))
            );
        }
break;
case 93:
//#line 409 "compiler.y"
{
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Herencia por composicion", getTokenLocation(val_peek(1)))
            );

            semanticHelper.declareComposition(getCurrentScopeStr(), val_peek(1).obj);
        }
break;
case 94:
//#line 417 "compiler.y"
{
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Herencia por composicion", getTokenLocation(val_peek(1)))
            );

            semanticHelper.declareComposition(getCurrentScopeStr(), val_peek(0).obj);
        }
break;
case 95:
//#line 428 "compiler.y"
{
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Método distribuido", getTokenLocation(val_peek(1)))
            );
        }
break;
case 96:
//#line 434 "compiler.y"
{
            compiler.addFoundSyntacticStructure(
                new SyntacticStructureResult("Método distribuido", getTokenLocation(val_peek(1)))
            );
        }
break;
case 97:
//#line 443 "compiler.y"
{
            /* Chequear alcance y tipo de ID*/

            String entryKey = semanticHelper.getKeyByScope(getSTEntry(val_peek(0)).getLexeme(), getCurrentScopeStr());

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
case 98:
//#line 477 "compiler.y"
{
            /* Guardar el current scope*/
            this.scopeCopy = (LinkedList<String>)(this._currentScope.clone());
            /* Reemplazar por el scope de la clase referenciada*/
            this._currentScope = semanticHelper.scopeStrToList(this.implementationMethodScope);
        }
break;
case 99:
//#line 487 "compiler.y"
{
            /* Recuperar el scope antes del IMPL*/
            this._currentScope = this.scopeCopy;
        }
break;
case 101:
//#line 496 "compiler.y"
{
            compiler.reportSyntaxError("Error en implementación distribuida", getTokenLocation(val_peek(2)));
        }
break;
//#line 1101 "Parser.java"
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
