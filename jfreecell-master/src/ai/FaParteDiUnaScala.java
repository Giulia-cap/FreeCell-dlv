package ai;

import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("faParteDiUnaScala")
public class FaParteDiUnaScala 
{
	@Param(0)
	private int faPa;

	public int getFaPa() {
		return faPa;
	}

	public void setFaPa(int faPa) {
		this.faPa = faPa;
	}

	public FaParteDiUnaScala() {
		super();
	}

	public FaParteDiUnaScala(int faPa) {
		super();
		this.faPa = faPa;
	}
	
	
}
