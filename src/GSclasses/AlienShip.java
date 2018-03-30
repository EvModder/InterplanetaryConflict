package GSclasses;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;

public class AlienShip extends Alien{
    private ArrayList<AlienShip> ships;
    private ArrayList<Alien> aliens;
    private boolean mothership;
    private int timesM, steadyS;
    protected int num;

    public AlienShip(int x, int y, int h, int s){
    	super(x, y, h, s);
    	num = new Random().nextInt(10)+1;
    	if(num<8)num = 1;
    	else if(num < 10){num = 2; h+=h/2;}
    	else{num = 3; h*=3; mothership = true;
    		ships = new ArrayList<AlienShip>();
    	}
    	
    	ImageIcon ii = new ImageIcon(getClass().getResource("/images/AlienShip"+num+".png"));
	    
    	width = ii.getImage().getWidth(null);
	    height = ii.getImage().getHeight(null);
       	
        aliens = new ArrayList<Alien>();
        speed = s;
        steadyS = s;
    }
    
    public AlienShip(int x, int y, int h, int s, int num){
    	super(x, y, h, s);
    	
    	this.num = num;
    	ImageIcon ii = new ImageIcon(getClass().getResource("/images/AlienShip"+num+".png"));
    	width = ii.getImage().getWidth(null);
	    height = ii.getImage().getHeight(null);
       	
    	if(num == 3){h*=3; mothership = true;
			ships = new ArrayList<AlienShip>();
		}else aliens = new ArrayList<Alien>();
        speed = s;
        steadyS = s;
    }
    
    public void move(ArrayList<CraftInfo> craft, int GS_W, int GS_H, int alienH, int alienS, int left){
	    timesM++;
	    
	    int sMidpt= (int) y+(height/2);
	    int closest = 0; int cX = 0;
	    
	    for(int i = 0; i < craft.size(); i++){
	    	CraftInfo c = craft.get(i);
		    int cMidpt= (int) c.getY() + c.getH()/2;
		    if(sMidpt-cMidpt < sMidpt-closest){
		    	closest = cMidpt;
		    	cX = c.getX();
		    }
	    }
	   
    	if	   (sMidpt>closest && sMidpt>closest){y-=speed;}
    	else if(sMidpt<closest && sMidpt<closest){y+=speed;}
    	
    	if(sMidpt-15<closest && sMidpt+15>closest){
    		x--; if(sMidpt == closest && speed == steadyS-1)x--;
    		
    		if(timesM%90==0 && x < GS_W+100 && x > cX){
    			if(mothership)ships.add(new AlienShip(x-39, y + height/2-25, alienH, steadyS+1, 1));
    			else aliens.add(new Alien(x-5, y + height/2-7, alienH, alienS+1));
    		}
    		
    	}
	    x-=speed;
    	
        if(x+width < 0)x=GS_W+3000;
        
        if(y<1)y=1;
        else if(y+height>GS_H-130){y=GS_H-130-height;}
        
        if(timesM % 2 == 0)speed = steadyS;else speed = steadyS-1;
        
        if(left<=15&&x>GS_W+2)x-=3;
        if(left<=5 &&x>GS_W+7)x-=8;
    }

    public boolean isMother(){return mothership;}
    public void setLoc(int X, int Y){x = X; y = Y;}
    public ArrayList<Alien> getAliensShot(){return aliens;}
    public ArrayList<AlienShip> getShipsShot(){return ships;}
    public Rectangle getBounds(){return new Rectangle(x, y+4, width, height-8);}
    public void clearAlienList(){if(mothership)ships.clear(); else aliens.clear();}
}