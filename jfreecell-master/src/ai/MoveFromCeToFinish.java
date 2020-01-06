package ai;

import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("moveFromeCeToFinish")
public class MoveFromCeToFinish 
{
	@Param(0)
	private int cardFrom1;
	@Param(1)
	private int finish1;
	
	public MoveFromCeToFinish() {}
	
	public MoveFromCeToFinish(int id,int f)
	{
		this.cardFrom1=id;
		this.finish1=f;
	}
	
	
	public String toString() {
		String s = "";
		s += "moveToF(" + cardFrom1 + "," + finish1 + ")";
		return s;
	}

	public int getCardFrom1() {
		return cardFrom1;
	}

	public void setCardFrom1(int cardFrom1) {
		this.cardFrom1 = cardFrom1;
	}

	public int getFinish1() {
		return finish1;
	}

	public void setFinish1(int finish1) {
		this.finish1 = finish1;
	}
}