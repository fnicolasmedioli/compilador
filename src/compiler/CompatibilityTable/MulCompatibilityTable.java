package compiler.CompatibilityTable;

import compiler.*;

public class MulCompatibilityTable implements ICompatibilityTable {

    private static DataType[][] matrix;

    private static synchronized void initializeMatrix()
    {
        int dataTypesQuantity = DataType.values().length;
        matrix = new DataType[dataTypesQuantity][dataTypesQuantity];

        setSymmetric(_uint, _uint, DataType.LONG);
        setSymmetric(_long, _long, DataType.LONG);
        setSymmetric(_double, _double, DataType.DOUBLE);
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

    public MulCompatibilityTable()
    {
        synchronized (MulCompatibilityTable.class)
        {
            if (matrix == null)
                initializeMatrix();
        }
    }
}