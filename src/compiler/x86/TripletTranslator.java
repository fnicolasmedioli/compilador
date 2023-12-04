package compiler.x86;

import compiler.*;

public class TripletTranslator {

    private final SymbolTable symbolTable;
    private final ListOfTriplets listOfTriplets;

    private boolean lastWasThis = false;

    public void setLastWasThis(boolean t)
    {
        lastWasThis = t;
    }

    public TripletTranslator(SymbolTable symbolTable, ListOfTriplets listOfTriplets)
    {
        this.symbolTable = symbolTable;
        this.listOfTriplets = listOfTriplets;
    }

    public String loadFromMemory(MemoryAssociation memoryAssociation, String register)
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
        boolean isConstant = memoryAssociation.isConstant();

        if (dataType == null)
        {
            System.out.println("La memoryAssociation no tiene dataType: " + memoryAssociation);
            return "";
        }

        if (memoryAssociation.usesOffset() && !lastWasThis)
        {
            if (memoryAssociation.isOffsetRespectEBP())
            {
                // Si es un offset respecto a EBP, hay que moverlo a EDX
                s += "mov edx, ebp\n";
            }
            else
            {
                // Si es un offset respecto al objeto, hay que moverlo a EDX
                s += "mov edx, dword ptr [ebp + 8]\n";
            }
        }

        if (!memoryAssociation.usesOffset())
        {
            switch (dataType)
            {
                case LONG:
                    s += String.format("mov %s, dword ptr [%s]\n", register, tag);
                    break;
                case STRING:
                    if (isConstant)
                        s += String.format("mov %s, offset %s\n", register, tag);
                    else
                        s += String.format("mov %s, dword ptr [%s]\n", register, tag);
                    break;
                case OBJECT:
                    s += String.format("mov %s, offset %s\n", register, tag);
                    break;
                case UINT:
                    s += String.format("mov %s, word ptr [%s]\n", register, tag);
                    break;
                case DOUBLE:
                    s += String.format("fld qword ptr [%s]\n", tag);
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
                    s += "mov ecx, edx\n";
                    s += String.format("add ecx, %d\n", offset);
                    s += String.format("mov %s, dword ptr [ecx]\n", register);
                    break;
                case OBJECT:
                    s += "mov ecx, edx\n";
                    s += String.format("add ecx, %d\n", offset);
                    s += String.format("mov %s, ecx\n", register);
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

        if (memoryAssociation.usesOffset() && !lastWasThis)
        {
            if (memoryAssociation.isOffsetRespectEBP())
            {
                // Si es un offset respecto a EBP, hay que moverlo a EDX
                s += "mov edx, ebp\n";
            }
            else
            {
                // Si es un offset respecto al objeto, hay que moverlo a EDX
                s += "mov edx, dword ptr [ebp + 8]\n";
            }
        }

        if (!memoryAssociation.usesOffset())
        {
            switch (dataType)
            {
                case LONG:
                    s += String.format("mov dword ptr [%s], %s\n", tag, register);
                    break;
                case STRING:
                    s += String.format("mov dword ptr [%s], %s\n", tag, register);
                    break;
                case UINT:
                    s += String.format("mov word ptr [%s], %s\n", tag, register);
                    break;
                case DOUBLE:
                    s += String.format("fstp qword ptr [%s]\n", tag);
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

        if (memoryAssociation.usesOffset())
        {
            if (memoryAssociation.isOffsetRespectEBP())
            {
                // Si es un offset respecto a EBP, hay que moverlo a EDX
                s += "mov edx, ebp\n";
            }
            else
            {
                // Si es un offset respecto al objeto, hay que moverlo a EDX
                s += "mov edx, dword ptr [ebp + 8]\n";
            }
        }

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
                s += "add eax, ebx\n";
                s += saveToMemory(resultMemoryAssociation, "eax");
                break;
            case UINT:
                s += loadFromMemory(o1MemoryAssociation, "ax");
                s += loadFromMemory(o2MemoryAssociation, "bx");
                s += "add ax, bx\n";
                s += saveToMemory(resultMemoryAssociation, "ax");
                break;
            case DOUBLE:
                s += loadDoubleFromMemory(o1MemoryAssociation, false);
                s += loadDoubleFromMemory(o2MemoryAssociation, false);
                s += "fadd\n";

                s += "fst qword ptr [___temp_double___]\n";
                s += "mov eax, dword ptr [___temp_double___]\n";
                s += "mov edx, dword ptr [___temp_double___ + 4]\n";

                s += "cmp eax, 0\n";
                s += "jne @@no_overflow_suma\n";
                s += "and edx, 7FF00000h\n";
                s += "cmp edx, 7FF00000h\n";
                s += "jne @@no_overflow_suma\n";
                s += "jmp @@overflow_suma\n";
                s += "@@no_overflow_suma:\n";

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
                s += String.format("cmp ax, bx\n");
                s += String.format("jb @@overflow_resta\n");
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
                s += "jnz @@overflow_mul\n";
                s += saveToMemory(resultMemoryAssociation, "eax");
                break;
            case UINT:
                s += loadFromMemory(o1MemoryAssociation, "ax");
                s += loadFromMemory(o2MemoryAssociation, "bx");
                s += String.format("mul bx\n");
                // Resultado queda en ax:dx
                s += "test dx, dx\n";
                s += "jnz @@overflow_mul\n";
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

                String reg = (operandsType == DataType.LONG) ? "eax" : "ax";

                s += loadFromMemory(o2MemoryAssociation, reg);
                s += String.format("cmp %s, 0\n", reg);
                s += "jz @@zero_div_error\n";

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
                s += "fstsw ax\n";
                s += "and eax, 4h\n";
                s += "cmp eax, 4h\n";
                s += "jz @@zero_div_error\n";
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

    private String retrieveAddress(MemoryAssociation mem, String register)
    {
        String s = "";

        if (mem.usesOffset())
        {
            if (mem.isOffsetRespectEBP())
            {
                // Si es un offset respecto a EBP, hay que moverlo a EDX
                s += "mov edx, ebp\n";
            }
            else
            {
                // Si es un offset respecto al objeto, hay que moverlo a EDX
                s += "mov edx, dword ptr [ebp + 8]\n";
            }
        }

        if (mem.usesOffset())
        {
            s += String.format("mov %s, edx\n", register);
            s += String.format("add %s, %d\n", register, mem.getOffset());
        }
        else
        {
            s += String.format("mov %s, offset %s\n", register, mem.getTag());
        }

        return s;
    }

    public String translatePrint(Triplet triplet)
    {
        TripletOperand o1 = triplet.getOperand1();

        if (o1 == null)
        {
            // Imprimir salto de linea

            String s = "";
            s += "push offset ___newline___\n";
            s += "call printf\n";
            s += "add esp, 4\n";

            return s;
        }

        String s = "";

        switch (o1.getMemoryAssociation().getDataType())
        {
            case LONG:
                s += loadFromMemory(o1.getMemoryAssociation(), "eax");
                s += "push eax\n";
                s += "push offset ___long_formatter___\n";
                s += "call printf\n";
                s += "add esp, 8\n";
                break;
            case UINT:
                s += loadFromMemory(o1.getMemoryAssociation(), "ax");
                s += "cwde\n";
                s += "push eax\n";
                s += "push offset ___uint_formatter___\n";
                s += "call printf\n";
                s += "add esp, 8\n";
                break;
            case DOUBLE:
                s += retrieveAddress(o1.getMemoryAssociation(), "ecx");
                s += "push dword ptr [ecx + 4]\n";
                s += "push dword ptr [ecx]\n";
                s += "push offset ___double_formatter___\n";
                s += "call printf\n";
                s += "add esp, 12\n";
                break;
            case STRING:
                s += loadFromMemory(o1.getMemoryAssociation(), "eax");
                s += "push eax\n";
                s += "push offset ___str_formatter___\n";
                s += "call printf\n";
                s += "add esp, 8\n";
                break;
            //case OBJECT:
            //    s += loadFromMemory(o1.getMemoryAssociation(), "eax");
            //    s += "call @@imprimir_objeto\n";
            //    break;
            default:
                s += "No deberia estar viendo esto\n";
                break;
        }

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

        switch (operandsType)
        {
            case LONG:
            case STRING:
            case OBJECT:
                s += loadFromMemory(o1MemoryAssociation, "eax");
                s += loadFromMemory(o2MemoryAssociation, "ebx");
                s += "cmp eax, ebx\n";
                break;
            case UINT:
                s += loadFromMemory(o1MemoryAssociation, "ax");
                s += loadFromMemory(o2MemoryAssociation, "bx");
                s += "cmp ax, bx\n";
                break;
            case DOUBLE:
                s += loadDoubleFromMemory(o2MemoryAssociation, false);
                s += loadDoubleFromMemory(o1MemoryAssociation, false);

                s += "fcom\n";
                s += "fstsw ax\n";
                s += "sahf\n";

                break;
            default:
                s += "No deberia estar viendo esto\n";
                break;
        }

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

        if (type == DataType.UINT || type == DataType.DOUBLE)
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

        //if (type == DataType.DOUBLE)
        //    s += "Salto condicional de double no implementado\n";

        return s;
    }

    private String negateJumpOP(String jumpOP)
    {
        switch (jumpOP)
        {
            case ">=":
                return "<";
            case "<=":
                return ">";
            case ">":
                return "<=";
            case "<":
                return ">=";
            case "==":
                return "!!";
            case "!!":
                return "==";
            default:
                return "No deberia estar viendo esto\n";
        }
    }

    public String translateNegCJump(Triplet jumpTriplet, Triplet compTriplet)
    {
        String op = negateJumpOP(compTriplet.getOperation());
        compTriplet.setOperation(op);

        String s = translateCJump(jumpTriplet, compTriplet);

        return s;
    }

    public String translateCall(Triplet triplet, Triplet prevTriplet)
    {
        TripletOperand o1 = triplet.getOperand1();
        TripletOperand o2 = triplet.getOperand2();

        String s = "";

        SymbolTableEntry stEntry = o1.getstEntry();

        Object _argEntryKey = stEntry.getAttrib(AttribKey.ARG_ENTRY_KEY);
        String argEntryKey = ((_argEntryKey != null) ? (String)_argEntryKey : null);

        int argSize = 0;

        if (argEntryKey != null)
        {
            MemoryAssociation o2MemoryAssociation = o2.getMemoryAssociation();
            String argTag = o2.getMemoryAssociation().getTag();
            DataType argDataType = o2.getMemoryAssociation().getDataType();

            switch (argDataType)
            {
                case LONG:
                    s += loadFromMemory(o2MemoryAssociation, "eax");
                    // s += String.format("mov dword ptr [%s], eax\n", SymbolTable.encodeString(argEntryKey));
                    s += "push eax\n";
                    argSize = 4;
                    break;
                case UINT:
                    s += loadFromMemory(o2MemoryAssociation, "ax");
                    // s += String.format("mov word ptr [%s], ax\n", SymbolTable.encodeString(argEntryKey));
                    s += "push ax\n";
                    argSize = 2;
                    break;
                case DOUBLE:
                    s += loadDoubleFromMemory(o2MemoryAssociation, false);
                    // s += String.format("fstp qword ptr [%s]\n", SymbolTable.encodeString(argEntryKey));
                    s += "sub esp, 8\n";
                    s += "fstp qword ptr [esp]\n";
                    argSize = 8;
                    break;
                case STRING:
                case OBJECT:
                    s += loadFromMemory(o2MemoryAssociation, "eax");
                    // s += String.format("mov dword ptr [%s], eax\n", SymbolTable.encodeString(argEntryKey));
                    s += "push eax\n";
                    argSize = 4;
                    break;
                default:
                    s += "No deberia estar viendo esto\n";
                    break;
            }
        }

        if (prevTriplet == null || !prevTriplet.getOperation().equals("THIS"))
        {
            // Pushear vacio para que no se rompa el stack
            s += "push 0\n";
        }
        else
        {
            // s +=" jajaj\n";
            s += loadFromMemory(prevTriplet.getOperand1().getMemoryAssociation(), "eax");
            s += "push eax\n";
        }

        s += "call " + stEntry.getAttrib(AttribKey.ASSEMBLY_TAG) + "\n";

        s += "add esp, 4\n"; // Sacar el this

        if (argSize > 0)
            s += String.format("add esp, %d\n", argSize);

        return s;
    }

/*
    public String translateThis(Triplet triplet)
    {
        TripletOperand o1 = triplet.getOperand1();

        MemoryAssociation memoryAssociation = (MemoryAssociation)o1.getstEntry().getAttrib(AttribKey.MEMORY_ASSOCIATION);

        // return String.format("mov edx, offset %s\n", memoryAssociation.getTag());

        String s = "";
        s += loadFromMemory(memoryAssociation, "edx");
        s += "push edx\n";
        return s;
    }
 */

    public String translateUItoD(Triplet triplet)
    {
        TripletOperand o1 = triplet.getOperand1();
        MemoryAssociation o1MemoryAssociation = o1.getMemoryAssociation();
        MemoryAssociation resultMemoryAssociation = triplet.getMemoryAssociation();

        String s = "";

        s += loadFromMemory(o1MemoryAssociation, "ax");
        s += "cwde\n";
        s += "push eax\n";
        s += "fild dword ptr [esp]\n";
        s += "add esp, 4\n";
        s += saveToMemory(resultMemoryAssociation, null);

        return s;
    }

    public String translateLtoD(Triplet triplet)
    {
        TripletOperand o1 = triplet.getOperand1();
        MemoryAssociation o1MemoryAssociation = o1.getMemoryAssociation();
        MemoryAssociation resultMemoryAssociation = triplet.getMemoryAssociation();

        String s = "";

        s += loadFromMemory(o1MemoryAssociation, "eax");
        s += "push eax\n";
        s += "fild dword ptr [esp]\n";
        s += "add esp, 4\n";
        s += saveToMemory(resultMemoryAssociation, null);

        return s;
    }

}
