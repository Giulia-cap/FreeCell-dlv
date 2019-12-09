package ai;


import freecell.Card;
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
	public int getCe() {
		return ce;
	}
	
}
