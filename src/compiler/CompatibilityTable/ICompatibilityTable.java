package compiler.CompatibilityTable;

import compiler.DataType;

public interface ICompatibilityTable {

    static final int _uint = DataType.UINT.ordinal();
    static final int _long = DataType.LONG.ordinal();
    static final int _string = DataType.STRING.ordinal();
    static final int _double = DataType.DOUBLE.ordinal();
    static final int _object = DataType.OBJECT.ordinal();
    static final int _boolean = DataType.BOOLEAN.ordinal();
    
    public DataType calcDataType(DataType a, DataType b);

}
