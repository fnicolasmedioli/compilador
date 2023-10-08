package compilador;

public class TokenLocation {

	Integer row;
	Integer column;
	
	public TokenLocation(Integer row)
	{
		this.row = row;
	}
	
	public TokenLocation(Integer row, Integer column)
	{
		this.row = row;
		this.column = column;
	}
	
	public Integer getRow()
	{
		return this.row;
	}
	
	public Integer getColumn()
	{
		return this.column;
	}
	
	public boolean greaterThan(TokenLocation b)
	{
		if (column == null || b.getColumn() == null)
			return row > b.getRow();

		return (row > b.getRow() || (row == b.getRow() && column > b.getColumn()));
	}
	
	@Override
	public String toString()
	{
		return "Fila " + row;
	}
}
