package compiler;

public enum DataType {

    STRING("STRING"),
    DOUBLE("DOUBLE"),
    UINT("UINT"),
    LONG("LONG"),
    OBJECT("OBJETO"),
    BOOLEAN("BOOLEAN");

    private final String str;
    DataType(String str)
    {
        this.str = str;
    }

    @Override
    public String toString()
    {
        return this.str;
    }
}