package freecell;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JPanel;

import freecell.Card.Suit;
import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("finishedCell")
public class FinishedCell extends JPanel implements CardDestination {
	private static final long serialVersionUID = 1502461858912278004L;
	
	private Card topC;
	//private Suit suit;
	
	
	@Param(0)
	private int top;
	@Param(1) 
	private String suitDlv;
	@Param(2)
	private int id;
	
	private Suit suit;
	
	public FinishedCell() {}
	
	public FinishedCell(int tC,String su,int id) 
	{
		this.top=tC;
		this.suitDlv=su;
		this.id=id; 
	}
	
	
	public String getSuitDlv() {
		return suitDlv;
	}

	public void setSuitDlv(String suitDlv) {
		this.suitDlv = suitDlv;
	}

	public FinishedCell(int tC,Suit su,int id) 
	{
		super();

		Dimension size = new Dimension(80, 120);
		setSize(size);
		setPreferredSize(size);
		setMinimumSize(size);
	
		this.top=tC;
		this.suit=su;
		this.id=id;  //TI SERVONO DAVVERO?
	}
	

	public int getTop() {
		return top;
	}


	public void setTop(int top) {
		this.top = top;
	}


	public void add(Card card) 
	{
		if (!canAdd(card)) {
			throw new IllegalArgumentException();
		}
		
		//if (suit == null) {
			suit = card.getSuit();
		//}
		
		topC = card;
		top=card.getId();
		
		
		String s="ciao";
		if(card.getSuit()==Suit.CLUBS)
			s="CLUBS";
		else if((card.getSuit()==Suit.DIAMONDS))
			s="DIAMONDS";
		else if((card.getSuit()==Suit.HEARTS))
			s="HEARTS";
		else if((card.getSuit()==Suit.SPADES))
			s="SPADES";
		
		suitDlv=s;
		
		repaint();
	}
	
	public boolean canAdd(Card card) 
	{
		int currentRank = (topC == null) ? 0 : topC.getRank();
		if(currentRank==0 && card.getRank()==1) return true;
		if (card.getRank() == currentRank + 1) 
		{ 
			if (suit == null || card.getSuit() == suit) 
			{
				return true;
			}
		}
		return false;
	}
	
	public Card getTopCard() {
		return topC;
	}
	
	public boolean isComplete() {
		return topC == null ? false : topC.getRank() == 13;
	}
	
	public void reset() {
		topC = null;
		
		repaint();
	}
	
	public void paintComponent(Graphics g) {
		if (topC == null) {
			g.setColor(FreeCell.BACKGROUND_COLOR);
			g.fillRect(0, 0, getWidth(), getHeight());
			
			g.setColor(FreeCell.CELL_OUTLINE_COLOR);
			g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		}
		else {
			topC.drawGraphics(g, new Point(0, 0));
		}
	}

	public Card getTopC() {
		return topC;
	}

	public Suit getSuit() {
		return suit;
	}

	public void setSuit(Suit suit) {
		this.suit = suit;
	}

	public int getId() {
		return id;
	}
	

	public Suit getSu() {
		return suit;
	}

	public void setSu(Suit su) {
		this.suit = su;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	
}
