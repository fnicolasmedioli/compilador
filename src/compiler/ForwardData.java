package compiler;

public class ForwardData {

    // Scope de donde se define la variable a la que se hace forward
    private String searchScope;
    private String classLexeme;
    private String varLexeme;

    public ForwardData(String searchScope, String classLexeme, String varLexeme) {
        this.searchScope = searchScope;
        this.classLexeme = classLexeme;
        this.varLexeme = varLexeme;
    }

    public String getSearchScope() {
        return searchScope;
    }

    public void setSearchScope(String searchScope) {
        this.searchScope = searchScope;
    }

    public String getClassLexeme() {
        return classLexeme;
    }

    public void setClassLexeme(String classLexeme) {
        this.classLexeme = classLexeme;
    }

    public String getVarLexeme() {
        return varLexeme;
    }

    public void setVarLexeme(String varLexeme) {
        this.varLexeme = varLexeme;
    }
}
