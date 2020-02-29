package freecell.images;

import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("esegui") 
public class Esegui {
	
	@Param(0)
	private int car;

	public Esegui(int car) {
		super();
		this.car = car;
	}

	public Esegui() {
		super();
	}

	public int getCar() {
		return car;
	}

	public void setCar(int car) {
		this.car = car;
	}

}
