package compiler.semantic;

import compiler.*;

public class Invocation {

    public LocatedSymbolTableEntry idTokenData;

    public LocatedSymbolTableEntry argDataTypeTokenData;

    public LocatedSymbolTableEntry argNameTokendata;

    public Invocation(LocatedSymbolTableEntry idTokenData, LocatedSymbolTableEntry argDataTypeTokenData, LocatedSymbolTableEntry argNameTokendata)
    {
        this.idTokenData = idTokenData;
        this.argDataTypeTokenData = argDataTypeTokenData;
        this.argNameTokendata = argNameTokendata;
    }
}