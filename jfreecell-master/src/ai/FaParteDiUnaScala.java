package ai;

import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("faParteDiUnaScala")
public class FaParteDiUnaScala 
{
	@Param(0)
	private int cCheFaPa;
	
	public FaParteDiUnaScala(){}
	public FaParteDiUnaScala(int f)
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
