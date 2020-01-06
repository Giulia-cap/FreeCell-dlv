package freecell;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.NoSuchElementException;

import javax.swing.JPanel;

import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("cell") 
public class Cell extends JPanel implements CardSource, CardDestination {
	private static final long serialVersionUID = 4656851991098138209L;
	
	private Card card;
	@Param(0)
	private int idCarta;
	@Param(1)
	private int id;
	
	private boolean selected;
	
	public Cell() {}
	
	public Cell(int idCarta,int id) 
	{
		super();
		
		Dimension size = new Dimension(80, 120);
		setSize(size);
		setPreferredSize(size);
		setMinimumSize(size);
		
		this.idCarta=idCarta;
		this.id=id;
		
		selected = false;
	}
	
	public Card remove() 
	{
		if (card == null) {
			throw new NoSuchElementException();
		}
		
		Card ret = card;
		card = null;
		
		idCarta=53;
		
		repaint();
		
		return ret;
	}
	
	public Card peek() {
		return card;
	}
	
	public boolean canRemove() {
		return card != null;
	}
	
	public void select() {
		selected = true;
		repaint();
	}
	
	public void unselect() {
		selected = false;
		repaint();
	}
	
	public void add(Card card)
	{
		if (this.card != null) {
			throw new ArrayIndexOutOfBoundsException();
		}
		
		this.card = card;
		this.idCarta=card.getId();
		repaint();
	}
	
	public boolean canAdd(Card card) {
		return this.card == null;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Card getCard() {
		return card;
	}

	public void setCard(Card card) {
		this.card = card;
	}

	public int getIdCarta() {
		return idCarta;
	}

	public void setIdCarta(int idCarta) {
		this.idCarta = idCarta;
	}

	public void reset()
	{
		card = null;
		//selected = false;
		
		repaint();
	}
	
	public void paintComponent(Graphics g) 
	{
		if (card == null) {
			g.setColor(FreeCell.BACKGROUND_COLOR);
			g.fillRect(0, 0, getWidth(), getHeight());
			
			g.setColor(FreeCell.CELL_OUTLINE_COLOR);
			g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		}
		else {
			card.drawGraphics(g, new Point(0, 0));
			
			/*if (selected) {
				g.setColor(FreeCell.SELECTED_COLOR);
				g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
				g.drawRect(1, 1, getWidth() - 3, getHeight() - 3);
			}*/
		}
	}

}