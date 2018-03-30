package playerClasses;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class CrossHairs implements MouseListener, MouseMotionListener {
	private final int SHOT_SENSITIVITY = 10;
	private final int OFFSET = 15;

	protected boolean visible, justShot;
	private int mouseX, mouseY/*, loaded*/;
	private int shotX, shotY;
	private int width, height;
	private Image image;
	private Board board;

	public CrossHairs(Board board, String name){this.board = board;
		URL url = getClass().getResource("/images/crosshairs-"+name+".png");
	   	if(url == null)
	   		JOptionPane.showMessageDialog(board, "unable to find /images/crosshairs-"+name+".png", "Load image error", JOptionPane.ERROR_MESSAGE);
	   	
	   	else image = new ImageIcon(url).getImage();
	   	
//	 	loaded = 10;//10 free shots to start with
		width = SHOT_SENSITIVITY;
		height = SHOT_SENSITIVITY;
		visible = true;
		mouseX = 40; mouseY = 60;
		board.addMouseListener(this);
		board.addMouseMotionListener(this);
//		board.setCursor(board.getToolkit().createCustomCursor(new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "null"));
	}

	public int getX(){return mouseX;}
	public int getY(){return mouseY;}
	
	public Rectangle getShotBounds(){
		Rectangle result = new Rectangle(shotX, shotY, width, height);
		// System.out.format("shotx=%d, shoty=%d, width=%d, height=%d\n", shotX, shotY, width, height);
		shotX =  shotY = -OFFSET;//put the shot area off the board
		return result;
	}
	
	public Image getImage(){return image;}
//	public void addLoaded(int num){loaded+=num;}
	public void setVisible(boolean visible){this.visible = visible;}

	public boolean isVisible(){return visible;}

	@Override public void mouseEntered(MouseEvent e){setVisible(true);
		board.setCursor(board.getToolkit().createCustomCursor(new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "null"));
	}
	
	@Override public void mouseExited(MouseEvent e){setVisible(false);}
	
	@Override public void mouseClicked(MouseEvent e){/*do nothing*/}
	
	@Override public void mousePressed(MouseEvent e){
		if (visible/* && loaded > 0*/&& board.thisCraft.shotsLeft>0 && justShot==false){board.thisCraft.addShots(-1);//loaded--;
			board.soundPlayer.playSound(board, "rifle.wav");
			shotX = e.getX() + OFFSET;
			shotY = e.getY() + OFFSET;
			justShot = true;
		}
		if(board.contains(mouseX, mouseY))board.requestFocus();
	}
	
	@Override public void mouseReleased(MouseEvent e){/*do nothing*/}
	
	@Override public void mouseDragged(final MouseEvent e){
		mouseX = e.getX();
		mouseY = e.getY();
	}
	
	@Override public void mouseMoved(MouseEvent e){
		mouseX = e.getX();
		mouseY = e.getY();
	}
}