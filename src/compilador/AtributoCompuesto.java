package compilador;

public abstract class AtributoCompuesto extends Atributo{
    private Atributo a1;
    private Atributo a2;

    public AtributoCompuesto(String nombre, Atributo a1, Atributo a2){
        super(nombre);
        this.a1=a1;
        this.a2=a2;
    }

    public Atributo getA1() {
        return a1;
    }

    public void setA1(Atributo a1) {
        this.a1 = a1;
    }

    public Atributo getA2() {
        return a2;
    }

    public void setA2(Atributo a2) {
        this.a2 = a2;
    }


}