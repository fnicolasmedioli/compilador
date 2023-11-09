package compiler.CompatibilityTable;

import compiler.*;

public class MulCompatibilityTable {

    private static DataType[][] matrix;

    private static final int _uint = DataType.UINT.ordinal();
    private static final int _long = DataType.LONG.ordinal();
    private static final int _string = DataType.STRING.ordinal();
    private static final int _double = DataType.DOUBLE.ordinal();
    private static final int _object = DataType.OBJECT.ordinal();

    private static synchronized void initializeMatrix()
    {
        int dataTypesQuantity = DataType.values().length;
        matrix = new DataType[dataTypesQuantity][dataTypesQuantity];


    }

    public DataType calcDataType(DataType a, DataType b)
    {
        return matrix[a.ordinal()][b.ordinal()];
    }

    private static synchronized void setSymmetric(int a, int b, DataType d)
    {
        matrix[a][b] = d;
        matrix[b][a] = d;
    }

    MulCompatibilityTable()
    {
        synchronized (MulCompatibilityTable.class)
        {
            if (matrix == null)
                initializeMatrix();
        }
    }
}