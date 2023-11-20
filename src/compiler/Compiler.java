package compiler;

import compiler.x86.Translator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.PriorityQueue;

public class Compiler {

	private final LexicalAnalyzer lexicalAnalyzer;
	private final Parser parser;
	private final PriorityQueue<SyntacticStructureResult> syntacticStructuresFound;
	private final CompilerMessagePrinter messagePrinter;
	private int errorCount;
	private final Translate translate;
	private final SemanticHelper semanticHelper;

	public Compiler(String sourceFileName) throws FileNotFoundException
	{
		this.lexicalAnalyzer = new LexicalAnalyzer(sourceFileName, this);
		this.semanticHelper = new SemanticHelper(this);

		this.parser = new Parser(this);
		this.syntacticStructuresFound = new PriorityQueue<>(
			new SyntacticStructureResultComparator()
		);
		this.messagePrinter = new CompilerMessagePrinter(this);
		this.errorCount = 0;
		this.translate = new Translate(this);
	}

	public void compile()
	{
		boolean yaccSuccess = parser.yyparse() == 0;

		System.out.println();

		if (yaccSuccess && errorCount == 0)
			messagePrinter.printGreen("Parsing correcto");
		else
			messagePrinter.error("Hubo errores en el parsing");
		System.out.println();
		messagePrinter.printTokenList();
		CompilerMessagePrinter.printFoundSyntacticalStrucutres(syntacticStructuresFound);
		CompilerMessagePrinter.printSymbolTable(getSymbolTable());

		if (errorCount > 0) return;

		ListOfTriplets listOfTriplets = parser.getListOfTriplets();
		System.out.println("\nLista de tercetos:");
		System.out.println(listOfTriplets);

		ListOfAssemblerCode listOfAssemblerCode = translate.translateTriplets(listOfTriplets);
		System.out.println("\nCodigo Assembler");
		System.out.println(listOfAssemblerCode);

		Translator translator = new Translator(this, listOfTriplets);

		String assemblyCode = translator.getAssemblyCode();
		System.out.println(assemblyCode);
		createAssembly(translator.getAssemblyCode());
	}

	public void createAssembly(String assemblyCode){

		String pathAssembly = "assembly.asm";

        try {
            File assembly = new File(pathAssembly);

			// Escribir contenido en el archivo
			FileWriter writer = new FileWriter(assembly, false);
			BufferedWriter bufferEscritor = new BufferedWriter(writer);

			// Contenido a escribir en el archivo
			String code = assemblyCode;

			// Escribir en el archivo
			bufferEscritor.write(code);

			// Cerrar el BufferedWriter
			bufferEscritor.close();
			System.out.println("Contenido agregado al archivo.");
        } catch (IOException e) {
            System.out.println("Ocurrió un error al crear o escribir en el archivo.");
            e.printStackTrace();
        }
	}
	public int yylex()
	{
		return lexicalAnalyzer.getToken();
	}

	public void setyylval(LocatedSymbolTableEntry tokenData)
	{
		parser.setyylval(tokenData);
	}

	public void reportLexicalError(String msg, TokenLocation loc)
	{
		if (loc != null)
			messagePrinter.error("[Lexico: " + loc + "] " + msg);
		else
			messagePrinter.error("[Lexico] " + msg);

		errorCount++;
	}

	public void reportSemanticError(String msg, TokenLocation loc)
	{
		if (loc != null)
			messagePrinter.error("[Semantico: " + loc + "] " + msg);
		else
			messagePrinter.error("[Semantico] " + msg);

		errorCount++;
	}

	public void reportSyntaxError(String msg, TokenLocation loc)
	{
		if (loc != null)
			messagePrinter.error("[Sintactico: " + loc + "] " + msg);
		else
			messagePrinter.error("[Sintactico] " + msg);

		errorCount++;
	}

	public void generateWarning(String msg, TokenLocation loc)
	{
		if (loc != null)
			messagePrinter.warning("[Warning: " + loc + "] " + msg);
		else
			messagePrinter.warning("[Warning] " + msg);
	}

	public void addFoundSyntacticStructure(SyntacticStructureResult s)
	{
		syntacticStructuresFound.add(s);
	}

	public SymbolTable getSymbolTable()
	{
		return lexicalAnalyzer.getSymbolTable();
	}

	public LexicalAnalyzer getLexicalAnalyzer()
	{
		return this.lexicalAnalyzer;
	}

	public CompilerMessagePrinter getMessagePrinter()
	{
		return this.messagePrinter;
	}

	public SemanticHelper getSemanticHelper()
	{
		return semanticHelper;
	}
}