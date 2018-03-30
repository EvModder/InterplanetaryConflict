package InterplanetaryConflictDesigner;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Rectangle;

public class Thing{
	
	private Image image;
	private String name;
	private boolean visible;
	private int x, y, w, h;
	final int LI;
	
	public Thing(String n, int x, int y, int levelIn){
		name = n; LI = levelIn;
		image = new ImageIcon(getClass().getResource("/images/"+n+".png")).getImage();
		h = image.getHeight(null); w = image.getWidth(null);
		this.x = x; this.y = y;
		visible = true;
	}
	
	public int getH(){return h;}
	public int getW(){return w;}
	public int getX(){return x;}
	public int getY(){return y;}
	public void setrX(int X){x = X;}
	public void setrY(int Y){y = Y;}
	public void setX(int X){x = X-w/2;}
	public void setY(int Y){y = Y-h/2;}
	public String getName(){return name;}
	public int getLevelFrom(){return LI;}
	public Image getImage(){return image;}
	
	public boolean isVisible(){return visible;}
	public void setVisible(Boolean v){visible = v;}
	public Rectangle getBounds() {return new Rectangle(x, y, w, h);}
}