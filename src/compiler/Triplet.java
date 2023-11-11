package compiler;

public class Triplet {
    private final String operation;
    private final TripletOperand o1;
    private final TripletOperand o2;
    private DataType type;

    public Triplet(String operation, TripletOperand o1, TripletOperand o2) {
        this.operation = operation;
        this.o1=o1;
        this.o2=o2;
        this.type = null;
    }

    public Triplet(String operation, TripletOperand o1, TripletOperand o2, DataType type) {
        this.operation = operation;
        this.o1 = o1;
        this.o2 = o2;
        this.type = type;
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
        return String.format("Triplet: %s [%s] [%s] [%s]", operation, getOperand1(), getOperand2(), type);
    }

    public void setDataType(DataType dataType){
        this.type = dataType;
    }

}


