package GSclasses;
import java.net.URL;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

public class Bonus{
	
	private int x, y, s, type;
	private boolean visible;
	private int width, height;
	
	public Bonus(int x, int y, int s, int type){this.type=type;
    	URL url = getClass().getResource("/images/Bonus"+type+".png");
		
		visible = true;
		this.x=x;this.y=y;this.s=s;
		width = new ImageIcon(url).getImage().getWidth(null);
		height = new ImageIcon(url).getImage().getHeight(null);
	}
	
	public void move(int width) {x -= s; if (x < -20){x=width+1700;}}
	
	public int getX(){return x;}
	public int getY(){return y;}
	public int getNumber() {return type;}
	public static int getBonuses(){return 14;}//returns the number of available bonuses
	public boolean isVisible() {return visible;}
	public void setVisible(Boolean visible) {this.visible = visible;}
	public Rectangle getBounds() {return new Rectangle(x, y, width, height);}
	public Rectangle getSmallBounds() {return new Rectangle(x-3, y-3, width-3, height-3);}
}