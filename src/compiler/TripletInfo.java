package compiler;

public class TripletInfo {

    public boolean procedureInit = false;
    public boolean procedureEnd = false;

    public TripletInfo setProcedureInit() {
        this.procedureInit = true;
        return this;
    }
    public TripletInfo setProcedureEnd() {
        this.procedureEnd = true;
        return this;
    }

    @Override
    public String toString()
    {
        return String.format("ProcedureInit: %s | ProcedureEnd: %s", procedureInit, procedureEnd);
    }

}
