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
            "include \\masm32\\include\\masm32.inc\n" +
            "includelib \\masm32\\lib\\kernel32.lib\n" +
            "includelib \\masm32\\lib\\masm32.lib\n\n";
    }

    private String getConstantDeclarationLine(String entryKey)
    {
        SymbolTableEntry entry = symbolTable.getEntry(entryKey);
        MemoryAssociation memoryAssociation = (MemoryAssociation)entry.getAttrib(AttribKey.MEMORY_ASSOCIATION);
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
                double numeric = Double.parseDouble(entry.getLexeme().replace('D', 'e').replace('d', 'e'));
                ByteBuffer buffer = ByteBuffer.allocate(Double.BYTES);
                buffer.putDouble(numeric);
                byte[] bytes = buffer.array();

                return String.format(
                    "%s db %d %d %d %d %d %d %d %d\n",
                    tag,
                    bytes[0], bytes[1], bytes[2], bytes[3], bytes[4], bytes[5], bytes[6], bytes[7]
                );
            default:
                return "No deberia estar viendo esto\n";
        }
    }

    public String craftDataSection()
    {
        StringBuilder sb = new StringBuilder();

        sb.append(".data\n");

        List<String> constantsKeys = symbolTable.getConstantList();

        for (String constantKey : constantsKeys)
            sb.append(getConstantDeclarationLine(constantKey));

        sb.append("\n");

        return sb.toString();
    }

    public String craftCodeSection() {
        StringBuilder sb = new StringBuilder();

        sb.append(".code\n");
        sb.append("start:\n\n");

        sb.append("jmp @@imprimir_mensaje_end\n");
        sb.append("@@imprimir_mensaje:\n");
        sb.append("pop eax\n");
        sb.append("invoke MessageBox, NULL, eax, eax, MB_OK\n");
        sb.append("ret\n");
        sb.append("@@imprimir_mensaje_end:\n\n");

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
                default:
                    sb.append("Operacion no implementada: ").append(triplet.getOperation()).append("\n");
                    break;
            }
            tripletID++;
        }

        sb.append("\n");
        sb.append("fin:\n");
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
