package compiler.x86;

import compiler.*;

public class TripletTranslator {

    private final SymbolTable symbolTable;

    public TripletTranslator(SymbolTable symbolTable)
    {
        this.symbolTable = symbolTable;
    }

    public String translateAdd(Triplet triplet)
    {
        TripletOperand o1 = triplet.getOperand1();
        TripletOperand o2 = triplet.getOperand2();

        String s = "";

        s += String.format("mov eax, %s\n", o1.getMemoryAssociation().getTag());
        s += String.format("add eax, %s\n", o2.getMemoryAssociation().getTag());
        s += String.format("mov %s, eax\n", triplet.getMemoryAssociation().getTag());

        return s;
    }

    public String translateAssign(Triplet triplet)
    {
        TripletOperand o1 = triplet.getOperand1();
        TripletOperand o2 = triplet.getOperand2();

        String s = "";

        s += String.format("mov eax, %s\n", o2.getMemoryAssociation().getTag());
        s += String.format("mov %s, eax\n", o1.getMemoryAssociation().getTag());

        return s;
    }
}
