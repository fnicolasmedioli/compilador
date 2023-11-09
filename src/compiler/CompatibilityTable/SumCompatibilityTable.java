package compiler.CompatibilityTable;

import compiler.*;

public class SumCompatibilityTable {

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

        setSymmetric(_uint, _uint, DataType.UINT);
        matrix[_uint][_long] = DataType.LONG;
        // matrix[_uint][_double] = DataType.DOUBLE;

        setSymmetric(_long, _long, DataType.LONG);
        // matrix[_long][_double] = DataType.DOUBLE;

        setSymmetric(_string, _string, DataType.STRING);

        setSymmetric(_double, _double, DataType.DOUBLE);
        // DOUBLE to UINT/LONG ??
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

    SumCompatibilityTable()
    {
        synchronized (SumCompatibilityTable.class)
        {
            if (matrix == null)
                initializeMatrix();
        }
    }
}