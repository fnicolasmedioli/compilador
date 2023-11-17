package compiler;

public enum DataType {

    STRING("STRING"),
    DOUBLE("DOUBLE", 64),
    UINT("UINT", 16),
    LONG("LONG", 32),
    OBJECT("OBJETO"),
    BOOLEAN("BOOLEAN");

    private final String str;
    private final Integer size; // Size in bits

    DataType(String str, Integer size)
    {
        this.str = str;
        this.size = size;
    }

    DataType(String str)
    {
        this.str = str;
        this.size = null;
    }

    @Override
    public String toString()
    {
        return this.str;
    }

    public Integer getSize()
    {
        return this.size;
    }
}