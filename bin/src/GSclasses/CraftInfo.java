package GSclasses;
import java.net.URL;
import java.util.ArrayList;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

public class CraftInfo {
	private String message, typeM;
	private ArrayList<Missile> ms;
	private boolean upAndRunning, power, crosshairsOn, shot;
	private int x, y, smallX, smallY, chx, chy;
	protected int shieldedTime, sNum;
	private int h, w, chH, chW;
	private String n;
	Image image;
	
	public CraftInfo(String urlname){
		upAndRunning=true;
		n = urlname;
		Image i;
		
		if(!urlname.startsWith("C:")){
			URL url = getClass().getResource("/images/Craft-"+urlname+".png");
			if(url == null){url = getClass().getResource("/images/Craft-Better.png");urlname = "Better";}
			i = new ImageIcon(url).getImage();
		}else{
			try{i = new ImageIcon(urlname).getImage();}
			catch(NullPointerException e){i = new ImageIcon("/images/Craft-Pizza.png").getImage();}
		}
		
		h = i.getHeight(null);
		w = i.getWidth(null);
		
		if(urlname.equals("Escape Shuttle")){
			h = i.getHeight(null)-6;
			w = i.getWidth(null)-10;
			smallX = 5; smallY = 3;
		}
		
		// crosshairs
		if(urlname.equals("Pizza")){chH = 75; chW = 80;}
		
		else {
			i = new ImageIcon(getClass().getResource("/images/otherCrosshairs.png")).getImage();
			chH = i.getHeight(null);
			chW = i.getWidth(null);
		}
			
		message = "";
	}
	
	public CraftInfo(Image i){
		upAndRunning=true;
		n = "Custom";
		image = i;
		
		h = i.getHeight(null);
		w = i.getWidth(null);
			
		message = "";
	}
	
	public int getH(){return h;}
	public int getW(){return w;}
	public int getX(){return x;}
	public int getY(){return y;}
	public void setX(int nx){x=nx;}
	public void setY(int ny){y=ny;}
	public int getCHX(){return chx;}
	public int getCHY(){return chy;}
	public void setCHX(int nx){chx=nx;}
	public void setCHY(int ny){chy=ny;}
	
	public String getName(){return n;}
	
	public String getType(){return typeM;}
	public void setType(String t){typeM=t;}
	
	public String getMessage(){return message;}
	public void addMessage(String m){message+=m;}
	public void clearMessage(){message="";}
	
	public boolean isPower(){return power;}
	public void setPower(boolean on){power=on;}
	
	public boolean justShot(){return shot;}
	public void setShot(boolean s){shot=s;}
	
	public boolean hasCrosshairs(){return crosshairsOn;}
	public void setCrosshairs(boolean on){crosshairsOn=on;}
	
	public boolean isOperational(){return upAndRunning;}
	public void setOperational(boolean on){upAndRunning=on;}
	
	public void add(Missile m){ms.add(m);}
	public void clearMissiles(){if(ms != null)ms.clear();}
	public ArrayList<Missile> getMissiles(){return ms;}
	public void setMissiles(ArrayList<Missile> ms){this.ms = ms;}
	
    public Rectangle getBounds(){return new Rectangle(x+smallX, y+smallY, w, h);}
    public Rectangle getCrosshairBounds(){return new Rectangle(chx, chy, chH, chW);}
}