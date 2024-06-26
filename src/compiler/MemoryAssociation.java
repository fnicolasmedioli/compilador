package compiler;

public class MemoryAssociation {

    private String tag = null;
    private int offset = -1;
    private int size = -1;
    private DataType dataType = null;
    private boolean isConstant = false;
    private boolean offsetRespectEBP = false;

    public MemoryAssociation(String tag, int size, DataType dataType)
    {
        this.tag = SymbolTable.encodeString(tag);
        this.size = size;
        this.dataType = dataType;
    }

    public MemoryAssociation(int offset, int size, DataType dataType)
    {
        this.offset = offset;
        this.size = size;
        this.dataType = dataType;
    }

    public MemoryAssociation(String tag)
    {
        this.tag = SymbolTable.encodeString(tag);
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

    public boolean usesOffset()
    {
        return this.offset != -1;
    }

    @Override
    public String toString()
    {
        if (offset != -1)
            return "Offset respecto a EBP: " + offsetRespectEBP + " | Offset: '" + offset + "' | DataType -> '" + dataType + "'";

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

    public int getOffset()
    {
        return offset;
    }

    public void setConstant(boolean isConstant)
    {
        this.isConstant = isConstant;
    }

    public boolean isConstant()
    {
        return isConstant;
    }

    public MemoryAssociation setOffsetRespectEBP()
    {
        this.offsetRespectEBP = true;
        return this;
    }

    public boolean isOffsetRespectEBP()
    {
        return offsetRespectEBP;
    }
}
