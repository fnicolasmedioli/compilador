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
		//System.out.println();
		//messagePrinter.printTokenList();
		//CompilerMessagePrinter.printFoundSyntacticalStrucutres(syntacticStructuresFound);
		//CompilerMessagePrinter.printSymbolTable(getSymbolTable());

		if (errorCount > 0)
		{
			messagePrinter.error("Corrija los errores para continuar con la compilacion");
			return;
		}

		ListOfTriplets listOfTriplets = parser.getListOfTriplets();
		System.out.println("\nLista de tercetos:");
		//System.out.println(listOfTriplets);

		Translator translator = new Translator(this, listOfTriplets);

		String assemblyCode = translator.getAssemblyCode();
		//System.out.println(assemblyCode);
		
		String assemblyArch = createAssembly(assemblyCode);
		
		compileCode(assemblyArch);

		deleteAssemblyCode(assemblyArch);


	}

	public void deleteAssemblyCode(String archAssembler){
		String[] archivosAEliminar = {archAssembler + ".asm", archAssembler + ".obj", archAssembler+".exe"}; // Agrega aquí todos los archivos que deseas eliminar

        for (String archivo : archivosAEliminar) {
            File file = new File(archivo);

            // Verificar si el archivo existe antes de intentar eliminarlo
            if (file.exists()) {
                if (file.delete()) {
                    System.out.println("Archivo " + archivo + " eliminado con éxito.");
                } else {
                    System.out.println("No se pudo eliminar el archivo " + archivo);
                }
            } else {
                System.out.println("El archivo " + archivo + " no existe.");
            }
        }
	}

	public void compileCode(String archivoASM){
			try {
            // Ruta al directorio donde se encuentran los archivos de masm32
            String masm32Path = "C:\\masm32\\bin\\"; // Cambia esto a tu ruta real

            // Archivo .asm que quieres ensamblar
            //String archivoASM = "hello.asm";

            // Comando para ensamblar el archivo .asm
            String comandoEnsamblar = masm32Path + "ml /c /Zd /coff " + archivoASM;

            // Crear el proceso para ejecutar el comando de ensamblaje
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", comandoEnsamblar);
            builder.redirectErrorStream(true);

            // Ejecutar el proceso
            Process procesoEnsamblaje = builder.start();

            // Esperar a que termine el proceso de ensamblaje
            procesoEnsamblaje.waitFor();

            // Comando para crear el objeto
            String comandoCrearObjeto = masm32Path + "Link /SUBSYSTEM:CONSOLE hello.obj";

            // Crear el proceso para crear el objeto
            builder = new ProcessBuilder("cmd.exe", "/c", comandoCrearObjeto);
            builder.redirectErrorStream(true);

            // Ejecutar el proceso
            Process procesoCrearObjeto = builder.start();

            // Esperar a que termine el proceso de creación del objeto
            procesoCrearObjeto.waitFor();

            // Comando para ejecutar el programa ensamblado
            String comandoEjecutar = "cmd.exe /c hello";

            // Ejecutar el programa ensamblado
            Process procesoEjecutar = Runtime.getRuntime().exec(comandoEjecutar);

            // Leer la salida del programa
            BufferedReader reader = new BufferedReader(new InputStreamReader(procesoEjecutar.getInputStream()));
            String linea;
            while ((linea = reader.readLine()) != null) {
                System.out.println(linea);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
	}
	public String createAssembly(String assemblyCode){

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
			System.out.println("\n\nContenido agregado al archivo.");
        } catch (IOException e) {
            System.out.println("Ocurrió un error al crear o escribir en el archivo.");
            e.printStackTrace();
        }

		return pathAssembly;

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