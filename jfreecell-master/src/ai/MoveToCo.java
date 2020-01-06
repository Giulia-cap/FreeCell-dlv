package ai;

import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("moveToCo")
public class MoveToCo 
{
	@Param(0)
	private int caId;
	@Param(1)
	private int coId;
	
	public MoveToCo() {}
	
	public MoveToCo(int id,int f)
	{
		this.caId=id;
		this.coId=f;
	}

	public int getCaId() {
		return caId;
	}

	public void setCaId(int caId) {
		this.caId = caId;
	}

	public int getCoId() {
		return coId;
	}

	public void setCoId(int coId) {
		this.coId = coId;
	}
	
	public String toString() {
		String s = "";
		s += "moveToF(" + caId + "," + coId + ")";
		return s;
	}
}