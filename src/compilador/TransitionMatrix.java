package compilador;

import java.util.Vector;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class TransitionMatrix {

    private static Vector<HashMap<Character, Transition>> matrix;
    
    private final static Character VALID_CHARS[] = {
        '_', '{', '}', '(', ')', ';', ',', '*', '\s',
        '\t', '\n', '%', '.', '-', '+', '/', '=', '!',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
        'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
        'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F',
        'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
        'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };
    
    public final static Integer INITIAL_STATE = 0;
    public final static Integer FINAL_STATE = INITIAL_STATE;
    
    public TransitionMatrix()
    {
        if (matrix != null) return;
        
        matrix = new Vector<>();
        
        HashMap<Character, Transition> s0 = new HashMap<>();
        s0.put('_', new Transition(1, List.of(2)));
        s0.put('{', new Transition(18, List.of(2)));
        s0.put('}', new Transition(18, List.of(2)));
        s0.put('(', new Transition(18, List.of(2)));
        s0.put(')', new Transition(18, List.of(2)));
        s0.put(';', new Transition(18, List.of(2)));
        s0.put(',', new Transition(18, List.of(2)));
        s0.put(':', new Transition(18, List.of(2)));
        s0.put('*', new Transition(19, List.of(2)));
        s0.put('\s', new Transition(0, null));
        s0.put('\t', new Transition(0, null));
        s0.put('\n', new Transition(0, List.of(9)));
        s0.put('%', new Transition(21, List.of(7)));        
        s0.put('.', new Transition(4, List.of(2)));        
        s0.put('-', new Transition(10, List.of(2)));
        s0.put('+', new Transition(12, List.of(2)));
        s0.put('/', new Transition(12, List.of(2)));
        s0.put('=', new Transition(13, List.of(2)));
        s0.put('<', new Transition(15, List.of(2)));
        s0.put('>', new Transition(15, List.of(2)));
        s0.put('!', new Transition(17, List.of(2)));
        this.insertNumbers(s0, new Transition(2, List.of(2)));
        this.insertLetters(s0, new Transition(1, List.of(2)));
        matrix.add(s0);
        
        HashMap<Character, Transition> s1 = new HashMap<>();
        s1.put('_', new Transition(1, List.of(3)));
        this.insertNumbers(s1, new Transition(1, List.of(3)));
        this.insertLetters(s1, new Transition(1, List.of(3)));
        this.insertCLN_(s1, new Transition(FINAL_STATE, List.of(1, 4)));
        matrix.add(s1);
        
        HashMap<Character, Transition> s2 = new HashMap<>();
        this.insertNumbers(s2, new Transition(2, List.of(3)));
        s2.put('.', new Transition(8, List.of(3)));
        s2.put('_', new Transition(3, List.of(3)));
        matrix.add(s2);
        
        HashMap<Character, Transition> s3 = new HashMap<>();
        s3.put('l', new Transition(FINAL_STATE, List.of(3, 5)));
        s3.put('u', new Transition(22, List.of(3)));
        matrix.add(s3);
        
        HashMap<Character, Transition> s4 = new HashMap<>();
        s4.put('_', new Transition(FINAL_STATE, List.of(1, 6)));
        this.insertLetters(s4, new Transition(FINAL_STATE, List.of(1, 6)));
        this.insertNumbers(s4, new Transition(5, List.of(3)));
        matrix.add(s4);
        
        HashMap<Character, Transition> s5 = new HashMap<>();
        s5.put('d', new Transition(6, List.of(3)));
        s5.put('D', new Transition(6, List.of(3)));
        this.insertNumbers(s5, new Transition(5, List.of(3)));
        this.insertCNdD(s5, new Transition(FINAL_STATE, List.of(1, 5)));
        matrix.add(s5);

        HashMap<Character, Transition> s6 = new HashMap<>();
        s6.put('+', new Transition(7, List.of(3)));
        s6.put('-', new Transition(7, List.of(3)));
        matrix.add(s6);
        
        HashMap<Character, Transition> s7 = new HashMap<>();
        this.insertNumbers(s7, new Transition(9, List.of(3)));
        matrix.add(s7);
        
        HashMap<Character, Transition> s8 = new HashMap<>();
        s8.put('d', new Transition(6, List.of(3)));
        s8.put('D', new Transition(6, List.of(3)));
        this.insertNumbers(s8, new Transition(5, List.of(3)));
        this.insertCNdD(s8, new Transition(FINAL_STATE, List.of(1, 5)));
        matrix.add(s8);
        
        HashMap<Character, Transition> s9 = new HashMap<>();
        this.insertNumbers(s9, new Transition(9, List.of(3)));
        this.insertCNdD(s9, new Transition(FINAL_STATE, List.of(1, 5)));
        matrix.add(s9);
        
        HashMap<Character, Transition> s10 = new HashMap<>();
        s10.put('=', new Transition(11, List.of(3)));
        this.insertAllBut(s10, new Transition(FINAL_STATE, List.of(1, 6)), '=');
        matrix.add(s10);
        
        HashMap<Character, Transition> s11 = new HashMap<>();
        this.insertAll(s11, new Transition(FINAL_STATE, List.of(1, 6)));
        matrix.add(s11);
        
        HashMap<Character, Transition> s12 = new HashMap<>();
        this.insertAll(s12, new Transition(FINAL_STATE, List.of(1, 6)));
        matrix.add(s12);
        
        HashMap<Character, Transition> s13 = new HashMap<>();
        s13.put('=', new Transition(14, List.of(3)));
        this.insertAllBut(s13, new Transition(FINAL_STATE, List.of(1, 6)), '=');
        matrix.add(s13);
        
        HashMap<Character, Transition> s14 = new HashMap<>();
        this.insertAll(s14, new Transition(FINAL_STATE, List.of(1, 6)));
        matrix.add(s14);
        
        HashMap<Character, Transition> s15 = new HashMap<>();
        s15.put('=', new Transition(16, List.of(3)));
        this.insertAllBut(s15, new Transition(FINAL_STATE, List.of(1, 6)), '=');
        matrix.add(s15);
        
        HashMap<Character, Transition> s16 = new HashMap<>();
        this.insertAll(s16, new Transition(FINAL_STATE, List.of(1, 6)));
        matrix.add(s16);
        
        HashMap<Character, Transition> s17 = new HashMap<>();
        s17.put('!', new Transition(FINAL_STATE, List.of(3, 6)));
        matrix.add(s17);
        
        HashMap<Character, Transition> s18 = new HashMap<>();
        this.insertAll(s18, new Transition(FINAL_STATE, List.of(1, 6)));
        matrix.add(s18);
        
        HashMap<Character, Transition> s19 = new HashMap<>();
        s19.put('*', new Transition(20, null));
        this.insertAllBut(s19, new Transition(FINAL_STATE, List.of(1, 6)), '*');
        matrix.add(s19);
        
        HashMap<Character, Transition> s20 = new HashMap<>();
        s20.put('\n', new Transition(FINAL_STATE, null));
        this.insertAllBut(s20, new Transition(20, null), '\n');
        matrix.add(s20);
        
        HashMap<Character, Transition> s21 = new HashMap<>();
        s21.put('%', new Transition(FINAL_STATE, List.of(8)));
        this.insertAllBut(s21, new Transition(21, List.of(3)), '%');
        matrix.add(s21);
        
        HashMap<Character, Transition> s22 = new HashMap<>();
        s22.put('i', new Transition(FINAL_STATE, List.of(3, 5)));
        matrix.add(s22);
    }
    
    private void insertLetters(Map<Character, Transition> m, Transition transition)
    {
        Character[] ar = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
        this.insertArray(m, transition, ar);
    }
    
    private void insertNumbers(Map<Character, Transition> m, Transition transition)
    {
        Character[] ar = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
        this.insertArray(m, transition, ar);
    }
    
    /**
     * Insert characters from C-L-N-_
     * @param m
     * @param newState
     */
    private void insertCLN_(Map<Character, Transition> m, Transition transition)
    {
        Character[] ar = { '{', '}', '(', ')', ';', ',', '*',
            '\s', '\t', '\n', '%', '.', '-', '+', '/', '=', '!' };
        this.insertArray(m, transition, ar);
    }
    
    /**
     * Insert characters from C-N-d-D
     * @param m
     * @param newState
     */
    private void insertCNdD(Map<Character, Transition> m, Transition transition)
    {
        Character[] ar = { '_', '{', '}', '(', ')', ';', ',', '*',
            '\s', '\t', '\n', '%', '.', '-', '+', '/', '=',
            '!', 'a', 'b', 'c', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'E',
            'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
            'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
        this.insertArray(m, transition, ar);
    }
    
    private void insertAllBut(Map<Character, Transition> m, Transition transition, Character avoid)
    {
        for (int i = 0; i < VALID_CHARS.length; i++)
            if (! VALID_CHARS[i].equals(avoid))
                m.put(VALID_CHARS[i], transition);
    }
    
    private void insertAll(Map<Character, Transition> m, Transition transition)
    {
        for (int i = 0; i < VALID_CHARS.length; i++)
            m.put(VALID_CHARS[i], transition);
    }
    
    private void insertArray(Map<Character, Transition> map, Transition transition, Character[] array)
    {
        for (Character t : array)
            map.put(t, transition);
    }
    
    public Transition getTransition(Integer from, Character charRead)
    {
        Map<Character, Transition> transitions = matrix.get(from);
        if (!transitions.containsKey(charRead))
            return null;
        return transitions.get(charRead);
    }
}