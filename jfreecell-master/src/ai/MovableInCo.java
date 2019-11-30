package ai;

import freecell.Card;
import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("spostabileInCo")
public class MovableInCo 
{
	@Param(0)
	private int cardId;
	
	public MovableInCo(int id)
	{
		this.cardId=id;
	}

	public int getCardId() {
		return cardId;
	}

	public void setCardId(int cardId) {
		this.cardId = cardId;
	}
}
