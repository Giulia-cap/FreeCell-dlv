package ai;

import freecell.Column;
import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("spostabileInF") 
public class MovableInF 
{
	private Column column;
	
	@Param(0)
	private int car;
	@Param(1)
	private int idFinish;


public MovableInF() {}
	
	public MovableInF(int id,int idFinish)
	{
		this.car=id;
		this.idFinish=idFinish;
	}

	public int getCar() {
		return car;
	}

	public void setCar(int cardId) {
		this.car = cardId;
	}
	
	public Column getColumn() {
		return column;
	}

	public void setColumn(Column column) {
		this.column = column;
	}

	public int getIdFinish() {
		return idFinish;
	}

	public void setIdFinish(int idFinish) {
		this.idFinish = idFinish;
	}

}
