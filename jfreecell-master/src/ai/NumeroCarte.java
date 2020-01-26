package ai;

import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("numeroCarte")
public class NumeroCarte {
	
	@Param(0)
	private int nCarte;

	public NumeroCarte(int nCarte) {
		
		this.nCarte = nCarte;
	}

	public NumeroCarte() {
		
	}

	public int getNCarte() {
		return nCarte;
	}

	public void setNCarte(int nCarte) {
		this.nCarte = nCarte;
	}

}
