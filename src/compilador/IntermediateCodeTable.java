package compilador;

import java.util.ArrayList;
import java.util.List;

public class IntermediateCodeTable{
    //Lista donde cada nodo es un terceto
    private List<Terceto> listTercetos;

    public void arrTercetos(){
        this.listTercetos = new ArrayList<>();
    }

    public void addTerceto(Terceto t){
        this.listTercetos.add(t);
    }

    public List<Terceto> getListTercetos(){
        return this.listTercetos;
    }

}