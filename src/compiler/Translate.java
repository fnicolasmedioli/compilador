package compiler;
import java.util.HashMap;
import java.util.Vector;

public class Translate {
    
    private HashMap<String, String> convertionMap;
    private ListOfAssemblerCode listOfAssemblerCode;
    private Vector<Register> registers;
    private SymbolTable symbolTable;
    private Compiler compiler;

    public Translate(Compiler compiler) {
        convertionMap = new HashMap<>();
        loadTranslations();
        listOfAssemblerCode = new ListOfAssemblerCode();
        this.registers = new Vector<>();
        this.compiler = compiler;
        this.symbolTable = compiler.getSymbolTable();
        
    }

  
    public void loadTranslations(){
        convertionMap.put("+", "SUM");
        convertionMap.put("-", "SUB");
        convertionMap.put("*", "MUL");
        convertionMap.put("/", "DIV"); 
        convertionMap.put("WRITE", "MOV");   
        convertionMap.put("=", "MOV");
    }

    public String getConvertion(String operation){
        return convertionMap.get(operation);
    }

    public ListOfAssemblerCode translateTriplets(ListOfTriplets listOfTriplets){
        for (int i = 0; i < listOfTriplets.getSize(); i++){
            this.listOfAssemblerCode.addCode(getCodAssembler(listOfTriplets.getTriplet(i), listOfTriplets, i));
        }
        return this.listOfAssemblerCode;

    }

    public void addRegister(Register register){
        if (!this.registers.contains(register)){
            this.registers.add(register);
        }
    }

    public boolean isAssign(Triplet t){
        if  (t.getType() == null && t.getOperation() == "="){
            return true;
        }
        return false;
    }

    public boolean isOperation(Triplet t){
        if (t.getOperation() == "*" || t.getOperation() == "+" || t.getOperation() == "-" || t.getOperation() == "/" ){
            return true;
        }
        return false;
    }
    public AssemblerCode getCodAssembler(Triplet t, ListOfTriplets listOfTriplets, int pos){
  
        String r1 = "";
        String r2 = "";
        
        String operation = getConvertion(t.getOperation());
        
        TripletOperand operand1 = t.getOperand1();
        TripletOperand operand2 = t.getOperand2();

        if (isAssign(t)){
            if (operand1.isFinal() && operand2.isFinal()){
                r1 = operand1.getstEntry().getLexeme();
                r2 = operand2.getstEntry().getLexeme();
            }else if(operand1.isFinal() && !operand2.isFinal()){
                r1 = operand1.getstEntry().getLexeme();
                r2 = getRegister(operand2.getIndex());
            }else if(!operand1.isFinal() && operand2.isFinal()){
                r1 = getRegister(operand1.getIndex());
                r2 = operand2.getstEntry().getLexeme();
            }else{
                r1 = getRegister(operand1.getIndex());
                r2 = getRegister(operand2.getIndex());
            }
            
        }else if (isOperation(t)){
            if (operand1.isFinal() && operand2.isFinal()){
                r1 = operand1.getstEntry().getLexeme();
                r1 = moveToRegister(r1, pos);
                r2 = operand2.getstEntry().getLexeme();

            }else if (operand1.isFinal() && !operand2.isFinal()){
                r1 = operand1.getstEntry().getLexeme();
                r1 = moveToRegister(r1, pos);
                r2 = getRegister(operand2.getIndex());

            }else if (!operand1.isFinal() && operand2.isFinal()){
                r1 = getRegister(operand1.getIndex());
                r2 = operand2.getstEntry().getLexeme();
            }else{
                r1 = getRegister(operand1.getIndex());
                r1 = moveToRegister(r1, pos);
                r2 = getRegister(operand2.getIndex());

            }
        }



        r1 = removeSuffix(r1);
        r2 = removeSuffix(r2);

        AssemblerCode assemblerCode = new AssemblerCode(operation, r1, r2);

        String varAux = t.getVarTriplet();
        Register register = new Register(varAux, pos);
        addSymbolTableEntry(varAux);
        addRegister(register);

        return  assemblerCode;
    }

    public void addSymbolTableEntry(String varAux){
        SymbolTableEntry varEntry = new SymbolTableEntry(Parser.ID, varAux);
        this.symbolTable.addNewEntry(varEntry, varAux);
    }   

    public String removeSuffix(String whitSuffix){

        String whitoutSuffix = whitSuffix;
        if (whitSuffix.endsWith("_ui")) {
            whitoutSuffix = whitSuffix.replaceAll("_ui" + "$", ""); // Se elimina el sufijo al final de la cadena
        }

        if (whitSuffix.endsWith("_l")) {
            whitoutSuffix = whitSuffix.replaceAll("_l" + "$", ""); 
        }

        return whitoutSuffix;
    }
    public String getRegister(int index){
        String toReturn = "nt";
        for (int i = 0; i < this.registers.size(); i++){
            if ( this.registers.elementAt(i).getIndexOfTriplet() == index)
                toReturn = this.registers.elementAt(i).getRegister();
        }
        return toReturn;
    }

    public void addToListCodeAssembler(AssemblerCode assemblerCode){
        this.listOfAssemblerCode.addCode(assemblerCode);
    }
    
    public String moveToRegister(String operand, int pos){

            Register register = new Register("R1", pos);
            addRegister(register);
            AssemblerCode assemblerCode = new AssemblerCode(getConvertion("WRITE"), register.getRegister(), operand);
            this.listOfAssemblerCode.addCode(assemblerCode);
            return "R1";

    }
    
}
