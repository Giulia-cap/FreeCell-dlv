package freecell;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ai.ProssimaInScala;
import ai.FaParteDiUnaScala;
import ai.MovableFromeCeInCo;
import ai.MovableFromeCeInFinish;
import ai.MovableInCe;
import ai.MovableInCo;
import ai.MovableInF;
import ai.MoveFromCeToColumn;
import ai.MoveFromCeToFinish;
import ai.MoveToCe;
import ai.MoveToCo;
import ai.MoveToF;
import ai.NumeroCarte;
import freecell.Card.Suit;
import it.unical.mat.embasp.base.Handler;
import it.unical.mat.embasp.base.InputProgram;
import it.unical.mat.embasp.base.Output;
import it.unical.mat.embasp.languages.asp.ASPInputProgram;
import it.unical.mat.embasp.languages.asp.ASPMapper;
import it.unical.mat.embasp.languages.asp.AnswerSet;
import it.unical.mat.embasp.languages.asp.AnswerSets;
import it.unical.mat.embasp.platforms.desktop.DesktopHandler;
import it.unical.mat.embasp.specializations.dlv.desktop.DLVDesktopService;


public class FreeCell extends JFrame implements MouseListener {	
	private static final long serialVersionUID = -4727296451255399519L;

	public static final Color BACKGROUND_COLOR = new Color(61, 145, 64);
	public static final Color SELECTED_COLOR = Color.blue;
	public static final Color CELL_OUTLINE_COLOR = new Color(0, 255, 127);

	private Deck deck;
	private Cell[] cells;
	private FinishedCell[] finishedCells;
	private Column[] columns;
	private static Handler handler;

	private Card appenaSpostata;
	private int nContemporanee=0;

	private int level=1;

	private String res;

	Button bstop;
	Button brallenta;
	Button bvelocizza;
	boolean first=true;
	boolean assi=true,centro,fine;
	boolean stop=false,rallenta=true,velocizza=false,colonneVuote=false;

	ASPMapper m;

	private CardSource selectedSource;
	//private static String encodingResourceAssi="encodings/liberaAssi";
	private static String encodingResourceAssi="encodings/freeCellAI";
	//private static String encodingResourceCentro="encodings/centroDelGioco";
	private static String encodingResourceCentro="encodings/freeCellAI";
	private static String encodingResourceFine="encodings/giocoQuasiFinito";
	private static String encodingResourceColonneLibere="encodings/giocoConColonneLibere";
	public InputProgram facts;

	public FreeCell() 
	{
		super("FreeCell");

		deck = new Deck();
		cells = new Cell[4];
		finishedCells = new FinishedCell[4];
		columns = new Column[8];

		handler = new DesktopHandler(new DLVDesktopService("lib/dlv.mingw.exe")); 
		facts= new ASPInputProgram();

		for (int i = 0; i < 4; i++) 
		{
			cells[i] = new Cell(53,i);
			try {
				facts.addObjectInput(cells[i]);
			} 
			catch (Exception e) {
				e.printStackTrace();
			}

			//cells[i].addMouseListener(this);

			finishedCells[i] = new FinishedCell(53,Suit.SPADES,i);
			FinishedCell fin = new FinishedCell(53,"SPADES",i);
			try {
				facts.addObjectInput(fin );  //ho messo come seme iniziale spade, i controlli su dlv tanto li faccio con la top card:se è 53 vuol dire che la cella è vuota
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}		


		for (int i = 0; i < 8; i++) 
			columns[i] = new Column(i);


		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		int ret = JOptionPane.showOptionDialog(this, "press ok to start the EASY mode", "start!", JOptionPane.OK_OPTION, JOptionPane.OK_OPTION, null, null, null);
		if (ret == JOptionPane.OK_OPTION) {

			createGUI();
			getContentPane().setBackground(BACKGROUND_COLOR);
			start();
			pack(); 
			setVisible(true);

			findMovableCards();

			findSolution();
		}
	}

	boolean booleana=false;
	JLabel label = new JLabel();

	private void start() 
	{
		gbc.gridx = 9;
		gbc.anchor = GridBagConstraints.CENTER;

		label.setText("Livello"+" "+level);

		this.add(label,gbc);
		// Reset
		for (int i = 0; i < 4; i++) 
		{
			cells[i].reset();
			finishedCells[i].reset();
		}

		for (int i = 0; i < 8; i++) 
		{
			columns[i].reset();
		}


		deck.shuffle(); //rimescolare

		generaDaFile();

	}

	private void generaDaFile() 
	{
		if(level==1)
			res="resources/easy.txt";
		else if(level==2)
			res="resources/medium.txt";
		else
			res="resources/hard.txt";

		char[] carte=new char[168];
		int j=0;

		
		try {
			// apre il file in lettura
			FileReader filein = new FileReader(res);

			int next;
			do {
				next = filein.read(); // legge il prossimo carattere

				if (next != -1) { // se non e' finito il file
					char nextc = (char) next;
					// System.out.print(j +"-"+ nextc+ " ");
					carte[j]=nextc; j++;
				}

			} while (next != -1);

			filein.close(); // chiude il file

		} catch (IOException e) {
			System.out.println(e);
		}
		///////////////////////////////////////////////////////////////////////////////////////////////

		int colonna=0;
		int val;
		Suit seme;

		for(int i=0;i<carte.length;)
		{
			if(converti(carte[i])!=0)
			{
				val=converti(carte[i]); i++;
				seme=controllaSeme(carte[i]);i++;
				Card c=deck.restituisciCarta(val, seme);
				columns[colonna].initAdd(c); //metto la carta nella colonna  
				int p=columns[colonna].getCards().size();
				try {
					facts.addObjectInput(new Column(c.getId(),p,colonna));
				}
				catch (Exception e) {
					e.printStackTrace();}
				if(colonna==7)
					colonna=0;
				else
					colonna++;
			}
			else i++;
		}

	}



	private Suit controllaSeme(char seme) {
		switch(seme)
		{
		case 'C': return Suit.CLUBS; 
		case 'D': return Suit.DIAMONDS;
		case 'H': return Suit.HEARTS;
		case 'S': return Suit.SPADES;
		}
		return null;

	}

	private int converti(char v)
	{
		switch (v) {
		case 'A':
			return 1;
		case '2':
			return 2;
		case '3':
			return 3;
		case '4':
			return 4;
		case '5':
			return 5;
		case '6':
			return 6;
		case '7':
			return 7;
		case '8':
			return 8;
		case '9':
			return 9;
		case 'T':
			return 10;
		case 'J':
			return 11;
		case 'Q':
			return 12;
		case 'K':
			return 13;
		}
		return 0;
	}

	int prova=0;

	private void findNewFacts()
	{
		generaCoperta();
		for (int i = 0; i < 4; i++) 
		{
			try {
				facts.addObjectInput(cells[i]);
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		for(int i=0;i<4;i++)
		{
			String s="ciao";
			if(finishedCells[i].getSuit()==Suit.CLUBS)
				s="CLUBS";
			else if(finishedCells[i].getSuit()==Suit.DIAMONDS)
				s="DIAMONDS";
			else if(finishedCells[i].getSuit()==Suit.HEARTS)
				s="HEARTS";
			else if(finishedCells[i].getSuit()==Suit.SPADES)
				s="SPADES";

			try {
				facts.addObjectInput(new FinishedCell(finishedCells[i].getTop(),s,i) );  
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}


		for(int i=0;i<8;i++)
		{
			for(int j=0;j<columns[i].getCards().size();j++)
			{
				Card c=columns[i].getCards().get(j);
				try {
					facts.addObjectInput(new Column(c.getId(),j,i));
				}
				catch (Exception e) {
					e.printStackTrace();}
			}
		}

		cercaStrategia();
	}



	private void generaCoperta() {
		Card carta;

		for(int i=0;i<8;i++)
		{
			if(!(columns[i].getCards().size()>1)) break;
			carta=columns[i].getCards().get(columns[i].getCards().size()-2);

			for(int j=0;j<8;j++)
			{
				if(i!=j)
				{
					if(!columns[j].getCards().isEmpty()) {
						Card bottom = columns[j].getCards().getLast(); 
						if (bottom.getColor() != carta.getColor() &&
								bottom.getRank() == carta.getRank() + 1)
						{
							try {
								facts.addObjectInput(new ProssimaInScala(carta.getId()));
							}
							catch (Exception e) {
								e.printStackTrace();}
						}
					}
				}
			}
		}

	}

	private void cercaStrategia()
	{

		assi=false;
		centro=false;
		fine=false;
		colonneVuote=false;
		int ncarte=0;
		for(int i=0;i<8;i++)
		{
			ncarte+=columns[i].getCards().size();
			if(columns[i].getCards().isEmpty()) {colonneVuote=true;}
			//if(assi) break;
			for(int j=0;j<columns[i].getCards().size();j++) {
				if((!columns[i].getCards().isEmpty()) && columns[i].getCards().get(j).getRank()==1 ) //vedo se devo liberare ancora assi
				{
					assi=true;
				}}
		}


		try {
			facts.addObjectInput(new NumeroCarte(ncarte));
		}
		catch (Exception e) {
			e.printStackTrace();}

		for(int i=0;i<4;i++)
			if(cells[i].getIdCarta()!=53)
				ncarte++;

		if(!assi && ncarte<=20)
			fine=true;
		else
			centro=true;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void findMovableCards() 
	{
		//	movCe=null; int pos=0; movF=null; int pos2=0;
		nContemporanee=1;
		boolean cellaVuota;
		if(!first)
		{
			facts.clearAll();
			findNewFacts();
			cellaVuota=false;
		}
		else cellaVuota=true;

		for(int j=0;j<4;j++) 
		{
			Card ca=cells[j].getCard();
			if(ca!=appenaSpostata)
			{
				if(cells[j].getIdCarta()==53) 
				{
					cellaVuota=true;
					nContemporanee++;
					//break;
				}
				else
				{
					for(int co=0;co<8;co++)
					{
						if(columns[co].canAdd(ca))
							try {
								facts.addObjectInput(new MovableFromeCeInCo(cells[j].getId(),co));
							}
						catch (Exception e) {
							e.printStackTrace();}
					}

					for(int h=0;h<4;h++)
					{
						if(finishedCells[h].canAdd(ca))
							try {
								facts.addObjectInput(new MovableFromeCeInFinish(cells[j].getIdCarta(),h));
							}
						catch (Exception e) {
							e.printStackTrace();}
					}
				}
			}
		}

		for(int i=0;i<8;i++)
		{
			if(!columns[i].getCards().isEmpty())
			{
				Card c=columns[i].getCards().getLast();
				//if(c!=appenaSpostata)
				//{
				//se c'è almeno una cella vuota le ultime carte possono essere spostate li
				if(cellaVuota && (c!=appenaSpostata))
				{
					try {
						facts.addObjectInput(new MovableInCe(c.getId()));
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
				//controllo se la carta può essere inserita nelle finished cell
				for(int j=0;j<4;j++)
				{
					if(finishedCells[j].canAdd(c) && (c!=appenaSpostata) )
					{
						try {
							facts.addObjectInput(new MovableInF(c.getId(),finishedCells[j].getId()));
							//movF[pos2]=new MovableInF(c.getId(),columns[i]);pos2++;
						}
						catch (Exception e) {
							e.printStackTrace();}
					}

				}


				////////////////////////////// Ultima carta di ogni colonna//////////////	
				Card ultima=columns[i].getCards().getLast();

				for(int co=0;co<8;co++)
				{
					if(columns[co].canAdd(ultima) && co!=i && (ultima!=appenaSpostata)  && (c!=appenaSpostata) )
						try {
							facts.addObjectInput(new MovableInCo(ultima.getId(),co));
						}
					catch (Exception e) {
						e.printStackTrace();}
				}
				////////////////////////////////////////////////////////////////////////////////////////////////////////			

				int spostate=0;
				LinkedList<Card> tmp= new LinkedList<Card>();
				boolean almenoUna=false;
				for(int ca=columns[i].getCards().size()-1;ca>=0;ca--)
				{
					if(spostate<=nContemporanee)
					{
						if(ca!=0)
						{
							Card card=columns[i].getCards().get(ca);
							Card bottom = columns[i].getCards().get(ca-1); 
							if (bottom.getColor() != card.getColor() &&
									card.getRank() == bottom.getRank() - 1)
							{
								tmp.add(card);
								spostate++;
								almenoUna=true;
							}
							else if(almenoUna)
							{
								tmp.add(card);
								spostate++;
								break;
							}
							else break;
						}
					}
				}


				if(!(tmp.isEmpty())) {
					Card papabile=tmp.getLast();
					for(int co=0;co<8;co++)
					{
						if(columns[co].canAdd(papabile) && co!=i &&(papabile!=appenaSpostata) && (c!=appenaSpostata) )
							try {
								facts.addObjectInput(new MovableInCo(papabile.getId(),co));
								facts.addObjectInput(new FaParteDiUnaScala(papabile.getId()));
							}
						catch (Exception e) {
							e.printStackTrace();}
					}}
			}

		}


	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void findSolution()
	{

		handler.addProgram(facts);
		InputProgram encoding= new ASPInputProgram();
		encoding.clearAll();



		if(fine)
		{
			handler = new DesktopHandler(new DLVDesktopService("lib/dlv.mingw.exe"));
			handler.addProgram(facts);
			encoding.addFilesPath(encodingResourceFine);
		}
		else if(colonneVuote)
		{
			handler = new DesktopHandler(new DLVDesktopService("lib/dlv.mingw.exe"));
			handler.addProgram(facts);
			encoding.addFilesPath(encodingResourceColonneLibere);
		}
		else if(assi) {
			handler = new DesktopHandler(new DLVDesktopService("lib/dlv.mingw.exe"));
			handler.addProgram(facts);
			encoding.addFilesPath(encodingResourceAssi);
		}
		else if(centro) { 
			handler = new DesktopHandler(new DLVDesktopService("lib/dlv.mingw.exe"));
			handler.addProgram(facts);
			encoding.addFilesPath(encodingResourceCentro);
		}
		else {
			handler = new DesktopHandler(new DLVDesktopService("lib/dlv.mingw.exe"));
			handler.addProgram(facts);
			encoding.addFilesPath(encodingResourceFine);
		}


		handler.addProgram(encoding);

		first=false;

		try {
			ASPMapper.getInstance().registerClass(MoveToCe.class); 

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			ASPMapper.getInstance().registerClass(MoveToF.class); 

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			ASPMapper.getInstance().registerClass(MoveToCo.class); 

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			ASPMapper.getInstance().registerClass(MoveFromCeToColumn.class); 

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			ASPMapper.getInstance().registerClass(MoveFromCeToFinish.class); 

		} catch (Exception e) {
			e.printStackTrace();
		}

		Output o =  handler.startSync();
		AnswerSets answers= (AnswerSets) o;
		System.out.println(facts.getPrograms());

		for(AnswerSet a:answers.getAnswersets())
		{ // per ogni as. il get restituisce un set di as in ordine di ottimalità
			try {
				for(Object obj:a.getAtoms())
				{
					if(obj instanceof MoveToCe)  {
						MoveToCe move = (MoveToCe) obj;
						System.out.print(move + " ");
						removeCard(move);
						break;
					}
					if(obj instanceof MoveToF)  {
						MoveToF move = (MoveToF) obj;
						System.out.print(move + " ");
						removeCard(move);
						break;
					}
					if(obj instanceof MoveToCo)  {
						MoveToCo move = (MoveToCo) obj;
						System.out.print(move + " ");
						removeCard(move);
						break;
					}
					if(obj instanceof MoveFromCeToColumn)  {
						MoveFromCeToColumn move = (MoveFromCeToColumn) obj;
						System.out.print(move + " ");
						removeCard(move);
						break;
					}
					if(obj instanceof MoveFromCeToFinish)  {
						MoveFromCeToFinish move = (MoveFromCeToFinish) obj;
						System.out.print(move + " ");
						removeCard(move);
						break;
					}
				}
				System.out.println();
			} catch (Exception e) {
				e.printStackTrace();
			} 
			break;
		}


	}



	private void removeCard(MoveToF moveToF) throws InterruptedException 
	{
		for(int i=0;i<4;i++)
		{
			if(finishedCells[i].getId()==moveToF.getFinish())
			{
				for(int j=0;j<8;j++)
				{
					if(/*!columns[i].getCards().isEmpty() &&*/ !columns[j].getCards().isEmpty())
					{
						if(columns[j].getCards().getLast().getId()==moveToF.getCardId())
						{
							appenaSpostata=columns[j].getCards().getLast();
							finishedCells[i].add(columns[j].remove());
							break;
						}
					}
				}
				break;
			}
		}
		selectedSource = null;
		checkForVictory();

		blocca();

		findMovableCards();
		findSolution();


	}



	private void removeCard(MoveToCe moveToC) throws InterruptedException 
	{

		for(int i=0;i<4;i++)
		{
			if(cells[i].getId()==moveToC.getCe())
			{
				for(int j=0;j<8;j++)
				{
					if(!columns[i].getCards().isEmpty())
					{
						if((!columns[j].getCards().isEmpty())&&columns[j].getCards().getLast().getId()==moveToC.getCa())
						{
							appenaSpostata=columns[j].getCards().getLast();
							cells[i].add(columns[j].remove());
							repaint();
							break;
						}
					}
				}
				break;
			}
		}


		blocca();

		findMovableCards();
		findSolution();


	}

	private int verifica(Card daMuovere)
	{

		for(int i=0;i<cells.length;i++)
		{
			
			if(cells[i].getIdCarta()<53)
			{
				System.out.println("vedi "+cells[i].getId());
				Card bottom=cells[i].getCard();
				if (bottom.getColor() != daMuovere.getColor() &&
						bottom.getRank() == daMuovere.getRank() + 1)
				{
					return i;
				}
			}
		}
		return 6;
	}

	private void removeCard(MoveToCo moveToCo) throws InterruptedException 
	{
		int column=0;

		for(int i=0;i<8;i++)
		{
			if(columns[i].getColumn()==moveToCo.getCoId()) {
				column=i; break;}  //individuo la colonna su cui spostare la carta
		}

		for(int i=0;i<8;i++)
		{
			if(i!=column)
			{
				for(int j=0;j<columns[i].getCards().size();j++)
				{
					if(columns[i].getCards().get(j).getId()==moveToCo.getCaId()) //individuo la carta che devo spostare
					{

						if(colonneVuote && !assi)
						{
							int cell=verifica(columns[i].getCards().get(j));


							if(cell!=6)
							{
								appenaSpostata=cells[cell].getCard();
								columns[column].add(cells[cell].remove());
								break;
							}


						}



						appenaSpostata=columns[i].getCards().get(j);
						//System.out.println("appena spostata: "+appenaSpostata);
						if(j!=columns[i].getCards().size()-1) //se non è l'ultima 
						{
							for(int k=j;k<columns[i].getCards().size();) //sposto tutte le  carte a partire da quella
							{
								if(columns[column].canAdd((columns[i].getCards().get(j))))
									columns[column].add(columns[i].removeI(j));
								else break;

							}

							//break;
						}
						else //se è l'ultima devo spostare solo quella
						{
							appenaSpostata=columns[i].getCards().get(j);
							//System.out.println("appena spostata: "+appenaSpostata);
							columns[column].add(columns[i].remove());
							break;	
						}
					}
				}
			}
		}


		selectedSource = null;

		blocca();

		findMovableCards();
		findSolution();


	}

	private void removeCard(MoveFromCeToColumn move) throws InterruptedException
	{
		for(int i=0;i<4;i++)
		{
			if(cells[i].getId()==move.getCefrom())
			{
				for(int j=0;j<8;j++) 
				{
					if(columns[j].getColumn()==move.getCoTo())
					{
						appenaSpostata=cells[i].getCard();
						columns[j].add(cells[i].remove());
						break;
					}
				}
			}
		}

		checkForVictory();

		blocca();

		findMovableCards();
		findSolution();
	}

	private void removeCard(MoveFromCeToFinish move) throws InterruptedException
	{
		for(int i=0;i<4;i++)
		{
			if(cells[i].getIdCarta()==move.getCardFrom1())
			{
				for(int j=0;j<4;j++) 
				{
					if(finishedCells[j].getId()==move.getFinish1())
					{
						appenaSpostata=cells[i].getCard();
						finishedCells[j].add(cells[i].remove());
						break;
					}
				}
			}
		}

		checkForVictory();

		blocca();

		findMovableCards();
		findSolution();
	}

	//------------------GRAFICA-----------------
	GridBagConstraints gbc = new GridBagConstraints();
	private void createGUI() {
		GridBagLayout gbl = new GridBagLayout();

		this.setLayout(gbl);

		bstop=new Button("stoppa");
		bstop.addMouseListener(this);

		brallenta=new Button("rallenta");
		brallenta.addMouseListener(this);

		bvelocizza=new Button("velocizza");
		bvelocizza.addMouseListener(this);


		gbc.gridy = 0;
		gbc.insets = new Insets(2, 2, 2, 2);
		for (int i = 0; i < 4; i++) {
			this.add(cells[i], gbc);
		}


		this.add(new JPanel() {
			private static final long serialVersionUID = -6234059593255900935L;

			public Color getBackground() {
				return FreeCell.BACKGROUND_COLOR;
			}

			public Dimension getPreferredSize() {
				return new Dimension(80, 120);
			}

		}, gbc);


		for (int i = 0; i < 4; i++) {
			this.add(finishedCells[i], gbc);
		}



		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.weighty = 1;
		for (int i = 0; i < 8; i++) {
			this.add(columns[i], gbc);
		}



		gbc.anchor = GridBagConstraints.NORTHEAST;
		gbc.gridy = 1;
		gbc.gridx = 8;
		this.add(bstop,gbc);
		gbc.gridx = 9;
		this.add(bvelocizza,gbc);
		gbc.gridx = 10;
		this.add(brallenta,gbc);

	}




	private void checkForVictory() {
		String mode;


		mode="MEDIUM";

		if(level==2)
			mode="HARD";

		for (int i = 0; i < 4; i++) {
			if (!finishedCells[i].isComplete()) {
				return;
			}
		}

		if(level>=3)
		{
			int ret = JOptionPane.showOptionDialog(this, "Congratulations! You've won! Would you like to play again?", "Victory!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
			if (ret == JOptionPane.YES_OPTION) {
				level=1;
				this.start();

			}
		}

		else
		{
			rallenta=true;
			velocizza=false;
			int ret = JOptionPane.showOptionDialog(this, "Congratulations! You've won! Would you like to play "+ mode+ " mode?", "Victory!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
			if (ret == JOptionPane.YES_OPTION) {
				level++;
				this.start();

			}
		}

	}



	public void blocca() throws InterruptedException
	{
		while(stop) System.out.print(stop);

		if(rallenta)
		{
			Thread.sleep(2000);
		}
		return;
	}








	public void mouseReleased(MouseEvent me) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}




	public void mousePressed(MouseEvent me) { 


		if (me.getSource()==bstop) 
		{
			if(stop==false) 
				stop=true;
			else 
				stop=false;
		}
		else if(me.getSource()==brallenta)
		{
			rallenta=true;
			velocizza=false;

		}
		else if(me.getSource()==bvelocizza)
		{
			velocizza=true;
			rallenta=false;
		}
	}
	public void mouseEntered(MouseEvent me) {}
	public void mouseExited(MouseEvent me) {}


	public static void main(String[] args) 
	{
		new FreeCell();
	}




}
