package GSclasses;
import javax.swing.ImageIcon;
import java.awt.Rectangle;

public class Missile {

	boolean visible;
    private int speed;
    protected int x, y;
    protected int width, height;
    
    public Missile(int x, int y, int speed, String name) {
    	ImageIcon ii = new ImageIcon(getClass().getResource("/images/"+name+".png"));
       	
        width = ii.getImage().getWidth(null);
        height = ii.getImage().getHeight(null);
        this.x = x; this.y = y-height/2;
        this.speed=speed;
       	visible=true;
    }
    public int getX(){return x;}
    public int getY(){return y;}
    public void move(){x += speed;}
    public boolean isVisible() {return visible;}
    public void setVisible(Boolean visible) {this.visible = visible;}
    public Rectangle getBounds() {return new Rectangle(x, y, width, height);}

}