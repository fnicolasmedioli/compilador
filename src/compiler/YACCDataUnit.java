package compiler;

import java.util.Vector;

public class YACCDataUnit {

    public DataType dataType;
    public Integer firstTriplet;
    public int tripletQuantity;
    public String lexeme;
    public Vector<LocatedSymbolTableEntry> tokensData;
    public TripletOperand tripletOperand;
    public int reservedTriplet;
    public String referencedEntryKey;
    protected boolean valid;
    public boolean forwarded = false;

    public LocatedSymbolTableEntry getLastTokenData()
    {
        return tokensData.get(tokensData.size() - 1);
    }

    public boolean isValid()
    {
        return valid;
    }

    public YACCDataUnit()
    {
        valid = true;
        tokensData = new Vector<>();
    }

    public void setValid(boolean v)
    {
        valid = v;
    }
}