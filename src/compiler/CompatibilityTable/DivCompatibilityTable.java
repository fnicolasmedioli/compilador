package compiler.CompatibilityTable;

import compiler.DataType;

public class DivCompatibilityTable implements ICompatibilityTable{
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

        setSymmetric(_uint, _uint, DataType.DOUBLE);

        setSymmetric(_uint, _long, DataType.DOUBLE);
        // matrix[_uint][_double] = DataType.DOUBLE;

        setSymmetric(_long, _long, DataType.DOUBLE);
        // matrix[_long][_double] = DataType.DOUBLE;

        setSymmetric(_double, _double, DataType.DOUBLE);

        setSymmetric(_double, _long, DataType.DOUBLE);

        setSymmetric(_double, _uint, DataType.DOUBLE);

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

    public DivCompatibilityTable()
    {
        synchronized (DivCompatibilityTable.class)
        {
            if (matrix == null)
                initializeMatrix();
        }
    }
}