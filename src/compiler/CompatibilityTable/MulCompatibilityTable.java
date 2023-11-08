package compiler.CompatibilityTable;

public class MulCompatibilityTable extends TypeCompatibilityTable{

    public MulCompatibilityTable(){
        super();

        this.table[1][1] = "long";
        this.table[1][2] = "long";
        this.table[1][3] = "float";
        this.table[1][4] = "double";
        this.table[1][5] = "";

        this.table[2][1] = "long";
        this.table[2][2] = "long";
        this.table[2][3] = "double";
        this.table[2][4] = "double";
        this.table[2][5] = "";

        this.table[3][1] = "float";
        this.table[3][2] = "double";
        this.table[3][3] = "double";
        this.table[3][4] = "double";
        this.table[3][5] = "";

        this.table[4][1] = "double";
        this.table[4][2] = "double";
        this.table[4][3] = "double";
        this.table[4][4] = "double";
        this.table[4][5] = "";

        this.table[5][1] = "";
        this.table[5][2] = "";
        this.table[5][3] = "";
        this.table[5][4] = "";
        this.table[5][5] = "";
    }
}
