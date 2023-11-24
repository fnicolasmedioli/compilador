package compiler;

import compiler.x86.Translator;

import java.util.PriorityQueue;
import java.io.*;

public class Compiler {

	private final LexicalAnalyzer lexicalAnalyzer;
	private final Parser parser;
	private final PriorityQueue<SyntacticStructureResult> syntacticStructuresFound;
	private final CompilerMessagePrinter messagePrinter;
	private int errorCount;
	//private final Translate translate;
	private final SemanticHelper semanticHelper;

	private final String masm32Path = "C:\\masm32\\bin\\";

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
		//this.translate = new Translate(this);
	}

	public void compile()
	{
		boolean yaccSuccess = false;

		try
		{
			yaccSuccess = parser.yyparse() == 0;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		System.out.println();

		if (yaccSuccess && errorCount == 0)
			messagePrinter.printGreen("Parsing correcto");
		else
			messagePrinter.error("Hubo errores en el parsing");

		System.out.println();
		messagePrinter.printTokenList();
		CompilerMessagePrinter.printFoundSyntacticalStrucutres(syntacticStructuresFound);
		CompilerMessagePrinter.printSymbolTable(getSymbolTable());

		if (errorCount > 0)
		{
			messagePrinter.error("Corrija los errores para continuar con la compilacion");
			return;
		}

		ListOfTriplets listOfTriplets = parser.getListOfTriplets();
		System.out.println("\nLista de tercetos:");
		System.out.println(listOfTriplets);

		Translator translator = new Translator(this, listOfTriplets);

		String assemblyCode = translator.getAssemblyCode();
		//System.out.println(assemblyCode);

		String fileWithoutExtension = "build\\program";
		String asmFileName = fileWithoutExtension + ".asm";

		boolean saveFileSuccess = saveToFile(assemblyCode, asmFileName);

		if (!saveFileSuccess)
		{
			messagePrinter.error("No se pudo guardar el archivo .asm");
			return;
		}
		else
			messagePrinter.printGreen("Archivo .asm generado exitosamente");

		/*
		System.out.println("Salida assembly:");
		assembleAndRun(fileWithoutExtension);
		*/
	}

	/*
	private void assembleAndRun(String fileWithoutExtension) {

		// Comando para ensamblar el archivo .asm
		String assembleCommand = masm32Path + "ml /c /Zd /coff " + fileWithoutExtension + ".asm";
		String linkCommand = masm32Path + "Link /SUBSYSTEM:CONSOLE " + fileWithoutExtension + ".obj";
		String runCommand = "cmd.exe /c " + fileWithoutExtension + ".exe";

		try {
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", assembleCommand);
            processBuilder.redirectErrorStream(true);

            Process assembleProcess = processBuilder.start();
			assembleProcess.waitFor(); // blocking

            processBuilder = new ProcessBuilder("cmd.exe", "/c", linkCommand);
			processBuilder.redirectErrorStream(true);

            Process linkProcess = processBuilder.start();
			linkProcess.waitFor();

            Process runProcess = Runtime.getRuntime().exec(runCommand);

            BufferedReader reader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
	}
	 */

	public boolean saveToFile(String text, String fileName) {
		try
		{
			File file = new File(fileName);
			FileWriter writer = new FileWriter(file, false);
			BufferedWriter bufferEscritor = new BufferedWriter(writer);
			bufferEscritor.write(text);
			bufferEscritor.close();
			return true;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
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