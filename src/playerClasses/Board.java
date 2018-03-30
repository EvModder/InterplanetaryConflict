package playerClasses;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.awt.AWTException;
import java.awt.Font;
import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Graphics;
import javax.imageio.ImageIO;
import javax.swing.Timer;
import javax.swing.JPanel;
import java.util.ArrayList;
import java.awt.Graphics2D;
import java.awt.FontMetrics;
import javax.swing.ImageIcon;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Board extends JPanel implements ActionListener{
	private static final long serialVersionUID = 1L;
	
	private int B_WIDTH, B_HEIGHT, BN, timeUp, timeToPower;
	protected SoundPlayer soundPlayer;
	protected boolean playMps, playWvs;
	private ArrayList<Thing> things;
	protected CrossHairs crosshairs;
	protected String info, song;
	private Image background;
	private Font plain, bold;
	protected Player frame;
	PlayerCraft thisCraft;
	private Timer timer;
	private Color rc;
	double zoom;
	
	public Board(Player p, int i, int panelH){
		frame = p; zoom = 1;
		BN=frame.rand.nextInt(13)+1;
		
		if(i== 1)thisCraft = new PlayerCraft(this, 50, 60, "Original");
		else if(i== 2)thisCraft = new PlayerCraft(this, 50, 60, "Bird_Large");
		else if(i== 3)thisCraft = new Craft_Blastin_Robin(this, 50, 60);
		else if(i== 4)thisCraft = new Craft_Flower_Power(this, 50, 60);
		else if(i== 5)thisCraft = new Craft_Defector(this, 50, 60);
		else if(i== 6)thisCraft = new PlayerCraft(this, 50, 60, "Pinker");
		else if(i== 7)thisCraft = new Craft_Shadow(this, 0, 70);
		else if(i== 8)thisCraft = new Craft_Saber(this, 50, 60);
		else if(i== 9)thisCraft = new Craft_V_Plane(this, 50, 60);
		else if(i== 10)thisCraft = new Craft_Bingi(this, 50, 60);
		else if(i==11)thisCraft = new Craft_Pizza(this, 50, 60);
		else if(i==12)thisCraft = new Craft_Shuttle(this, 50, 60);
		else thisCraft = new PlayerCraft(this, 50, 60, p.craft[i]);
		
		addKeyListener(new MyKeyListener());
		soundPlayer = new SoundPlayer();
		setFocusable(true);
		setBackground(Color.BLACK);
		setDoubleBuffered(true);
		setSize(frame.getW(), frame.getH()-panelH);
    	URL url = getClass().getResource("/images/Background"+BN+".png");
    	background = new ImageIcon(url).getImage();
		zoom=1;
		rc = tidbits.getRandomColor();
		
		plain= new Font("Times New Roman", Font.PLAIN, 14);
		bold = new Font("Times New Roman", Font.BOLD , 14);
		
		soundPlayer = new SoundPlayer();
		song = "file:sound/we_will_rock_you.mp3";
		addNotify();
		timer = new Timer(15, this); timer.start();
	}
	
	public void restart(boolean won){
		thisCraft.turnOffPower();
		thisCraft.setVisible(true);
		rc = tidbits.getRandomColor();
		if(won){
			BN+=frame.rand.nextInt(3)+1;
			if(BN > 13)BN-=13;//13 is the num of backgrounds
		    URL url = getClass().getResource("/images/Background"+BN+".png");
		    background = new ImageIcon(url).getImage();
		}
		thisCraft.resetCraft();
		
		soundPlayer = new SoundPlayer();
		song = "file:sound/we_will_rock_you.mp3";
		soundPlayer.playSound(this, song);
	}
		
	public void addNotify(){
		super.addNotify();
		B_WIDTH = getWidth();
		B_HEIGHT = getHeight();
	}
	
	public void paint(Graphics g){
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(background, (B_WIDTH/2-background.getWidth(null)/2)-3, (B_HEIGHT/2-background.getHeight(null)/2), this);
		
		if(frame.isPaused() == false){
			if(timeToPower>0){timeToPower--;
				if(timeToPower==0)thisCraft.turnOffPower();
			}else if(thisCraft.isPower())timeToPower=400;
		}
	
		if(frame.isIngame()){
			if(frame.isPaused() == false)thisCraft.move();
			frame.sendStats();
			ArrayList<Thing> t;
			if((t = things) != null){
				for(int i = 0; i < t.size(); i++){
					Thing o = t.get(i);
					g2d.drawImage(o.getImage(), o.getX(), o.getY(), this);
				}
			}
			
			if(crosshairs != null && crosshairs.isVisible()) g2d.drawImage(crosshairs.getImage(), crosshairs.getX(), crosshairs.getY(), this);

			g.setFont(plain);
			g2d.setColor(Color.WHITE);
			
			String info = "Level "+frame.getLevel()+":     Aliens left: "+frame.alienNum+"   shots Left: "+thisCraft.getShots()
					+"   Bombs Left: "+(int)thisCraft.getBombs()+"   Nukes left: "+(int)thisCraft.getNukes()+"   " +
					"Invincibility sequences: "+(int)thisCraft.getSuperShots()+"   Lives: ";
			g2d.drawString(info, 5, 15);
		
			g2d.setFont(bold);
			int h = thisCraft.getHealth();
			if(h>4)g2d.setColor(Color.GREEN);
			else if(h>2)g2d.setColor(Color.YELLOW);
			else if(h>1)g2d.setColor(Color.ORANGE);
			else g2d.setColor(Color.RED);
			g2d.drawString(h+"", this.getFontMetrics(plain).stringWidth(info)+ 5, 15);
		}
		else{
			if (frame.alienNum==0){info="\n Level "+(frame.getLevel()-1)+" complete!";}
			else{info = "\n You lost. "+frame.alienNum+ " aliens survived";}
			
			Font small = new Font("Helvetica", Font.BOLD, 14);
			FontMetrics metr = this.getFontMetrics(small);
	
			g.setColor(rc); g.setFont(small);
			g.drawString(info, (B_WIDTH - metr.stringWidth(info)) / 2, B_HEIGHT / 2);
		}
		Toolkit.getDefaultToolkit().sync();
		g.dispose();
	}

	private class MyKeyListener implements KeyListener{
		
		@Override public void keyReleased(KeyEvent ev){thisCraft.keyReleased(ev);}
		
		@Override public void keyPressed(KeyEvent e){
			int key = e.getKeyCode();
	        thisCraft.code+=tidbits.getKey(key);
	        
			if(key == KeyEvent.VK_C)crossHairShooter();
			else if(key == KeyEvent.VK_MINUS && zoom>1)zoom-=.1;
			else if(key == KeyEvent.VK_EQUALS && zoom<4)zoom+=.1;
		//	else if(key == KeyEvent.VK_PRINTSCREEN)saveScreenshot();
			else thisCraft.keyPressed(e, frame.isPaused());
		}
		
		@Override public void keyTyped(KeyEvent ev){/*do nothing*/}
	}
	
	private void crossHairShooter(){
		if(crosshairs == null){
			if(thisCraft.name.equals("Pizza"))crosshairs = new CrossHairs(this, "mushroom");
				else crosshairs = new CrossHairs(this, "norm");
		}
		else /*crossHairs.setVisible(!crossHairs.isVisible());*/crosshairs = null;
	}
	
	public void saveScreenshot(){
		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		BufferedImage capture = null;
		
		try{capture = new Robot().createScreenCapture(screenRect);
		}catch(AWTException e){e.printStackTrace();}
		
		//C:\Users\Nate\Pictures\Useful Items\Other\explosion.png
		
		try{ImageIO.write(capture, "bmp", new File("C:\"Users\"Nate\"Pictures"));
		}catch(IOException e){e.printStackTrace();}
	}
	
	@Override protected Graphics getComponentGraphics(Graphics g){
		Graphics2D g2d = (Graphics2D) g;

		AffineTransform trOld = g2d.getTransform();
		AffineTransform tr2 = new AffineTransform(trOld);
		
		double x = getWidth() * (1 - zoom ) / 2;
		double y = getHeight() * (1 - zoom) / 2;
		tr2.translate(x,y);
		tr2.scale(zoom, zoom);

		g2d.setTransform(tr2);
		return super.getComponentGraphics(g2d);
	}
	
	@Override public void actionPerformed(ActionEvent arg0){
		frame.sendStats();
		if(crosshairs != null)crosshairs.justShot = false;
		timeUp++; if(timeUp==1800){timeUp=0; frame.takeLine();}
		repaint();
	}
	
	public void hurtCraft(){
		thisCraft.takeHealth(1);
		if(thisCraft.getHealth()<=0){thisCraft.setVisible(false); crosshairs = null;}
	}
	
	public void kill(){
		timer.stop();
		timer = null;
	}
	
	public int getW(){return B_WIDTH;}
	public int getH(){return B_HEIGHT;}
	public void drawThings(ArrayList<Thing> things){this.things = things; repaint();}
	public boolean hasCrosshairs(){if(crosshairs != null && crosshairs.isVisible())return true; else return false;}
}