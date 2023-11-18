package compiler;

public enum AttribKey {

    ID_TYPE("Tipo de identificador"),
    DATA_TYPE("Tipo de dato"),
    INSTANCE_OF("Instancia de clase"),
    ARG_TYPE("Tipo de argumento"),
    ATTRIBS_SET("Conjunto de atributos"),
    METHODS_SET("Conjunto de metodos"),
    FIRST_TRIPLET("Primer terceto de la instruccion"),
    MEMORY_ASSOCIATION("Asociacion de memoria"),
    IS_CONSTANT("Es una constante");

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