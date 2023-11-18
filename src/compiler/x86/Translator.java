package compiler.x86;

import compiler.*;
import compiler.Compiler;

import java.util.LinkedList;
import java.util.Set;
import java.util.List;
import java.util.Collections;

public class Translator {

    SymbolTable symbolTable;

    public Translator(Compiler compiler) {
        this.symbolTable = compiler.getSymbolTable();
    }

    public String craftHeaderSection()
    {
        return
            ".386\n" +
            ".model flat, stdcall\n" +
            "option casemap :none\n" +
            "include \\masm32\\include\\windows.inc\n" +
            "include \\masm32\\include\\kernel32.inc\n" +
            "include \\masm32\\include\\masm32.inc\n" +
            "includelib \\masm32\\lib\\kernel32.lib\n" +
            "includelib \\masm32\\lib\\masm32.lib\n";
    }

    private String getConstantDeclarationLine(String entryKey)
    {
        SymbolTableEntry entry = symbolTable.getEntry(entryKey);
        MemoryAssociation memoryAssociation = (MemoryAssociation)entry.getAttrib(AttribKey.MEMORY_ASSOCIATION);
        DataType dataType = (DataType)entry.getAttrib(AttribKey.DATA_TYPE);

        String tag = memoryAssociation.getTag();
        int size = memoryAssociation.getSize();

        String simpleNumber;

        switch (dataType)
        {
            case LONG:
                simpleNumber = entry.getLexeme().substring(0, entry.getLexeme().length() - 2);
                return String.format("%s dd %s\n", tag, simpleNumber);
            case STRING:
                return String.format("%s db '%s', 0\n", tag, entry.getLexeme());
            case UINT:
                simpleNumber = entry.getLexeme().substring(0, entry.getLexeme().length() - 3);
                return String.format("%s dw %s\n", tag, simpleNumber);
            case DOUBLE:
                return String.format("%s dq %s\n", tag, simpleNumber);
            default:
                return null;
        }
    }

    public String craftDataSection()
    {
        StringBuilder sb = new StringBuilder();
    return null;
    }

    private LinkedList<FlattenObjectItem> _flattenObject(String entryKey, Integer currentOffset)
    {
        LinkedList<FlattenObjectItem> toReturn = new LinkedList<>();

        List<String> rawChildsEntryKeys = symbolTable.getChildrenOf(entryKey);

        System.out.println("Antes del filtro");
        System.out.println(rawChildsEntryKeys);

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
