package compilador;

public class Terceto {
    private String operation;
    private String o1;
    private String o2;

    public Terceto(){

    }

    public Terceto(String operation, String o1, String o2){
        this.operation = operation;
        this.o1=o1;
        this.o2=o2;    
    }

    public String getOperation(){
        return this.operation;
    }

    public String getOperating1(){
        return this.o1;
    }

    public String getOperating2(){
        return this.o1;
    }

    public void setOperation(String operation){
        this.operation = operation;
    }

    public void setOperating1(String o1){
        this.o1 = o1;
    }

    public void setOperating2(String o2){
       this.o2 = o2;
    }
}
