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

    private String loadFromMemory(MemoryAssociation memoryAssociation, String register)
    {
        if (memoryAssociation == null)
        {
            System.out.println("loadFromMemory() - memoryAssociation == null");
            return "";
        }

        String s = "";

        String tag = memoryAssociation.getTag();
        int offset = memoryAssociation.getOffset();
        DataType dataType = memoryAssociation.getDataType();

        if (dataType == null)
        {
            System.out.println("La memoryAssociation no tiene dataType: " + memoryAssociation);
            return "";
        }

        if (!memoryAssociation.usesOffset())
        {
            switch (dataType)
            {
                case LONG:
                    s += String.format("mov %s, %s\n", register, tag);
                    break;
                case STRING:
                    s += String.format("mov %s, addr %s\n", register, tag);
                    break;
                case UINT:
                    s += String.format("mov %s, %s\n", register, tag);
                    break;
                case DOUBLE:
                    s += String.format("fld %s\n", tag);
                    break;
                default:
                    s += "No deberia estar viendo esto\n";
                    break;
            }
        }
        else
        {
            switch (dataType)
            {
                case LONG:
                    s += String.format("mov %s, dword ptr [edx + %d]\n", register, offset);
                    break;
                case STRING:
                    s += String.format("mov %s, addr [edx + %d]\n", register, offset);
                    break;
                case UINT:
                    s += String.format("mov %s, word ptr [edx + %d]\n", register, offset);
                    break;
                case DOUBLE:
                    s += String.format("fld qword ptr [edx + %d]\n", offset);
                    break;
                default:
                    s += "No deberia estar viendo esto\n";
                    break;
            }
        }
        return s;
    }

    private String saveToMemory(MemoryAssociation memoryAssociation, String register)
    {
        String s = "";

        String tag = memoryAssociation.getTag();
        int offset = memoryAssociation.getOffset();
        DataType dataType = memoryAssociation.getDataType();

        if (!memoryAssociation.usesOffset())
        {
            switch (dataType)
            {
                case LONG:
                    s += String.format("mov %s, %s\n", tag, register);
                    break;
                case STRING:
                    s += String.format("mov %s, %s\n", tag, register);
                    break;
                case UINT:
                    s += String.format("mov %s, %s\n", tag, register);
                    break;
                case DOUBLE:
                    s += String.format("fstp %s\n", tag);
                    break;
                default:
                    s += "No deberia estar viendo esto\n";
                    break;
            }
        }
        else
        {
            switch (dataType)
            {
                case LONG:
                    s += String.format("mov dword ptr [edx + %d], %s\n", offset, register);
                    break;
                case STRING:
                    s += String.format("mov dword ptr [edx + %d], %s\n", offset, register);
                    break;
                case UINT:
                    s += String.format("mov word ptr [edx + %d], %s\n", offset, register);
                    break;
                case DOUBLE:
                    s += String.format("fstp qword ptr [edx + %d]\n", offset);
                    break;
                default:
                    s += "No deberia estar viendo esto\n";
                    break;
            }
        }
        return s;
    }


    private String loadDoubleFromMemory(MemoryAssociation memoryAssociation, boolean isInteger)
    {
        String s = "";

        String mnemonic = (isInteger) ? "fild" : "fld";

        String tag = memoryAssociation.getTag();
        int offset = memoryAssociation.getOffset();
        DataType dataType = memoryAssociation.getDataType();

        if (!memoryAssociation.usesOffset())
            s += String.format("%s qword ptr [%s]\n", mnemonic, tag);
        else
            s += String.format("%s qword ptr [edx + %d]\n", mnemonic, offset);

        return s;
    }

    public String translateAdd(Triplet triplet)
    {
        TripletOperand o1 = triplet.getOperand1();
        TripletOperand o2 = triplet.getOperand2();
        MemoryAssociation o1MemoryAssociation = o1.getMemoryAssociation();
        MemoryAssociation o2MemoryAssociation = o2.getMemoryAssociation();
        DataType operandsType = o1.getMemoryAssociation().getDataType();
        MemoryAssociation resultMemoryAssociation = triplet.getMemoryAssociation();

        String s = "";

        switch (operandsType)
        {
            case LONG:
                s += loadFromMemory(o1MemoryAssociation, "eax");
                s += loadFromMemory(o2MemoryAssociation, "ebx");
                s += String.format("add eax, ebx\n");
                s += saveToMemory(resultMemoryAssociation, "eax");
                break;
            case UINT:
                s += loadFromMemory(o1MemoryAssociation, "ax");
                s += loadFromMemory(o2MemoryAssociation, "bx");
                s += String.format("add ax, bx\n");
                s += saveToMemory(resultMemoryAssociation, "ax");
                break;
            case DOUBLE:
                s += loadDoubleFromMemory(o1MemoryAssociation, false);
                s += loadDoubleFromMemory(o2MemoryAssociation, false);
                s += "fadd\n";
                s += saveToMemory(resultMemoryAssociation, null);
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
        MemoryAssociation o1MemoryAssociation = o1.getMemoryAssociation();
        MemoryAssociation o2MemoryAssociation = o2.getMemoryAssociation();
        DataType operandsType = o1.getMemoryAssociation().getDataType();
        MemoryAssociation resultMemoryAssociation = triplet.getMemoryAssociation();

        String s = "";

        switch (operandsType)
        {
            case LONG:
                s += loadFromMemory(o1MemoryAssociation, "eax");
                s += loadFromMemory(o2MemoryAssociation, "ebx");
                s += String.format("sub eax, ebx\n");
                s += saveToMemory(o1MemoryAssociation, "eax");
                break;
            case UINT:
                s += loadFromMemory(o1MemoryAssociation, "ax");
                s += loadFromMemory(o2MemoryAssociation, "bx");
                s += String.format("sub ax, bx\n");
                s += saveToMemory(resultMemoryAssociation, "ax");
                break;
            case DOUBLE:
                s += loadDoubleFromMemory(o1MemoryAssociation, false);
                s += loadDoubleFromMemory(o2MemoryAssociation, false);
                s += "fsub\n";
                s += saveToMemory(resultMemoryAssociation, null);
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
        MemoryAssociation o1MemoryAssociation = o1.getMemoryAssociation();
        MemoryAssociation o2MemoryAssociation = o2.getMemoryAssociation();
        DataType operandsType = o1.getMemoryAssociation().getDataType();
        MemoryAssociation resultMemoryAssociation = triplet.getMemoryAssociation();

        String s = "";

        switch(operandsType)
        {
            case LONG:
                s += loadFromMemory(o1MemoryAssociation, "eax");
                s += loadFromMemory(o2MemoryAssociation, "ebx");
                s += String.format("mul ebx\n");
                s += "test edx, edx\n";
                s += "jnz .overflow\n";
                s += saveToMemory(resultMemoryAssociation, "eax");
                break;
            case UINT:
                s += loadFromMemory(o1MemoryAssociation, "ax");
                s += loadFromMemory(o2MemoryAssociation, "bx");
                s += String.format("mul bx\n");
                // Resultado queda en ax:dx
                s += "test dx, dx\n";
                s += "jnz .overflow\n";
                s += saveToMemory(resultMemoryAssociation, "ax");
                break;
            case DOUBLE:
                s += loadDoubleFromMemory(o1MemoryAssociation, false);
                s += loadDoubleFromMemory(o2MemoryAssociation, false);
                s += "fmul\n";
                s += saveToMemory(resultMemoryAssociation, null);
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
        MemoryAssociation o1MemoryAssociation = o1.getMemoryAssociation();
        MemoryAssociation o2MemoryAssociation = o2.getMemoryAssociation();
        DataType operandsType = o1.getMemoryAssociation().getDataType();
        MemoryAssociation resultMemoryAssociation = triplet.getMemoryAssociation();

        String s = "";

        switch (operandsType)
        {
            case LONG:
            case UINT:
                // Primero el dividendo (va en ST1)
                s += loadDoubleFromMemory(o1MemoryAssociation, true);
                s += loadDoubleFromMemory(o2MemoryAssociation, true);
                s += "fdiv\n";
                s += saveToMemory(resultMemoryAssociation, null);
                break;
            case DOUBLE:
                s += loadDoubleFromMemory(o1MemoryAssociation, false);
                s += loadDoubleFromMemory(o2MemoryAssociation, false);
                s += "fdiv\n";
                s += saveToMemory(resultMemoryAssociation, null);
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
        MemoryAssociation o1MemoryAssociation = o1.getMemoryAssociation();
        MemoryAssociation o2MemoryAssociation = o2.getMemoryAssociation();

        DataType operandsType = o1.getMemoryAssociation().getDataType();

        String s = "";

        switch(operandsType)
        {
            case LONG:
            case STRING:
                s += loadFromMemory(o2MemoryAssociation, "eax");
                s += saveToMemory(o1MemoryAssociation, "eax");
                break;
            case UINT:
                s += loadFromMemory(o2MemoryAssociation, "ax");
                s += saveToMemory(o1MemoryAssociation, "ax");
                break;
            case DOUBLE:
                s += loadDoubleFromMemory(o2MemoryAssociation, false);
                s += saveToMemory(o1MemoryAssociation, null);
                break;
        }

        return s;
    }

    public String translatePrint(Triplet triplet)
    {
        TripletOperand o1 = triplet.getOperand1();

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
        MemoryAssociation o1MemoryAssociation = o1.getMemoryAssociation();
        MemoryAssociation o2MemoryAssociation = o2.getMemoryAssociation();
        DataType operandsType = o1.getMemoryAssociation().getDataType();
        MemoryAssociation resultMemoryAssociation = triplet.getMemoryAssociation();

        String s = "";

        s += loadFromMemory(o1MemoryAssociation, "eax");
        s += loadFromMemory(o2MemoryAssociation, "ebx");
        s += "cmp eax, ebx\n";

        return s;
    }

    public String translateCJump(Triplet jumpTriplet, Triplet compTriplet)
    {
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

        System.out.println(stEntry);

        Object _argEntryKey = stEntry.getAttrib(AttribKey.ARG_ENTRY_KEY);
        String argEntryKey = ((_argEntryKey != null) ? (String)_argEntryKey : null);

        if (argEntryKey != null)
        {
            MemoryAssociation o2MemoryAssociation = o2.getMemoryAssociation();
            String argTag = o2.getMemoryAssociation().getTag();
            DataType argDataType = o2.getMemoryAssociation().getDataType();

            switch (argDataType)
            {
                case LONG:
                    s += loadFromMemory(o2MemoryAssociation, "eax");
                    s += String.format("mov %s, eax\n", SymbolTable.encodeString(argEntryKey));
                    break;
                case UINT:
                    s += loadFromMemory(o2MemoryAssociation, "ax");
                    s += String.format("mov %s, ax\n", SymbolTable.encodeString(argEntryKey));
                    break;
                case DOUBLE:
                    s += loadDoubleFromMemory(o2MemoryAssociation, false);
                    s += String.format("fstp %s\n", SymbolTable.encodeString(argEntryKey));
                    break;
                case STRING:
                    s += loadFromMemory(o2MemoryAssociation, "eax");
                    s += String.format("mov %s, eax\n", SymbolTable.encodeString(argEntryKey));
                    break;
                default:
                    s += "No deberia estar viendo esto\n";
                    break;
            }
        }

        // Setear en edx el valor de this

        s += "call " + stEntry.getAttrib(AttribKey.ASSEMBLY_TAG) + "\n";

        return s;
    }


    public String translateThis(Triplet triplet)
    {
        TripletOperand o1 = triplet.getOperand1();

        MemoryAssociation memoryAssociation = (MemoryAssociation)o1.getstEntry().getAttrib(AttribKey.MEMORY_ASSOCIATION);

        return String.format("mov edx, addr %s\n", memoryAssociation.getTag());
    }

}
