package ai;

import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("moveFromeCeToColumn")
public class MoveFromCeToColumn 
{
	@Param(0)
	private int cefrom;
	@Param(1)
	private int coTo;
	
	public MoveFromCeToColumn() {}
	
	public MoveFromCeToColumn(int id,int f)
	{
		this.cefrom=id;
		this.coTo=f;
	}
	
	public int getCefrom() {
		return cefrom;
	}

	public void setCefrom(int cefrom) {
		this.cefrom = cefrom;
	}

	public int getCoTo() {
		return coTo;
	}

	public void setCoTo(int coTo) {
		this.coTo = coTo;
	}

	public String toString() {
		String s = "";
		s += "moveFromeCeToColumn(" + cefrom + "," + coTo + ")";
		return s;
	}
}