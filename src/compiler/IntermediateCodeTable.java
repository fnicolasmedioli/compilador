package compiler;

import java.util.List;
import java.util.Vector;

public class IntermediateCodeTable{
    //Lista donde cada nodo es un terceto
    private Vector<Terceto> listTercetos;

    public void arrTercetos(){
        this.listTercetos = new Vector<>();
    }

    public void addTerceto(Terceto t){
        this.listTercetos.add(t);
    }

    public List<Terceto> getListTercetos(){
        return this.listTercetos;
    }

}