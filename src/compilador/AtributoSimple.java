package compilador;

public class AtributoSimple extends Atributo{
    private Comparable valor;

    public AtributoSimple(String nombre, Comparable valor){
        super(nombre);
        this.valor=valor;
    }

    public Comparable getAtributo() {
        return this.valor;
    }

    public void setValor(Comparable valor) {
        this.valor = valor;
    }

}
