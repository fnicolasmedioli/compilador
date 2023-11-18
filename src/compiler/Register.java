package compiler;

public class Register {
    private String register;
    private int indexListOfTiplet;

    public Register(String register, int indexListOfTiplet) {
        this.register = register;
        this.indexListOfTiplet = indexListOfTiplet;
    }

    public void setRegister(String register) {
        this.register = register;
    }

    public void setIndexListOfTiplet(int indexListOfTiplet) {
        this.indexListOfTiplet = indexListOfTiplet;
    }

    public String getRegister() {
        return register;
    }

    public int getIndexListOfTiplet() {
        return indexListOfTiplet;
    }

    
    
}
