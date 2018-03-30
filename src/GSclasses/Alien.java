package GSclasses;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

public class Alien{
	
	protected int x, y;
	protected int width, health;
	protected boolean visible;
	protected int height;
	protected int speed;

    public Alien(int x, int y, int health, int s) {
    	ImageIcon ii = new ImageIcon(getClass().getResource("/images/alien.png"));
        width = ii.getImage().getWidth(null);
        height = ii.getImage().getHeight(null);
        this.x = x; this.y = y;
        this.health = health;
        visible = true;
        speed=s;
    }
    
    public void move(int W, int H, int left){
    	x-=speed;
    	if(x<-20)x = W+2000;
    	
    	if(left<=15&&x>W)x-=3;
       	if(left<=5 &&x>W+7)x-=8;
    }
    
    public int getX() {return x;}
    public int getY() {return y;}
    public int getHealth() {return health;}
    public void setHealth(int H) {health=H;}
    public void setVisible(Boolean visible) {this.visible = visible;}
    public Rectangle getBounds() {return new Rectangle(x, y, width, height);}
}