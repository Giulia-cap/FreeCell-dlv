package ai;
import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;


@Id("moveToFi")
public class MoveToF 
{
	@Param(0)
	private int cardId;
	@Param(1)
	private int finish;
	
	public MoveToF() {}
	
	public MoveToF(int id,int f)
	{
		this.cardId=id;
		this.finish=f;
	}

	public int getFinish() {
		return finish;
	}

	public void setFinish(int finish) {
		this.finish = finish;
	}
	
	public int getCardId() {
		return cardId;
	}

	public void setCardId(int cardId) {
		this.cardId = cardId;
	}
	
	public String toString() {
		String s = "";
		s += "moveToF(" + cardId + "," + finish + ")";
		return s;
	}

}
