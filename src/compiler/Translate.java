package compiler;
import java.util.HashMap;

public class Translate {
    
    private HashMap<String, String> convertionMap;

    public Translate() {
        convertionMap = new HashMap<>();
        loadTranslations();
    }

    public void loadTranslations(){
        convertionMap.put("+", "SUM");
        convertionMap.put("-", "SUB");
        convertionMap.put("*", "MUL");
        convertionMap.put("/", "DIV");    
    }

    public String getConvertion(String operation){
        return convertionMap.get(operation);
    }

    public String translateTriplets(ListOfTriplets listOfTriplets){
        String codAssembler = "";
        for (int i = 0; i < listOfTriplets.getSize(); i++){
            codAssembler += "\n" + getCodAssembler(listOfTriplets.getTriplet(i));
        }
        return codAssembler;

    }

    public String getCodAssembler(Triplet t){
        String operation = getConvertion(t.getOperation());

        return  operation + " " + t.getOperand1() + " " +t.getOperand2().toString();
    }
    
}
