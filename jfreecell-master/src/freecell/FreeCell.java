package freecell;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import ai.MovableInCe;
import ai.MovableInCo;
import ai.MovableInF;
import ai.MoveToCe;
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


public class FreeCell extends JFrame /*implements MouseListener*/ {	
	private static final long serialVersionUID = -4727296451255399519L;
	
	public static final Color BACKGROUND_COLOR = new Color(61, 145, 64);
	public static final Color SELECTED_COLOR = Color.blue;
	public static final Color CELL_OUTLINE_COLOR = new Color(0, 255, 127);
	
	private Deck deck;
	private Cell[] cells;
	private FinishedCell[] finishedCells;
	private Column[] columns;
	private static Handler handler;
	
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
			try {
				    facts.addObjectInput(finishedCells[i] );  //ho messo come seme iniziale spade, i controlli su dlv tanto li faccio con la top card:se è 53 vuol dire che la cella è vuota
					} 
					catch (Exception e) {
						e.printStackTrace();
					}
				//finishedCells[i].addMouseListener(this);
			}		
		
			
		for (int i = 0; i < 8; i++) 
			columns[i] = new Column();
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
	}
	
	private void start()   //MI SERVE SOLO PER CREARE LA SCHERMATA DI PARTENZA
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
		
		findMovableCards();
		findSolution();
		
	}
	
	
	private void findMovableCards()  // per ora prende solo le ultime carte 
	{
	//	movCe=null; int pos=0; movF=null; int pos2=0;
		boolean cellaVuota=false;
		for(int j=0;j<4;j++) 
		{
			if(cells[j].getIdCarta()==53) 
			{
				 cellaVuota=true;
				 break;
			}
		}
		
		for(int i=0;i<8;i++)
		{
			Card c=columns[i].getCards().getLast();
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
			//controllo se la carta può essere spostata in colonna (per ora controlla solo l'ultima
			for(int co=0;co<8;co++)
			{
				if(columns[co].canAdd(columns[i].getCards().getLast()))
					try {
						facts.addObjectInput(new MovableInCo(c.getId(),columns[co].getColumn()));
						}
					catch (Exception e) {
						e.printStackTrace();}
			}
		}

	}
	
	private void findSolution()
	{
		handler.addProgram(facts);
		InputProgram encoding= new ASPInputProgram(); 
		//encoding.addProgram(getEncodings(encodingResource));
		encoding.addFilesPath(encodingResource);
		encoding.addFilesPath(instanceResource);
		handler.addProgram(encoding);
		
		try {
			ASPMapper.getInstance().registerClass(MoveToCe.class); 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		Output o =  handler.startSync();
		AnswerSets answers = (AnswerSets) o; 
		
		
		System.out.println(facts.getPrograms());
		System.out.println(answers.getAnswersets());

		for(AnswerSet a:answers.getAnswersets())
		{ // per ogni as. il get restituisce un set di as in ordine di ottimalità
			try {
				for(Object obj:a.getAtoms()){
					if(obj instanceof MoveToCe)  {
						MoveToCe move = (MoveToCe) obj;
						System.out.print(move + " ");
					}
				}
				System.out.println();
			} catch (Exception e) {
				e.printStackTrace();
			} 			
		}
		
	}
	
	
	
	private void removeCard(MoveToF moveToF) 
	{
		
		
		for(int i=0;i<4;i++)
		{
			if(finishedCells[i].getId()==moveToF.getFinish())
			{
				for(int j=0;j<8;j++)
				{
					if(columns[j].getCard()==moveToF.getCardId())
					{
						finishedCells[i].add(columns[j].remove()); break;
					}
				}
				break;
			}
		}
		selectedSource = null;
		checkForVictory();
		
	}
	private void removeCard(MoveToCe moveToC) 
	{
		for(int i=0;i<4;i++)
		{
			if(cells[i].getId()==moveToC.getCe())
			{
				for(int j=0;j<8;j++)
				{
					if(columns[j].getCard()==moveToC.getCa())
					{
						cells[i].add(columns[j].remove()); break;
					}
				}
				break;
			}
		}
		
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
	
	
	
	
	
	

	
	
	/*
	
	// mouseReleased behaves better than mouseClicked
	public void mouseReleased(MouseEvent me) {
		if (selectedSource == null) {
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
		}
	}
	
	// Used to detect double-clicks.
	public void mouseClicked(MouseEvent me) {
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
	}
	
	// Because we implement MouseListener, we have to include these empty methods
	public void mousePressed(MouseEvent me) {}
	public void mouseEntered(MouseEvent me) {}
	public void mouseExited(MouseEvent me) {}*/
	
	
		public static void main(String[] args) 
		{
			new FreeCell();
		}

	
	
}
