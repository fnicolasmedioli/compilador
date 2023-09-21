package compilador;

public abstract class Atributo {
    private String nombre;

    public Atributo(String nombre){
        this.nombre=nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean equals(Atributo atributo){
        return this.getNombre().equals(atributo.getNombre());
    }

    public abstract Comparable getAtributo();

}
