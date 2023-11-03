package compiler.CompatibilityTable;

public class TypeCompatibilityTable {
    String[][] table;

    public TypeCompatibilityTable(){
        this.table = new String[5][5];
        this.table[0][1] = "int";
        this.table[1][0] = "int";
        this.table[0][2] = "long";
        this.table[2][0] = "long";
        this.table[0][3] = "float";
        this.table[3][0] = "float";
        this.table[0][4] = "double";
        this.table[4][0] = "double";
    }

    public String[][] getTable(){
        return this.table;
    }
}
