package ai;
import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("spostabileDaCellaAFinish")
public class MovableFromeCeInFinish 
{
		@Param(0)
		private int C1;
		@Param(1)
		private int to1;
		
		public MovableFromeCeInFinish() {}
		
		public MovableFromeCeInFinish(int Card,int Column)
		{
			this.C1=Card;
			this.to1=Column;
		}

		public int getC1() {
			return C1;
		}

		public void setC1(int c1) {
			C1 = c1;
		}

		public int getTo1() {
			return to1;
		}

		public void setTo1(int to1) {
			this.to1 = to1;
		}

		
	
}
