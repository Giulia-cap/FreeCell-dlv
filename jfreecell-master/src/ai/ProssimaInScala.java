package ai;

import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("prossimaInScala")
public class ProssimaInScala 
{
	@Param(0)
	private int cCheFaPa;
	
	public ProssimaInScala(){}
	public ProssimaInScala(int f)
	{
		cCheFaPa=f;
	}
	public int getCCheFaPa() {
		return cCheFaPa;
	}
	public void setCCheFaPa(int cCheFaPa) {
		this.cCheFaPa = cCheFaPa;
	}
	
	
}
