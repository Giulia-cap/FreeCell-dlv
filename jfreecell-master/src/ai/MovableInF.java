package ai;

import freecell.Column;
import it.unical.mat.embasp.languages.Id;

@Id("spostabileInF") 
public class MovableInF extends  MovableInCo 
{
	private Column column;
	public MovableInF(int id)
	{
		super(id);
	}
	
	public MovableInF(int id,Column c)
	{
		super(id);
		this.column=c;
	}

	public Column getColumn() {
		return column;
	}

	public void setColumn(Column column) {
		this.column = column;
	}
}
