package compilador;

/**
 * Tipo de dato usado para pasar por yylval
 */
public class TokenInfo {

	String lexeme;
	TokenLocation location;
	
	public TokenInfo() {
		this.lexeme = null;
		this.location = null;
	}
	
	public TokenInfo(String lexeme, TokenLocation location) {
		this.lexeme = lexeme;
		this.location = location;
	}

	public String getLexeme() {
		return lexeme;
	}

	public TokenLocation getLocation() {
		return location;
	}
}