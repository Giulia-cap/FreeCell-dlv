package freecell;

import java.awt.Button;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;


public class Menu extends JFrame implements MouseListener {

	private static final long serialVersionUID = 1L;
	public static final Color BACKGROUND_COLOR = new Color(61, 145, 64);
	private Button bstart;
	
	public  Menu()
	{
		super("FreeCell");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		bstart=new Button("start");
		bstart.addMouseListener(this);
		
		this.add(bstart);
		
		getContentPane().setBackground(BACKGROUND_COLOR);
		
		pack(); //grafica
		setVisible(true);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getSource()==bstart) 
		{
			
			this.dispose();
			new FreeCell();
		}
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	public static void main(String[] args) 
	{
		new Menu();
	}

}
