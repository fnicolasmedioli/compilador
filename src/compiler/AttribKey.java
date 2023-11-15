package compiler;

public enum AttribKey {

    ID_TYPE("Tipo de identificador"),
    DATA_TYPE("Tipo de dato"),
    INSTANCE_OF("Instancia de clase"),
    ARG_TYPE("Tipo de argumento"),
    ATTRIBS_MAP("Mapa de atributos");

    private final String str;
    AttribKey(String str)
    {
        this.str = str;
    }

    @Override
    public String toString()
    {
        return this.str;
    }
}