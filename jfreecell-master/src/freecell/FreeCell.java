package freecell;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

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
	
	
	boolean first=true;

	ASPMapper m;

	private CardSource selectedSource;
	private static String encodingResource="encodings/freeCell";
	private static String instanceResource="encodings/instance";
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
			//finishedCells[i].addMouseListener(this);
		}		


		for (int i = 0; i < 8; i++) 
			columns[i] = new Column(i);
		//columns[i].addMouseListener(this);


		// When someone clicks the background, we want to be able to cancel moves.
		//getContentPane().addMouseListener(this);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		createGUI();
		getContentPane().setBackground(BACKGROUND_COLOR);

		// Start the game. Doing this here so the window will be sized correctly.
		
		start();

		pack(); //grafica
		setVisible(true);
		
		findMovableCards();
		findSolution();
	}

	private void start() 
	{
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

		// Deal
		deck.shuffle(); //rimescolare
		
		
		//generaRandom();
		generaDaFile();
		 
	}
	
	private void generaDaFile() 
	{
		char[] carte=new char[168];
		int j=0;
		////////////////////////METTO TUTTO IN UN ARRAY///////////////////////////////////////////////
		  try {
	            // apre il file in lettura
	            FileReader filein = new FileReader("resources/game2.txt");
	            
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

	private void generaRandom() {
		for (int i = 0; i < 52; i++) 
		{
			int id=i % 8;
			Card c=deck.draw();
			columns[id].initAdd(c); //prendo una carta a caso dal mazzo e la metto in una colonna 
			int p=columns[id].getCards().size();
			try {
				//System.out.println(c); System.out.println(p); System.out.println(id);
				facts.addObjectInput(new Column(c.getId(),p,id));
			}
			catch (Exception e) {
				e.printStackTrace();}
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
				if(c!=appenaSpostata)
				{
					//se c'è almeno una cella vuota le ultime carte possono essere spostate li
					if(cellaVuota)
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
						if(finishedCells[j].canAdd(c))
						{
							try {
								facts.addObjectInput(new MovableInF(c.getId(),finishedCells[j].getId()));
								//movF[pos2]=new MovableInF(c.getId(),columns[i]);pos2++;
							}
							catch (Exception e) {
								e.printStackTrace();}
						}
		
					}
				/*	//controllo se l'ultima carta può essere spostata in colonna 
					for(int co=0;co<8;co++)
					{
						if(columns[co].canAdd(c))
							try {
								facts.addObjectInput(new MovableInCo(c.getId(),co));
							}
						catch (Exception e) {
							e.printStackTrace();}
					}*/
					
					int spostate=0;
					for(int ca=columns[i].getCards().size()-1;ca>=0;ca--)
					{
						Card controllo=columns[i].getCards().get(ca); //se è l'ultima la controllo sicuro
						boolean trovata=false;
						if(spostate<=nContemporanee)
						{
							if(!(ca==columns[i].getCards().size()-1) && ca!=0) 
							{
								Card card=columns[i].getCards().get(ca);
								Card bottom = columns[i].getCards().get(ca-1); 
								
								if (bottom.getColor() != card.getColor() &&
										bottom.getRank() == card.getRank() + 1) {
									controllo=bottom; 
									trovata=true;
									try {
										facts.addObjectInput(new FaParteDiUnaScala(controllo.getId()));
									}
								catch (Exception e) {
									e.printStackTrace();}
								}
								else break; 
							}
						}
							
							for(int co=0;co<8;co++)
							{
								if(columns[co].canAdd(controllo))
									try {
										facts.addObjectInput(new MovableInCo(controllo.getId(),co));
										if(trovata)
										{
											spostate++;
											System.out.println("ne ho trovata una! "+controllo.getId()+co);
										}
									}
								catch (Exception e) {
									e.printStackTrace();}
							}
					}
			
				}
			}
			
		}
		
	}
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void findSolution()
	{
		handler.addProgram(facts);
		InputProgram encoding= new ASPInputProgram(); 
		//encoding.addProgram(getEncodings(encodingResource));
		encoding.addFilesPath(encodingResource);
		//encoding.addFilesPath(instanceResource);
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
		AnswerSets answers = (AnswerSets) o; 


		System.out.println(facts.getPrograms());
		//
		//System.out.println(answers.getAnswersets());

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
						if(columns[j].getCards().getLast().getId()==moveToC.getCa())
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
	
	private void removeCard(MoveToCo moveToCo) throws InterruptedException 
	{
		/*for(int i=0;i<8;i++)
		{
			if(!columns[i].getCards().isEmpty())
			{
				if(columns[i].getCards().getLast().getId()==moveToCo.getCaId())
				{
					for(int j=0;j<8;j++) 
					{
						if(columns[j].getColumn()==moveToCo.getCoId())
						{
							appenaSpostata=columns[i].getCards().getLast();
							columns[j].add(columns[i].remove());
							break;
						}
					}
				}
			}
		}*/
		
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
						if(j!=columns[i].getCards().size()-1) //se non è l'ultima 
						{
							for(int k=j;k<columns[i].getCards().size();k++) //sposto tutte le  carte a partire da quella
							{
								columns[column].add(columns[i].removeI(j));
							}
							appenaSpostata=columns[i].getCards().get(j);
							break;
						}
						else //se è l'ultima devo spostare solo quella
						{
							appenaSpostata=columns[i].getCards().get(j);
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

	private void createGUI() {
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		this.setLayout(gbl);

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
	}




	private void checkForVictory() {
		for (int i = 0; i < 4; i++) {
			if (!finishedCells[i].isComplete()) {
				return;
			}
		}

		int ret = JOptionPane.showOptionDialog(this, "Congratulations! You've won! Would you like to play again?", "Victory!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
		if (ret == JOptionPane.YES_OPTION) {
			this.start();
			
		}
		// If no, let them look at the board before they exit
	}



public void blocca() throws InterruptedException
{
	return;//Thread.sleep(5000);
}





	

	// mouseReleased behaves better than mouseClicked
	public void mouseReleased(MouseEvent me) {
		/*if (selectedSource == null) {
			if (me.getSource() instanceof CardSource) {
				CardSource cs = (CardSource)me.getSource();
				if (cs.canRemove()) {
					selectedSource = cs;
					selectedSource.select();
				}
			}
		}
		else
		{
			if (me.getSource() instanceof CardDestination) {
				CardDestination cd = (CardDestination)me.getSource();
				if (cd.canAdd(selectedSource.peek())) {
					cd.add(selectedSource.remove());
					selectedSource.unselect();
					selectedSource = null;
					autoFill();
					checkForVictory();
				}
				else if (cd == selectedSource) {
					// If you click the same thing twice, consider that a cancel move.
					selectedSource.unselect();
					selectedSource = null;
				}
				else {
					JOptionPane.showMessageDialog(this, "That move is illegal.", "Illegal Move", JOptionPane.ERROR_MESSAGE);
					selectedSource.unselect();
					selectedSource = null;
				}
			}
			else {
				selectedSource.unselect();
				selectedSource = null;
			}
		}*/
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	// Used to detect double-clicks.
	/*public void mouseClicked(MouseEvent me) {
		// This check basically ensures that the double-click was our intended
		// move from the get-go.
		if (selectedSource == null) {
			if (me.getClickCount() == 2) {
				if (me.getSource() instanceof Column) {
					Column c = (Column)me.getSource();
					for (int i = 0; i < 4; i++) {
						if (cells[i].canAdd(c.peek())) {
							cells[i].add(c.remove());
							autoFill();
							checkForVictory();
							return;
						}
					}

					JOptionPane.showMessageDialog(this, "Sorry, there are no free cells.", "Illegal Move", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	// Automatically adds appropriate cards to finished cells
	private void autoFill() {
		boolean keepGoing;

		// We keep going as long as something was added last time
		do {
			keepGoing = false;

			// Begin by finding out how many finished cells of each color have begun to be populated
			int numBlacks = 0;
			int numReds = 0;

			// We'll also keep track of the lowest card found of each color. These values only get used,
			// however, if there are two finished cells of the corresponding color.
			int lowestBlack = 14; // We use 14 because all cards are lower than this.
			int lowestRed = 14;

			for (int i = 0; i < 4; i++) {
				Card c = finishedCells[i].getTopCard();
				if (c != null) {
					if (c.getColor() == Color.BLACK) {
						numBlacks++;

						if (c.getRank() < lowestBlack) {
							lowestBlack = c.getRank();
						}
					}
					else {
						numReds++;

						if (c.getRank() < lowestRed) {
							lowestRed = c.getRank();
						}
					}
				}
			}

			// If there weren't two finished cells of a color, we set the corresponding lowest value
			// to zero to indicate empty.
			if (numBlacks < 2) {
				lowestBlack = 0;
			}

			if (numReds < 2) {
				lowestRed = 0;
			}

			// Now, go through the bottoms of the columns and the free cells and try to add if applicable
			ArrayList<CardSource> sources = new ArrayList<CardSource>();
			sources.addAll(Arrays.asList(columns));
			sources.addAll(Arrays.asList(cells));

			for (int i = 0; i < sources.size(); i++) {
				Card c = sources.get(i).peek();

				if (c != null) {
					// If the card is an A or a 2, we can just try to add to a finished cell
					if (c.getRank() <= 2) {
						for (int j = 0; j < 4; j++) {
							if (finishedCells[j].canAdd(c)) {
								finishedCells[j].add(sources.get(i).remove());
								keepGoing = true;
								break;
							}
						}
					}
					else {
						// If the card is a 3 or greater, we have to check to make sure no cards in the
						// stacks can still go under us. Only then do we add to a finished cell.
						if (c.getColor() == Color.BLACK && c.getRank() <= lowestRed + 1) {
							for (int j = 0; j < 4; j++) {
								if (finishedCells[j].canAdd(c)) {
									finishedCells[j].add(sources.get(i).remove());
									keepGoing = true;
									break;
								}
							}
						}
						else if (c.getColor() == Color.RED && c.getRank() <= lowestBlack + 1) {
							for (int j = 0; j < 4; j++) {
								if (finishedCells[j].canAdd(c)) {
									finishedCells[j].add(sources.get(i).remove());
									keepGoing = true;
									break;
								}
							}
						}
					}
				}
			}
		} while (keepGoing);
	}*/

	// Because we implement MouseListener, we have to include these empty methods
	public void mousePressed(MouseEvent me) {}
	public void mouseEntered(MouseEvent me) {}
	public void mouseExited(MouseEvent me) {}


	public static void main(String[] args) 
	{
		new FreeCell();
	}

	



}
