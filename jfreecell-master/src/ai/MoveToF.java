package ai;
import ai.MovableInCo;
import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;


@Id("moveToF")
public class MoveToF extends  MovableInCo 
{
	//private int cardId;
	@Param(1)
	private int finish;
	
	public MoveToF(int id,int f)
	{
		super(id);
		this.finish=f;
	}

	public int getFinish() {
		return finish;
	}

	public void setFinish(int finish) {
		this.finish = finish;
	}

}
