package compiler;

public class Triplet {
    private String operation;
    private final TripletOperand o1;
    private final TripletOperand o2;
    private DataType type;
    private static int cantTriplet = 0;
    private String varTriplet;
    private MemoryAssociation memoryAssociation = null;

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Triplet(String operation, TripletOperand o1, TripletOperand o2) {
        this.operation = operation;
        this.o1=o1;
        this.o2=o2;
        this.type = null;
        cantTriplet++;
        this.varTriplet = "@aux" + cantTriplet;
    }

    public Triplet(String operation, TripletOperand o1, TripletOperand o2, DataType type) {
        this.operation = operation;
        this.o1 = o1;
        this.o2 = o2;
        this.type = type;
        cantTriplet++;
        this.varTriplet = "@aux" + cantTriplet;
    }

    public String getVarTriplet() {
        return varTriplet;
    }

    public DataType getType() {
        return this.type;
    }

    public String getOperation() {
        return this.operation;
    }

    public TripletOperand getOperand1() {
        return this.o1;
    }

    public TripletOperand getOperand2() {
        return this.o2;
    }

    @Override
    public String toString()
    {
        return String.format("'%s' [%s] [%s] Type: %s [%s] Mem: %s", operation, getOperand1(), getOperand2(), type, varTriplet, memoryAssociation);
    }

    public void setDataType(DataType dataType) {
        this.type = dataType;
    }

    public void setMemoryAssociation(MemoryAssociation memoryAssociation){
        this.memoryAssociation = memoryAssociation;
    }

    public MemoryAssociation getMemoryAssociation(){
        return this.memoryAssociation;
    }
}


