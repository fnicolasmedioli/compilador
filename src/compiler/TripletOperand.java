package compiler;

public class TripletOperand {

    public final Integer index;

    public final SymbolTableEntry stEntry;

    public TripletOperand(Integer index)
    {
        this.index = index;
        this.stEntry = null;
    }

    public TripletOperand(SymbolTableEntry stEntry)
    {
        this.index = null;
        this.stEntry = stEntry;
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
}
