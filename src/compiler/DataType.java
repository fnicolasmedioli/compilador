package compiler;

public enum DataType {

    STRING("STRING", 4),
    DOUBLE("DOUBLE", 8),
    UINT("UINT", 2),
    LONG("LONG", 4),
    OBJECT("OBJETO"),
    BOOLEAN("BOOLEAN");

    private final String str;
    private final Integer size; // Size in bytes

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

    public boolean hasSize()
    {
        return this.size != null;
    }
}