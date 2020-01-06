package ai;
import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("spostabileDaCellaAColonna")
public class MovableFromeCeInCo 
{
		@Param(0)
		private int CellFrom;
		@Param(1)
		private int to;
		
		public MovableFromeCeInCo() {}
		
		public MovableFromeCeInCo(int Card,int Column)
		{
			this.CellFrom=Card;
			this.to=Column;
		}


		public int getCellFrom() {
			return CellFrom;
		}

		public void setCellFrom(int cellFrom) {
			CellFrom = cellFrom;
		}

		public int getTo() {
			return to;
		}

		public void setTo(int to) {
			this.to = to;
		}

		
	
}
