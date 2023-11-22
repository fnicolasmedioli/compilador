package compiler.x86;

import compiler.*;

public class TripletTranslator {

    private final SymbolTable symbolTable;
    private final ListOfTriplets listOfTriplets;

    public TripletTranslator(SymbolTable symbolTable, ListOfTriplets listOfTriplets)
    {
        this.symbolTable = symbolTable;
        this.listOfTriplets = listOfTriplets;
    }

    public String translateAdd(Triplet triplet)
    {
        TripletOperand o1 = triplet.getOperand1();
        TripletOperand o2 = triplet.getOperand2();
        DataType operandsType = o1.getMemoryAssociation().getDataType();

        String s = "";

        switch (operandsType)
        {
            case LONG:
                s += String.format("mov eax, %s\n", o1.getMemoryAssociation().getTag());
                s += String.format("add eax, %s\n", o2.getMemoryAssociation().getTag());
                s += String.format("mov %s, eax\n", triplet.getMemoryAssociation().getTag());
                break;
            case UINT:
                s += String.format("mov ax, %s\n", o1.getMemoryAssociation().getTag());
                s += String.format("mov cx, %s\n", o2.getMemoryAssociation().getTag());
                s += "add ax, cx\n";
                s += String.format("mov %s, ax\n", triplet.getMemoryAssociation().getTag());
                break;
            case DOUBLE:
                s += String.format("fld %s\n", o1.getMemoryAssociation().getTag());
                s += String.format("fld %s\n", o2.getMemoryAssociation().getTag());
                s += "fadd\n";
                s += String.format("fstp %s\n", triplet.getMemoryAssociation().getTag());
                break;
            default:
                s += "No deberia estar viendo esto\n";
                break;
        }

        return s;
    }

    public String translateSub(Triplet triplet)
    {
        TripletOperand o1 = triplet.getOperand1();
        TripletOperand o2 = triplet.getOperand2();
        DataType operandsType = o1.getMemoryAssociation().getDataType();

        String s = "";

        switch (operandsType)
        {
            case LONG:
                s += String.format("mov eax, %s\n", o1.getMemoryAssociation().getTag());
                s += String.format("sub eax, %s\n", o2.getMemoryAssociation().getTag());
                s += String.format("mov %s, eax\n", triplet.getMemoryAssociation().getTag());
                break;
            case UINT:
                s += String.format("mov ax, %s\n", o1.getMemoryAssociation().getTag());
                s += String.format("mov cx, %s\n", o2.getMemoryAssociation().getTag());
                s += "sub ax, cx\n";
                s += String.format("mov %s, ax\n", triplet.getMemoryAssociation().getTag());
                break;
            case DOUBLE:
                s += String.format("fld %s\n", o1.getMemoryAssociation().getTag());
                s += String.format("fld %s\n", o2.getMemoryAssociation().getTag());
                s += "fsub\n";
                s += String.format("fstp %s\n", triplet.getMemoryAssociation().getTag());
                break;
            default:
                s += "No deberia estar viendo esto\n";
                break;
        }

        return s;
    }

    public String translateMul(Triplet triplet)
    {
        TripletOperand o1 = triplet.getOperand1();
        TripletOperand o2 = triplet.getOperand2();
        DataType operandsType = o1.getMemoryAssociation().getDataType();

        String s = "";

        switch(operandsType)
        {
            case LONG:
                s += String.format("mov eax, %s\n", o1.getMemoryAssociation().getTag());
                s += String.format("mul %s\n", o2.getMemoryAssociation().getTag());
                s += "test edx, edx\n";
                s += "jnz .overflow\n";
                s += String.format("mov %s, eax\n", triplet.getMemoryAssociation().getTag());
                break;
            case UINT:
                s += String.format("mov ax, %s\n", o1.getMemoryAssociation().getTag());
                s += String.format("mov cx, %s\n", o2.getMemoryAssociation().getTag());
                s += "mul cx\n";
                // Resultado queda en ax:dx
                s += "test dx, dx\n";
                s += "jnz .overflow\n";
                s += String.format("mov %s, ax\n", triplet.getMemoryAssociation().getTag());
                break;
            case DOUBLE:
                s += String.format("fild %s\n", o1.getMemoryAssociation().getTag());
                s += String.format("fild %s\n", o2.getMemoryAssociation().getTag());
                s += "fmul\n";
                s += String.format("fstp %s\n", triplet.getMemoryAssociation().getTag());
                break;
            default:
                s += "No deberia estar viendo esto\n";
                break;
        }

        return s;
    }

    public String translateDiv(Triplet triplet)
    {
        TripletOperand o1 = triplet.getOperand1();
        TripletOperand o2 = triplet.getOperand2();
        DataType operandsType = o1.getMemoryAssociation().getDataType();

        String s = "";

        switch (operandsType)
        {
            case LONG:
            case UINT:
                // Primero el dividendo (va en ST1)
                s += String.format("fild %s\n", o1.getMemoryAssociation().getTag());
                s += String.format("fild %s\n", o2.getMemoryAssociation().getTag());
                s += "fdiv\n";
                s += String.format("fst %s\n", triplet.getMemoryAssociation().getTag());
                break;
            case DOUBLE:
                s += String.format("fld %s\n", o1.getMemoryAssociation().getTag());
                s += String.format("fld %s\n", o2.getMemoryAssociation().getTag());
                s += "fdiv\n";
                s += String.format("fst %s\n", triplet.getMemoryAssociation().getTag());
                break;
            default:
                s += "No deberia estar viendo esto\n";
                break;
        }

        return s;
    }

    public String translateAssign(Triplet triplet)
    {
        TripletOperand o1 = triplet.getOperand1();
        TripletOperand o2 = triplet.getOperand2();
        DataType operandsType = o1.getMemoryAssociation().getDataType();

        String s = "";

        switch(operandsType)
        {
            case LONG:
                s += String.format("mov eax, %s\n", o2.getMemoryAssociation().getTag());
                s += String.format("mov %s, eax\n", o1.getMemoryAssociation().getTag());
                break;
            case UINT:
                s += String.format("mov ax, %s\n", o2.getMemoryAssociation().getTag());
                s += String.format("mov %s, ax\n", o1.getMemoryAssociation().getTag());
                break;
            case DOUBLE:
                s += String.format("fld %s\n", o2.getMemoryAssociation().getTag());
                s += String.format("fst %s\n", o1.getMemoryAssociation().getTag());
                break;
        }

        return s;
    }

    public String translatePrint(Triplet triplet)
    {
        TripletOperand o1 = triplet.getOperand1();
        TripletOperand o2 = triplet.getOperand2();

        String s = "";

        s += String.format("mov eax, addr %s\n", o1.getMemoryAssociation().getTag());
        s += "push eax\n";
        s += "call imprimir_mensaje\n";

        return s;
    }

    public String translateJMP(Triplet triplet)
    {
        TripletOperand o1 = triplet.getOperand1();

        String s = "";

        int tripletID = o1.getIndex();

        String tag = (!listOfTriplets.getTags(tripletID).isEmpty()) ? listOfTriplets.getTags(tripletID).getLast() : "Tag del JMP no encontrado\n";

        s += String.format("jmp %s\n", tag);

        return s;
    }

    public String translateCmp(Triplet triplet)
    {
        TripletOperand o1 = triplet.getOperand1();
        TripletOperand o2 = triplet.getOperand2();

        String s = "";

        s += String.format("mov eax, %s\n", o1.getMemoryAssociation().getTag());
        s += String.format("cmp eax, %s\n", o2.getMemoryAssociation().getTag());

        return s;
    }

    public String translateCJump(Triplet jumpTriplet, Triplet compTriplet)
    {
        TripletOperand o1 = compTriplet.getOperand1();
        TripletOperand o2 = compTriplet.getOperand2();

        String comp = compTriplet.getOperation();
        DataType type = compTriplet.getType();

        String jumpTag = (!listOfTriplets.getTags(jumpTriplet.getOperand1().getIndex()).isEmpty()) ? listOfTriplets.getTags(jumpTriplet.getOperand1().getIndex()).getLast() : "Tag del CJMP no encontrada\n";

        String s = "";

        if (type == DataType.LONG)
        {
            // Comparaciones con signo
            switch (comp)
            {
                case ">=":
                    s += String.format("jge %s\n", jumpTag);
                    break;
                case "<=":
                    s += String.format("jle %s\n", jumpTag);
                    break;
                case ">":
                    s += String.format("jg %s\n", jumpTag);
                    break;
                case "<":
                    s += String.format("jl %s\n", jumpTag);
                    break;
                case "==":
                    s += String.format("je %s\n", jumpTag);
                    break;
                case "!!":
                    s += String.format("jne %s\n", jumpTag);
                    break;
            }
        }

        if (type == DataType.UINT)
        {
            // Comparaciones sin signo
            switch (comp)
            {
                case ">=":
                    s += String.format("jae %s\n", jumpTag);
                    break;
                case "<=":
                    s += String.format("jbe %s\n", jumpTag);
                    break;
                case ">":
                    s += String.format("ja %s\n", jumpTag);
                    break;
                case "<":
                    s += String.format("jb %s\n", jumpTag);
                    break;
                case "==":
                    s += String.format("je %s\n", jumpTag);
                    break;
                case "!!":
                    s += String.format("jne %s\n", jumpTag);
                    break;
            }
        }

        if (type == DataType.DOUBLE)
            s += "Salto condicional de double no implementado\n";


        return s;
    }

    public String translateNegCJump(Triplet jumpTriplet, Triplet compTriplet)
    {
        String s = "";

        s += "pushfd\n";
        s += "pop eax\n";
        s += "not eax\n";
        s += "push eax\n";
        s += "popfd\n";
        s += translateCJump(jumpTriplet, compTriplet);

        return s;
    }

    public String translateCall(Triplet triplet)
    {
        TripletOperand o1 = triplet.getOperand1();
        TripletOperand o2 = triplet.getOperand2();

        String s = "";

        SymbolTableEntry stEntry = o1.getstEntry();

        Object _argEntryKey = stEntry.getAttrib(AttribKey.ARG_ENTRY_KEY);
        String argEntryKey = ((_argEntryKey != null) ? (String)_argEntryKey : null);

        if (argEntryKey != null)
        {
            String argTag = o2.getMemoryAssociation().getTag();
            DataType argDataType = o2.getMemoryAssociation().getDataType();

            switch (argDataType)
            {
                case LONG:
                    s += String.format("mov eax, %s\n", argTag);
                    s += String.format("mov %s, eax\n", SymbolTable.encodeString(argEntryKey));
                    break;
                case UINT:
                    s += String.format("mov ax, %s\n", argTag);
                    s += String.format("mov %s, ax\n", SymbolTable.encodeString(argEntryKey));
                    break;
                case DOUBLE:
                    s += String.format("fld %s\n", argTag);
                    // s += "fstp qword [esp]\n";
                    s += String.format("fstp %s\n", SymbolTable.encodeString(argEntryKey));
                    break;
                case STRING:
                    s += String.format("mov eax, addr %s\n", argTag);
                    s += String.format("mov %s, eax\n", SymbolTable.encodeString(argEntryKey));
                    break;
                default:
                    s += "No deberia estar viendo esto\n";
                    break;
            }
        }

        s += "call " + stEntry.getAttrib(AttribKey.ASSEMBLY_TAG) + "\n";

        return s;
    }

}
