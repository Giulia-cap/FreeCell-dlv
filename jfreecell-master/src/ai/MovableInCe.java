package ai;

import freecell.Card;
import freecell.Column;
import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("spostabileInCe")
public class MovableInCe 
{
	//private int cardId;
	private Column column;
	
	@Param(0)
	private int cardId1;
	
	
	public MovableInCe(int id)
	{
		cardId1=id;
	}
	
	public MovableInCe(int id,Column c)
	{
		cardId1=id;
		this.column=c;
	}

	public Column getColumn() {
		return column;
	}

	public void setColumn(Column column) {
		this.column = column;
	}

	public int getCardId1() {
		return cardId1;
	}

	public void setCardId1(int cardId) {
		this.cardId1 = cardId;
	}
}
