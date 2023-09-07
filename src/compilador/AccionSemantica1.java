package compilador;

public class AccionSemantica1 implements AccionSemantica {

	private static AccionSemantica instancia;
	
	public static AccionSemantica i()
	{
		if (instancia == null)
			instancia = new AccionSemantica1();
		return instancia;
	}
	
	@Override
	public void ejecutar()
	{
		
	}
}