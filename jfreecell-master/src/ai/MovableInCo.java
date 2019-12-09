package ai;

import freecell.Card;
import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("spostabileInCo")
public class MovableInCo 
{
	@Param(0)
	private int cardId;
	@Param(0)
	private int colId;
	
	public MovableInCo() {}
	
	public MovableInCo(int id,int idc)
	{
		this.cardId=id;
		this.colId=idc;
	}

	public int getColId() {
		return colId;
	}

	public void setColId(int colId) {
		this.colId = colId;
	}

	public int getCardId() {
		return cardId;
	}

	public void setCardId(int cardId) {
		this.cardId = cardId;
	}
}
