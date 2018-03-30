package playerClasses;
import java.awt.Image;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import java.net.URL;

public class PlayerCraft{
	protected Board parent;
	protected final int B_HEIGHT;
	protected int misNum, speed, oSpeed, oHealth;
	protected int dx, dy, x, y, health, width, height;
	protected int shotsLeft, oShots;
	protected double nukesLeft, bombsLeft, superShotsLeft,
    oNukes, oBombs, oSuperShots;
    
    private boolean powerAmo, visible;
	protected boolean roundUniverse;
	protected String typeMs, MsSound;
    protected String name, code;
	
	public PlayerCraft(Board parent, int x, int y, String name){
        this.x = x; this.y = y; health = 3;
    	superShotsLeft = 1; nukesLeft = 2; bombsLeft = 3; shotsLeft = 200;
    	speed=2;roundUniverse=false;powerAmo=false; code="";
    	misNum = 8; this.parent = parent;
    	B_HEIGHT=parent.getH();
    	
     	typeMs="missile";code="";
     	MsSound = "photon-torpedo.wav";
    	this.name=name;
    	
    	Image i;
    	if(name.startsWith("C:"))i = new ImageIcon(name).getImage();

    	else{
	    	URL url = getClass().getResource("/images/Craft-"+name+".png");
	    	if(url == null)url = getClass().getResource("/images/Craft-Original.png");
	    	i = new ImageIcon(url).getImage();
    	}
       	width = i.getWidth (null);
    	height= i.getHeight(null);
    	
        visible = true;
    	oSpeed=speed;
    	oHealth=health;
    	oShots=shotsLeft;
    	oBombs=bombsLeft;
    	oNukes=nukesLeft;
    	oSuperShots=superShotsLeft;
    }
    
	public void move(){
        x += dx; y += dy;
        if(roundUniverse){
	        if(x<-width/2)x=parent.getW()-10-width/2;
	        else if(x > parent.getW()-width/2-10){x=-width/2;}
	        if(y<-height/2){y=parent.getH()-60-height/2;}
	        else if(y > parent.getH()-height/2-60){y=-height/2;}//Minus ship_height and buttonPanel height
        }
        else{
	        if(x<1){x=1;}
	        else if(x > parent.getW()-width-10){x=parent.getW()-width-10;}
	        if(y<1){y=1;}
	        else if	(y > parent.getH()-height-60){y=parent.getH()-height-60;}//Minus ship_height and buttonPanel height
        }
    }
    
    public void resetCraft(){
    	visible = true;
    	if(health>0){
	    	oSpeed=speed;
	    	oHealth=health;
	    	oShots=shotsLeft;
	    	oBombs=bombsLeft;
	    	oNukes=nukesLeft;
	    	oSuperShots=superShotsLeft;
    	}else{speed=oSpeed;
        	health=oHealth;
        	shotsLeft=oShots;
        	bombsLeft=oBombs;
        	nukesLeft=oNukes;
        	superShotsLeft=oSuperShots;
        	roundUniverse = false;
    	}
    }
   
    public void checkCode(){
    	if(code.equalsIgnoreCase("Victory"))nukesLeft+=100;
    	else if(code.equalsIgnoreCase("suicide")){width+=500; height+=500;}
    	else if(code.equalsIgnoreCase("Bomb 'em"))bombsLeft+=100;
    	else if(code.equalsIgnoreCase("shoot 'em"))shotsLeft+=500;
    	else if(code.equalsIgnoreCase("cheater"))speed = shotsLeft = (int) (bombsLeft = nukesLeft = superShotsLeft = 0);
    	else if(code.equalsIgnoreCase("faster!"))speed+=3;
    	code="";
    }
    
    public void keyPressed(KeyEvent e, boolean paused) {
    	if(visible==true && paused == false){
	    	int key = e.getKeyCode();
	        
	        if (key == KeyEvent.VK_LEFT && !name.equals("Shadow"))dx =-speed;
	        if (key == KeyEvent.VK_RIGHT&& !name.equals("Shadow"))dx = speed;
	        if (key == KeyEvent.VK_UP  )dy =-speed;
	        if (key == KeyEvent.VK_DOWN)dy = speed;
	        
//	        else if(key == KeyEvent.VK_L && parent.crosshairs.isVisible()){
//	        	if(shotsLeft>0){
//		    		if(shotsLeft>25){parent.crosshairs.addLoaded(25); shotsLeft-=25;}
//		    		else{parent.crosshairs.addLoaded(shotsLeft); shotsLeft=0;}
//		    		parent.soundPlayer.playSound(parent, "loadgun.wav");
//	        	}
//	        }
	        if (key == KeyEvent.VK_SPACE && shotsLeft>0){
	        	parent.soundPlayer.playSound(parent, MsSound); fire();}
	        
	        else if (key == KeyEvent.VK_B){if(bombsLeft>0){
	        	parent.soundPlayer.playSound(parent, MsSound); bomb();if(misNum>8)misNum--;}}
	        
	        else if(key == KeyEvent.VK_N && nukesLeft>0){
	        	parent.soundPlayer.playSound(parent, MsSound); nuke();}
	        
	        else if (key == KeyEvent.VK_ENTER && powerAmo == false && superShotsLeft>0){
	        	powerAmo = true; superShotsLeft--;
	        	parent.soundPlayer.playSound(parent, "metal_vibrating.wav");
        	}
	        
	        else if (key == KeyEvent.VK_SHIFT)checkCode();
	    }
    }
    
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode(); 
		//Crazy sliding version
		//        if (key == KeyEvent.VK_LEFT	)dx = -1;
		//        if (key == KeyEvent.VK_RIGHT)dx = 1;
		//        if (key == KeyEvent.VK_UP	)dy = -1;
		//        if (key == KeyEvent.VK_DOWN	)dy = 1;
        
      if(key == KeyEvent.VK_LEFT|| key == KeyEvent.VK_RIGHT)dx = 0;
      if(key == KeyEvent.VK_UP	 || key == KeyEvent.VK_DOWN )dy = 0;
    }
    
    protected void fire(){shotsLeft--;
    	parent.soundPlayer.playSound(parent, MsSound);
	    if(name.equals("Better"))parent.frame.addMessage("new missile/"+(x + width)+"/"+(y + height/2+4)+"/"+ (speed+1)+"\n");
	    else parent.frame.addMessage("new missile/"+(x + width)+"/"+(y + height/2)+"/"+ (speed+1)+"\n");
    }
    
    protected void bomb(){String b = "";
    	bombsLeft--;
    	parent.soundPlayer.playSound(parent, MsSound);
    	for(int m=height/2-misNum*10-10;m<height+misNum*10-10;m+=20)b += "new missile/"+(x+width)+"/"+(y+m)+"/"+(speed+1)+":";
    	parent.frame.addMessage(b);
    }
   
    protected void nuke(){String n="";
    	nukesLeft--;
    	for(int m = 13; m < parent.getH()-60; m+=20)n+="new missile/"+(x+width)+"/"+m+"/"+(speed+1)+":";
    	parent.frame.addMessage(n);
    }
    
    public int getX(){return x;}
    public int getY(){return y;}
    public void setX(int X){x=X;}
    public void setY(int Y){y=Y;}
    public int getSpeed(){return speed;}
    public int getHealth(){return health;}
    
    public int getShots(){return shotsLeft;}
    public boolean isPower(){return powerAmo;}
    public double getBombs(){return bombsLeft;}
    public double getNukes(){return nukesLeft;}
    public boolean isVisible(){return visible;}
    public double getSuperShots(){return superShotsLeft;}
    public void setVisible(boolean visible){this.visible = visible;}
	public String getMsType(){if(powerAmo && typeMs.equals("missile"))return "Missile-Super"; else return typeMs;}
    
    public void setSpeed(int S){speed=S;}
    public void takeHealth(int n){health-=n;}
    public void turnOffPower(){powerAmo=false;}
    public void addMisNum(int num){misNum+=num;}
    public void addShots(int num){shotsLeft+=num;}
    public void addBombs(int num){bombsLeft+=num;}
    public void addNukes(int num){nukesLeft+=num;}
    public void addSuperShots(int num){superShotsLeft+=num;}
    public void switchRound(){if(roundUniverse)roundUniverse=false;else roundUniverse=true;}
}