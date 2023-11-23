package compiler;

public enum AttribKey {

    ID_TYPE("Tipo de identificador"),
    DATA_TYPE("Tipo de dato"),
    INSTANCE_OF("Instancia de clase"),
    ARG_TYPE("Tipo de argumento"),
    ARG_ENTRY_KEY("Entry Key del argumento"),
    ATTRIBS_SET("Conjunto de atributos"),
    ATTRIBS_OFFSETS("Offsets de atributos"),
    METHODS_SET("Conjunto de metodos"),
    FIRST_TRIPLET("Primer terceto de la instruccion"),
    MEMORY_ASSOCIATION("Asociacion de memoria"),
    IS_CONSTANT("Es una constante"),
    ASSEMBLY_TAG("Tag en assembly"),
    METHOD_OF("Clase asociada al metodo"),
    ATTRIB_OF_CLASS("Atributo de clase"),
    ORIGINAL_KEY("Key del atributo original"),
    IS_AUX_VAR("Es variable auxiliar");

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