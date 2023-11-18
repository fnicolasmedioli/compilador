package compiler;

public class MemoryAssociation {

    private String tag = null;
    private int size = -1;
    private DataType dataType = null;

    public MemoryAssociation(String tag, int size, DataType dataType)
    {
        this.tag = this.encode(tag);
        this.size = size;
        this.dataType = dataType;
    }

    public MemoryAssociation(String tag)
    {
        this.tag = this.encode(tag);
    }

    public MemoryAssociation(int size)
    {
        this.size = size;
    }

    public String getTag()
    {
        return tag;
    }

    public int getSize()
    {
        if (!hasSize())
            System.out.println("SE ACCEDE A SIZE SIN TENERLO");
        return size;
    }

    public DataType getDataType()
    {
        return dataType;
    }

    public void addSize(int size)
    {
        this.size += size;
    }

    public boolean hasSize()
    {
        return this.size != -1;
    }

    @Override
    public String toString()
    {
        if (tag == null)
            return "Size -> '" + size + "'";

        if (!hasSize())
            return "Tag -> '" + tag + "'";

        return "Tag -> '" +
                tag +
                "' | '" +
                "Size -> '" +
                size +
                "' | '" +
                "DataType -> " +
                dataType +
                "'";
    }

    private String encode(String str)
    {
        return str
            .replace(":", "_")
            .replace(".", "_")
            .replace("%", "_");
    }
}
