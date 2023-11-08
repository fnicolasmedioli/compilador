package compiler;

import java.util.Vector;

public class ListOfTecretos {
    private Vector<Terceto> tercetos;

    public ListOfTecretos(Vector<Terceto> tercetos) {
        this.tercetos = new Vector<Terceto>();
    }


    public void setTercetos(Vector<Terceto> tercetos) {
        this.tercetos = tercetos;
    }

    public Vector<Terceto> getTercetos() {
        return tercetos;
    }

    public void addTerceto(Terceto t){
        this.tercetos.add(t);
    }

    public Terceto getTerceto(int pos){
        return this.tercetos.elementAt(pos);
    }
    
}
