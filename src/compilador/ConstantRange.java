package compilador;

public class ConstantRange {
	
	private static final String MAX_LONG_SIGNED = "2147483648"; // pow(2, 31)
	private static final String MAX_LONG_UNSIGNED = "2147483647"; // pow(2, 31) - 1
	
	private static final String MAX_UINT = "65535"; // pow(2, 16) - 1
	private static final double MIN_DOUBLE = 2.2250738585072014E-308;
	private static final double MAX_DOUBLE = 1.7976931348623157E+308;

	public static boolean isValidLONG(String lexeme, boolean signed)
	{
		String num = lexeme.substring(0, lexeme.length() - 2);
		
		if (signed)
			return 
				!
				(num.length() > MAX_LONG_SIGNED.length()
				|| (num.length() == MAX_LONG_SIGNED.length() && num.compareTo(MAX_LONG_SIGNED) > 0)
			);
		else
			return
				!
				(num.length() > MAX_LONG_UNSIGNED.length()
				|| (num.length() == MAX_LONG_UNSIGNED.length() && num.compareTo(MAX_LONG_UNSIGNED) > 0)
			);
	}
	
	public static boolean isValidUINT(String lexeme)
	{
		String num = lexeme.substring(0, lexeme.length() - 3);
		
		return
			!
			(num.length() > MAX_UINT.length()
				|| (num.length() == MAX_UINT.length() && num.compareTo(MAX_UINT) > 0)
			);
	}
	
	public static boolean isValidDOUBLE(String lexeme)
	{
		double conv = Double.parseDouble(lexeme.replace('D', 'e').replace('d', 'e'));
		
		return !(
			conv != 0 &&
			(conv > MAX_DOUBLE || conv < MIN_DOUBLE)
		);
	}
}
