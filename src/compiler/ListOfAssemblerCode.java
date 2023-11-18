package compiler;

import java.util.Vector;

public class ListOfAssemblerCode {
    private final Vector<AssemblerCode> assemblerCodes;

    public ListOfAssemblerCode() {
        assemblerCodes = new Vector<>();
    }

    public AssemblerCode getCode(int index) {
        
        return this.assemblerCodes.elementAt(index);
        
    }

    public void addCode(AssemblerCode code){
        this.assemblerCodes.add(code);
    }

    public int getSize(){
        return this.assemblerCodes.size();
    }
    
    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < assemblerCodes.size(); i++)
            s.append(String.format("[%d] %s\n", i, assemblerCodes.get(i)));

        return s.toString();
    }
}
