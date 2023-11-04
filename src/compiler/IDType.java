package compiler;

public enum IDType {
	
	VAR_ATTRIB("Variable o atributo"),
	FUNC_METHOD("Funcion o metodo"),
	CLASSNAME("Nombre de clase");

	private final String str;
	IDType(String str)
	{
		this.str = str;
	}

	@Override
	public String toString()
	{
		return this.str;
	}
}