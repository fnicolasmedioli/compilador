package compiler;

public class AssemblerCode {
    private String operation;
    private String r1;
    private String r2;
    
    public AssemblerCode(String operation, String r1, String r2) {
        this.operation = operation;
        this.r1 = r1;
        this.r2 = r2;
    }

    
    public AssemblerCode() {
    }


    public String getOperation() {
        return operation;
    }

    public String getR1() {
        return r1;
    }

    public String getR2() {
        return r2;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public void setR1(String r1) {
        this.r1 = r1;
    }

    public void setR2(String r2) {
        this.r2 = r2;
    }

    @Override
    public String toString()
    {
        return String.format("AssemblerCode: %s [%s] [%s]", operation, getR1(), getR2());
    }
}
