package compilador;

import java.util.Vector;
import java.util.Map;
import java.util.HashMap;

public class TransitionMatrix {

	private static Vector<HashMap<String, Integer>> matrix;
	
	private final static String VALID_CHARS[] = {
		"_", "{", "}", "(", ")", ";", ",", "*", "\s",
		"\t", "\n", "%", ".", "-", "+", "/", "=", "!",
		"a", "b", "c", "e", "f", "g", "h", "i", "j", "k",
		"l", "m", "n", "o", "p", "q", "r", "s", "t", "u",
		"v", "w", "x", "y", "z", "A", "B", "C", "E", "F",
		"G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
		"Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
		"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
	};
	private final static Integer FINAL_STATE = 23;
	
	public TransitionMatrix()
	{
		if (matrix != null) return;
		
		matrix = new Vector<>();
		
		HashMap<String, Integer> s0 = new HashMap<>();
		s0.put("_", 1);
		s0.put("{", 18);
		s0.put("}", 18);
		s0.put("(", 18);
		s0.put(")", 18);
		s0.put(";", 18);
		s0.put(",", 18);
		s0.put("*", 19);
		s0.put("\s", 0);
		s0.put("\t", 0);
		s0.put("\n", 0);
		s0.put("%", 21);
		s0.put(".", 4);
		s0.put("-", 10);
		s0.put("+", 12);
		s0.put("/", 12);
		s0.put("=", 13);
		s0.put("<", 15);
		s0.put(">", 15);
		s0.put("!", 17);
		this.insertNumbers(s0, 2);
		this.insertLetters(s0, 1);
		matrix.add(s0);
		
		HashMap<String, Integer> s1 = new HashMap<>();
		s1.put("_", 1);
		this.insertNumbers(s1, 1);
		this.insertLetters(s1, 1);
		this.insertCLN_(s1, FINAL_STATE);
		matrix.add(s1);
		
		HashMap<String, Integer> s2 = new HashMap<>();
		this.insertNumbers(s2, 2);
		s2.put(".", 8);
		s2.put("_", 3);
		matrix.add(s2);
		
		HashMap<String, Integer> s3 = new HashMap<>();
		s3.put("l", FINAL_STATE);
		matrix.add(s3);
		
		HashMap<String, Integer> s4 = new HashMap<>();
		this.insertLetters(s4, FINAL_STATE);
		this.insertNumbers(s4, 5);
		matrix.add(s4);
		
		HashMap<String, Integer> s5 = new HashMap<>();
		s5.put("d", 6);
		s5.put("D", 6);
		this.insertNumbers(s5, 5);
		this.insertCNdD(s5, FINAL_STATE);
		matrix.add(s5);

		HashMap<String, Integer> s6 = new HashMap<>();
		s6.put("+", 7);
		s6.put("-", 7);
		matrix.add(s6);
		
		HashMap<String, Integer> s7 = new HashMap<>();
		this.insertNumbers(s7, 9);
		matrix.add(s7);
		
		HashMap<String, Integer> s8 = new HashMap<>();
		s8.put("d", 6);
		s8.put("D", 6);
		this.insertNumbers(s8, 5);
		this.insertCNdD(s8, FINAL_STATE);
		matrix.add(s8);
		
		HashMap<String, Integer> s9 = new HashMap<>();
		this.insertNumbers(s9, 9);
		this.insertCNdD(s9, FINAL_STATE);
		matrix.add(s9);
		
		HashMap<String, Integer> s10 = new HashMap<>();
		s10.put("=", 11);
		this.insertAllBut(s10, FINAL_STATE, "=");
		matrix.add(s10);
		
		HashMap<String, Integer> s11 = new HashMap<>();
		this.insertAll(s11, FINAL_STATE);
		matrix.add(s11);
		
		HashMap<String, Integer> s12 = new HashMap<>();
		this.insertAll(s12, FINAL_STATE);
		matrix.add(s12);
		
		HashMap<String, Integer> s13 = new HashMap<>();
		s13.put("=", 14);
		matrix.add(s13);
		
		HashMap<String, Integer> s14 = new HashMap<>();
		this.insertAll(s14, FINAL_STATE);
		matrix.add(s14);
		
		HashMap<String, Integer> s15 = new HashMap<>();
		s15.put("=", 16);
		this.insertAllBut(s15, FINAL_STATE, "=");
		matrix.add(s15);
		
		HashMap<String, Integer> s16 = new HashMap<>();
		this.insertAll(s16, FINAL_STATE);
		matrix.add(s16);
		
		HashMap<String, Integer> s17 = new HashMap<>();
		s17.put("!", FINAL_STATE);
		matrix.add(s17);
		
		HashMap<String, Integer> s18 = new HashMap<>();
		this.insertAll(s18, FINAL_STATE);
		matrix.add(s18);
		
		HashMap<String, Integer> s19 = new HashMap<>();
		s19.put("*", 20);
		this.insertAllBut(s19, FINAL_STATE, "*");
		matrix.add(s19);
		
		HashMap<String, Integer> s20 = new HashMap<>();
		s20.put("\n", FINAL_STATE);
		this.insertAllBut(s20, 20, "\n");
		matrix.add(s20);
		
		HashMap<String, Integer> s21 = new HashMap<>();
		s21.put("%", FINAL_STATE);
		this.insertAllBut(s21, 21, "%");
		matrix.add(s21);
	}
	
	private void insertLetters(Map<String, Integer> m, Integer newState)
	{
		String[] ar = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
		this.insertArray(m, newState, ar);
	}
	
	private void insertNumbers(Map<String, Integer> m, Integer newState)
	{
		String[] ar = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
		this.insertArray(m, newState, ar);
	}
	
	/**
	 * Insert characters from C-L-N-_
	 * @param m
	 * @param newState
	 */
	private void insertCLN_(Map<String, Integer> m, Integer newState)
	{
		String[] ar = { "{", "}", "(", ")", ";", ",", "*",
			"\s", "\t", "\n", "%", ".", "-", "+", "/", "=", "!" };
		this.insertArray(m, newState, ar);
	}
	
	/**
	 * Insert characters from C-N-d-D
	 * @param m
	 * @param newState
	 */
	private void insertCNdD(Map<String, Integer> m, Integer newState)
	{
		String[] ar = { "_", "{", "}", "(", ")", ";", ",", "*",
			"\s", "\t", "\n", "%", ".", "-", "+", "/", "=",
			"!", "a", "b", "c", "e", "f", "g", "h", "i", "j",
			"k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
			"u", "v", "w", "x", "y", "z", "A", "B", "C", "E",
			"F", "G", "H", "I", "J", "K", "L", "M", "N", "O",
			"P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
		this.insertArray(m, newState, ar);
	}
	
	private void insertAllBut(Map<String, Integer> m, Integer newState, String avoid)
	{
		for (int i = 0; i < VALID_CHARS.length; i++)
			if (! VALID_CHARS[i].equals(avoid))
				m.put(VALID_CHARS[i], newState);
	}
	
	private void insertAll(Map<String, Integer> m, Integer newState)
	{
		for (int i = 0; i < VALID_CHARS.length; i++)
			m.put(VALID_CHARS[i], newState);
	}
	
	private void insertArray(Map<String, Integer> map, Integer newState, String[] array)
	{
		for (String t : array)
			map.put(t, newState);
	}
	
	public Integer getTransition(Integer from, String charRead)
	{
		Map<String, Integer> transitions = matrix.get(from);
		if (!transitions.containsKey(charRead))
			// Return to initial state
			return 0;
		return transitions.get(charRead);
	}
}