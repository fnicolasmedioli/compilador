package compiler.x86;

import compiler.*;
import compiler.Compiler;

import java.util.LinkedList;
import java.util.Set;
import java.util.List;
import java.util.Collections;
import java.nio.ByteBuffer;

public class Translator {

    SymbolTable symbolTable;
    TripletTranslator tripletTranslator;
    ListOfTriplets listOfTriplets;

    public Translator(Compiler compiler, ListOfTriplets listOfTriplets) {
        this.symbolTable = compiler.getSymbolTable();
        this.tripletTranslator = new TripletTranslator(symbolTable, listOfTriplets);
        this.listOfTriplets = listOfTriplets;
    }

    public String getAssemblyCode()
    {
        return craftHeaderSection() +
                craftDataSection() +
                craftCodeSection();
    }

    public String craftHeaderSection()
    {
        return
            ".586\n" +
            ".model flat, stdcall\n\n" +
            "option casemap :none\n" +
            "include \\masm32\\include\\windows.inc\n" +
            "include \\masm32\\include\\kernel32.inc\n" +
            "include \\masm32\\include\\user32.inc\n" +
            "include \\masm32\\include\\masm32.inc\n" +
            "includelib \\masm32\\lib\\kernel32.lib\n" +
            "includelib \\masm32\\lib\\user32.lib\n" +
            "includelib \\masm32\\lib\\masm32.lib\n" +
            "includelib \\masm32\\lib\\msvcrt.lib\n\n" +
            "extern printf : near\n\n";
    }

    private String getConstantDeclarationLine(String entryKey)
    {
        SymbolTableEntry entry = symbolTable.getEntry(entryKey);

        if (entry == null) return "getConstantDeclarationLine() - entry == null para " + entryKey + "\n";

        MemoryAssociation memoryAssociation = (MemoryAssociation)entry.getAttrib(AttribKey.MEMORY_ASSOCIATION);

        if (memoryAssociation == null) return "getConstantDeclarationLine() - memoryAssociation == null para " + entryKey + "\n";

        DataType dataType = (DataType)entry.getAttrib(AttribKey.DATA_TYPE);

        String tag = memoryAssociation.getTag();
        String simpleNumber;

        switch (dataType)
        {
            case LONG:
                simpleNumber = entry.getLexeme().substring(0, entry.getLexeme().length() - 2);
                return String.format("%s dd %s\n", tag, simpleNumber);
            case STRING:
            return String.format("%s db '%s', 0\n", tag, entry.getLexeme().substring(1, entry.getLexeme().length() - 1));
            case UINT:
                simpleNumber = entry.getLexeme().substring(0, entry.getLexeme().length() - 3);
                return String.format("%s dw %s\n", tag, simpleNumber);
            case DOUBLE:

                /*
                double numeric = Double.parseDouble();
                ByteBuffer buffer = ByteBuffer.allocate(Double.BYTES);
                buffer.putDouble(numeric);
                byte[] bytes = buffer.array();
                 */

                String n = entry.getLexeme().replace('D', 'e').replace('d', 'e');
                if (n.charAt(n.length()-1) == '.') n += "0";

                return String.format(
                    "%s dq %s\n",
                    tag,
                    n
                );
            default:
                return "No deberia estar viendo esto\n";
        }
    }

    private String getVarDeclarationLine(String entryKey)
    {
        SymbolTableEntry entry = symbolTable.getEntry(entryKey);

        MemoryAssociation memoryAssociation = (MemoryAssociation)entry.getAttrib(AttribKey.MEMORY_ASSOCIATION);
        DataType dataType = (DataType)entry.getAttrib(AttribKey.DATA_TYPE);

        String tag = memoryAssociation.getTag();

        switch (dataType)
        {
            case LONG:
            case STRING:
                return String.format("%s dd ?\n", tag);
            case UINT:
                return String.format("%s dw ?\n", tag);
            case DOUBLE:
                return String.format("%s dq ?\n", tag);
            case OBJECT:
                int size = memoryAssociation.getSize();
                return String.format("%s db %d dup(?)\n", tag, size);
            default:
                return "No deberia estar viendo esto\n";
        }
    }

    public String craftDataSection()
    {
        StringBuilder sb = new StringBuilder();

        sb.append(".data\n");

        sb.append("___temp_double___ dq ?\n");
        sb.append("__overflow_mul_msg__ db 'Overflow detectado. Finaliza la ejecucion', 10, 0\n");
        sb.append("__overflow_suma_msg__ db 'Overflow en suma de DOUBLEs detectado. Finaliza la ejecucion', 10, 0\n");
        sb.append("__overflow_resta_msg__ db 'Overflow en resta de UINTs detectado. Finaliza la ejecucion', 10, 0\n");
        sb.append("___str_formatter___ db '%s', 0\n");
        sb.append("___long_formatter___ db '%d', 0\n");
        sb.append("___uint_formatter___ db '%d', 0\n");
        sb.append("___double_formatter___ db '%f', 0\n");
        sb.append("___newline___ db 10, 0\n\n");

        List<String> constantsKeys = symbolTable.getConstantList();

        for (String constantKey : constantsKeys)
            sb.append(getConstantDeclarationLine(constantKey));

        List<String> varsKeys = symbolTable.getVarList();

        for (String varKey : varsKeys)
            sb.append(getVarDeclarationLine(varKey));

        sb.append("\n");

        return sb.toString();
    }

    public String craftCodeSection() {
        StringBuilder sb = new StringBuilder();

        sb.append(".code\n");
        sb.append("start:\n\n");

        sb.append("jmp @@imprimir_mensaje_end\n");
        sb.append("@@imprimir_mensaje:\n");
        sb.append("pushad\n");
        sb.append("push eax\n");
        sb.append("push offset ___str_formatter___\n");
        sb.append("call printf\n");
        sb.append("popad\n");
        sb.append("ret\n");
        sb.append("@@imprimir_mensaje_end:\n\n");

        sb.append("jmp @@overflow_mul_end\n");
        sb.append("@@overflow_mul:\n");
        sb.append("push offset __overflow_mul_msg__\n");
        sb.append("push offset ___str_formatter___\n");
        sb.append("call printf\n");
        sb.append("jmp @@fin\n");
        sb.append("@@overflow_mul_end:\n\n");

        sb.append("jmp @@overflow_suma_end\n");
        sb.append("@@overflow_suma:\n");
        sb.append("push offset __overflow_suma_msg__\n");
        sb.append("push offset ___str_formatter___\n");
        sb.append("call printf\n");
        sb.append("jmp @@fin\n");
        sb.append("@@overflow_suma_end:\n\n");

        sb.append("jmp @@overflow_resta_end\n");
        sb.append("@@overflow_resta:\n");
        sb.append("push offset __overflow_resta_msg__\n");
        sb.append("push offset ___str_formatter___\n");
        sb.append("call printf\n");
        sb.append("jmp @@fin\n");
        sb.append("@@overflow_resta_end:\n\n");

        int tripletID = 0;

        for (Triplet triplet : listOfTriplets)
        {
            // Buscar los tags asociados a el triplet

            for (String tag : listOfTriplets.getTags(tripletID))
                sb.append(String.format("%s:\n", tag));

            switch (triplet.getOperation())
            {
                case "+":
                    sb.append(tripletTranslator.translateAdd(triplet));
                    break;
                case "=":
                    sb.append(tripletTranslator.translateAssign(triplet));
                    break;
                case "-":
                    sb.append(tripletTranslator.translateSub(triplet));
                    break;
                case "/":
                    sb.append(tripletTranslator.translateDiv(triplet));
                    break;
                case "*":
                    sb.append(tripletTranslator.translateMul(triplet));
                    break;
                case "PRINT":
                    sb.append(tripletTranslator.translatePrint(triplet));
                    break;
                case "RETURN":
                    sb.append("ret\n");
                    break;
                case "JMP":
                    sb.append(tripletTranslator.translateJMP(triplet));
                    break;
                case ">=":
                case "<=":
                case "<":
                case ">":
                case "==":
                case "!!":
                    sb.append(tripletTranslator.translateCmp(triplet));
                    break;
                case "END":
                    break;
                case "CJUMP":
                    Triplet prevTriplet = listOfTriplets.getTriplet(tripletID-1);
                    sb.append(tripletTranslator.translateCJump(triplet, prevTriplet));
                    break;
                case "NEG_CJUMP":
                    Triplet prevTriplet2 = listOfTriplets.getTriplet(tripletID-1);
                    sb.append(tripletTranslator.translateNegCJump(triplet, prevTriplet2));
                    break;
                case "CALL":
                    sb.append(tripletTranslator.translateCall(triplet));
                    break;
                case "THIS":
                    sb.append(tripletTranslator.translateThis(triplet));
                    break;
                case "UITOD":
                    sb.append(tripletTranslator.translateUItoD(triplet));
                    break;
                case "LTOD":
                    sb.append(tripletTranslator.translateLtoD(triplet));
                    break;
                default:
                    sb.append("Operacion no implementada: ").append(triplet.getOperation()).append("\n");
                    break;
            }
            tripletID++;
        }

        sb.append("\n");
        sb.append("@@fin:\n");
        sb.append("invoke ExitProcess, 0\n");
        sb.append("end start");

        return sb.toString();
    }

    private LinkedList<FlattenObjectItem> _flattenObject(String entryKey, Integer currentOffset)
    {
        LinkedList<FlattenObjectItem> toReturn = new LinkedList<>();

        List<String> rawChildsEntryKeys = symbolTable.getChildrenOf(entryKey);

        // Eliminar hijos que son objetos (No tendran memoria al no ser primitivos)

        List<String> childsEntryKeys = new LinkedList<>();

        for (String childEntryKey : rawChildsEntryKeys)
        {
            SymbolTableEntry childEntry = symbolTable.getEntry(childEntryKey);
            if ((DataType)childEntry.getAttrib(AttribKey.DATA_TYPE) != DataType.OBJECT)
                childsEntryKeys.add(childEntryKey);
        }

        Collections.sort(childsEntryKeys);

        for (String childEntryKey : childsEntryKeys)
        {
            SymbolTableEntry childEntry = symbolTable.getEntry(childEntryKey);

            MemoryAssociation memoryAssociation = (MemoryAssociation)childEntry.getAttrib(AttribKey.MEMORY_ASSOCIATION);

            if (childEntry.getAttrib(AttribKey.DATA_TYPE) != DataType.OBJECT && memoryAssociation.hasSize())
            {
                toReturn.add(new FlattenObjectItem(memoryAssociation, currentOffset));
                currentOffset += memoryAssociation.getSize();
            }

            toReturn.addAll(_flattenObject(childEntryKey, currentOffset));
        }

        return toReturn;
    }

    private LinkedList<FlattenObjectItem> flattenObject(String entryKey)
    {
        Integer baseOffset = 0;
        return _flattenObject(entryKey, baseOffset);
    }
}
