package compiler;

import java.util.Vector;

public class YACCDataUnit {

    public DataType dataType;
    public int firstTriplet;
    public int tripletQuantity;
    public String lexeme;
    public Vector<LocatedSymbolTableEntry> tokensData;
    public TripletOperand tripletOperand;

    public int reservedTriplet;

    public YACCDataUnit()
    {
        tokensData = new Vector<>();
    }
}