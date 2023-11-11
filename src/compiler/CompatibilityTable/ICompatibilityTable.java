package compiler.CompatibilityTable;

import compiler.DataType;

public interface ICompatibilityTable {
    
    public DataType calcDataType(DataType a, DataType b);

}
