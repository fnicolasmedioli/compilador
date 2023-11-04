package compiler;

public enum AttribKey {

    ID_TYPE("Tipo de identificador"),
    DATA_TYPE("Tipo de dato");

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