package compiler;

public class FlattenObjectItem {
    public MemoryAssociation memoryAssociation;
    public int offset;
    public FlattenObjectItem(MemoryAssociation memoryAssociation, int offset)
    {
        this.memoryAssociation = memoryAssociation;
        this.offset = offset;
    }

    @Override
    public String toString()
    {
        return "Tag -> " + memoryAssociation.getTag() + " Offset -> " + offset;
    }

    public MemoryAssociation getMemoryAssociation()
    {
        return memoryAssociation;
    }

    public int getOffset()
    {
        return offset;
    }
}