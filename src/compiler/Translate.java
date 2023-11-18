package compiler;
import java.util.HashMap;
import java.util.Vector;

public class Translate {
    
    private HashMap<String, String> convertionMap;
    private ListOfAssemblerCode listOfAssemblerCode;
    private Vector<Register> registers;

    public Translate() {
        convertionMap = new HashMap<>();
        loadTranslations();
        listOfAssemblerCode = new ListOfAssemblerCode();
        this.registers = new Vector<>();
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

    public AssemblerCode getCodAssembler(Triplet t, ListOfTriplets listOfTriplets, int pos){
  
        String r1;

        String operation = getConvertion(t.getOperation());
        
        if (t.getOperation() == "="){
            r1 = t.getOperand1().getstEntry().getLexeme();
        }else{
            r1 = moveToRegister(t.getOperand1(), pos);
        }
        

        String r2;

        if (t.getOperand2().isFinal()){
            r2 = t.getOperand2().getstEntry().getLexeme(); 
        } else{
            r2 = getRegister(t.getOperand2().getIndex());

            //operand2= Integer.toString(t.getOperand2().getIndex());
        }

        AssemblerCode assemblerCode = new AssemblerCode(operation, r1, r2);
        return  assemblerCode;
    }

    public String getRegister(int index){
        String toReturn = "";
        for (int i = 0; i < this.registers.size(); i++){
            if ( this.registers.elementAt(i).getIndexListOfTiplet() == index)
                toReturn = this.registers.elementAt(i).getRegister();
        }
        return toReturn;
    }

    public void addToListCodeAssembler(AssemblerCode assemblerCode){
        this.listOfAssemblerCode.addCode(assemblerCode);
    }
    
    public String moveToRegister(TripletOperand t, int pos){

        String operand1;
        if (t.isFinal()){
            operand1 = t.getstEntry().getLexeme();
            Register register = new Register("R1", pos);
            addRegister(register);
            AssemblerCode assemblerCode = new AssemblerCode(getConvertion("WRITE"), register.getRegister(), operand1);
            this.listOfAssemblerCode.addCode(assemblerCode);
            return "R1";
        } else{
            operand1= Integer.toString(t.getIndex());
            return "R1";
        }
    }
    
}
