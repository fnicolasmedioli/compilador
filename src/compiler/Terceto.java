package compiler;

public class Terceto <Tipo1, Tipo2>{
    private String operation;
    private Tipo1 o1;
    private Tipo2 o2;
    private String type;

    public Terceto(){

    }

    public Terceto(String operation, Tipo1 o1, Tipo2 o2){
        this.operation = operation;
        this.o1=o1;
        this.o2=o2;    
    }

    public void setType(String t){
        this.type = t;
    }

    public String getType(){
        return this.type;
    }
    public String getOperation(){
        return this.operation;
    }

    public Tipo1 getOperating1(){
        return this.o1;
    }

    public Tipo2 getOperating2(){
        return this.o2;
    }

    public void setOperation(String operation){
        this.operation = operation;
    }

    public void setOperating1(Tipo1 o1){
        this.o1 = o1;
    }

    public void setOperating2(Tipo2 o2){
       this.o2 = o2;
    }
}
