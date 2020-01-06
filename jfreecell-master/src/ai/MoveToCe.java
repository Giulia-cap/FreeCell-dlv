package ai;

import freecell.Column;
import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("moveToCe")
public class MoveToCe /*extends  MovableInCo */
{
	
	private Column column;
	@Param(0)
	private int ca;
	@Param(1)
	private int ce;

	public MoveToCe() {}
		
	public MoveToCe(int cards,int cell)
	{
		//super(cards);
		this.ce=cell;
		this.ca=cards;
	}
	
	public MoveToCe(Column c)
	{
		this.column=c;
	}
	
	public int getCa() {
		return ca;
	}
	public void setCa(int ca) {
		this.ca = ca;
	}
	
	public Column getColumn() {
		return column;
	}

	public void setColumn(Column column) {
		this.column = column;
	}

	public void setCe(int ce) {
		this.ce = ce;
	}
	public int getCe() {
		return ce;
	}
	
	public String toString() {
		String s = "";
		s += "moveToCe(" + ca + "," + ce + ")";
		return s;
	}
	
}
