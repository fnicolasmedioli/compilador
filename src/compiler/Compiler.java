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
			return;
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

		if (!semanticHelper.isForwardListEmpty())
		{
			messagePrinter.error("Hubo referencias forward no encontradas");
			errorCount++;
		}

		if (!yaccSuccess || errorCount > 0)
		{
			messagePrinter.error("Corrija los errores para continuar con la compilacion");
			return;
		}

		ListOfTriplets listOfTriplets = parser.getListOfTriplets();
		System.out.println("\nLista de tercetos:");
		System.out.println(listOfTriplets);

		Translator translator = new Translator(this, listOfTriplets);

		String assemblyCode = translator.getAssemblyCode();

		String fileWithoutExtension = "build\\program";
		String asmFileName = fileWithoutExtension + ".asm";

		boolean saveFileSuccess = saveToFile(assemblyCode, asmFileName);

		if (!saveFileSuccess)
		{
			messagePrinter.error("No se pudo guardar el archivo .asm");
			return;
		}

		String s = runASM();

		System.out.println(s);
	}

	private String runASM()
	{
		String runCommand = String.format("\"cd %s/scripts && run_masm.bat\"", System.getProperty("user.dir"));

		StringBuilder output = new StringBuilder();

		try
		{
			Process runProcess = null;

			ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", runCommand);

			processBuilder.redirectErrorStream(true);
			runProcess = processBuilder.start();

			BufferedReader reader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));

			String line;
			while ((line = reader.readLine()) != null) {
				output.append(line).append("\n");
			}

			int exitCode = runProcess.waitFor();
		}
		catch (IOException | InterruptedException e)
		{
			e.printStackTrace();
			return null;
		}

		return output.toString();
	}

	public boolean saveToFile(String text, String fileName) {

		File folder = new File("build");

		if (!folder.exists()) {
			boolean success = folder.mkdirs();
			if (!success)
			{
				messagePrinter.error("No se pudo crear la carpeta build");
				return false;
			}
		}

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