package playerClasses;
import java.awt.Image;

public class Thing{
	private Image image;
	private int x, y;
	
	public Thing(Image i, int x, int y){image = i; this.x = x; this.y = y;}
	
	public int getX(){return x;}
	public int getY(){return y;}
	public Image getImage(){return image;}
}