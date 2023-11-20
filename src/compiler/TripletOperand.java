package compiler;

public class TripletOperand {

    public final Integer index;

    public final SymbolTableEntry stEntry;

    public final ListOfTriplets listOfTriplets;

    public TripletOperand(Integer index, ListOfTriplets listOfTriplets)
    {
        this.index = index;
        this.stEntry = null;
        this.listOfTriplets = listOfTriplets;
    }

    public TripletOperand(SymbolTableEntry stEntry, ListOfTriplets listOfTriplets)
    {
        this.index = null;
        this.stEntry = stEntry;
        this.listOfTriplets = listOfTriplets;
    }

    public boolean isFinal()
    {
        return this.stEntry != null;
    }

    @Override
    public String toString()
    {
        if (isFinal())
            return "Entrada en la tabla de simbolos: " + stEntry.getLexeme();
        else
            return "Triplet: " + index;
    }

    public Integer getIndex() {
        return index;
    }

    public SymbolTableEntry getstEntry(){
        return this.stEntry;
    }

    public MemoryAssociation getMemoryAssociation()
    {
        if (isFinal())
            return (MemoryAssociation)stEntry.getAttrib(AttribKey.MEMORY_ASSOCIATION);
        else
            return listOfTriplets.getTriplet(index).getMemoryAssociation();
    }
}
