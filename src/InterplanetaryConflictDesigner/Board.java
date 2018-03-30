package InterplanetaryConflictDesigner;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.BorderLayout;
import javax.swing.ImageIcon;
import java.awt.Graphics2D;
import javax.swing.JButton;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.Image;
import java.awt.Color;

public class Board extends JPanel{
	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("unused")
	private int B_WIDTH, B_HEIGHT, selected, mx, my, eW = 24, eH = 23, diffX;//, diffY;
	private boolean newItem, overExit, overNext, overPrev;
	private Image exitButton, nextButton, prevButton;
	
	protected String sAction;
	
	private ArrayList<Thing> things;
	private ArrayList<Thing> sThings;
	
	private Rectangle selectedR;
	private ICDesigner frame;
	private JFrame warning;
	double zoom;
	
	public Board(ICDesigner d){
		sAction = "copy";
		things = new ArrayList<Thing>();
		frame = d; zoom = 1;
		selected = -1;
		diffX = 0;
		
		exitButton = new ImageIcon(getClass().getResource("/images/exitbutton1.png")).getImage();
		nextButton = new ImageIcon(getClass().getResource("/images/nextbutton.png")).getImage();
		prevButton = new ImageIcon(getClass().getResource("/images/prevbutton.png")).getImage();
		
		addMouseListener(new MyMouseListener());
		addMouseMotionListener(new MyMouseMotionListener());
		
		setFocusable(true);
		setBackground(Color.BLACK);
		setDoubleBuffered(true);
		setSize(frame.getWidth(), frame.getHeight()-36);
		addNotify();
		zoom=1;
	}
	
	public void addNotify(){
		super.addNotify();
		B_WIDTH = getWidth();
		B_HEIGHT = getHeight();
	}
	
	public void paint(Graphics g){
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		
		for(int i = 0; i < things.size(); i++){
			Thing t = things.get(i);
			if(t.isVisible())g2d.drawImage(t.getImage(), t.getX()-diffX, t.getY(), this);
		}
		
		if(sThings != null)for(int i = 0; i < sThings.size(); i++){
			Thing t = sThings.get(i);
			if(t.isVisible())g2d.drawImage(t.getImage(), t.getX()-diffX, t.getY(), this);
		}
		
		g2d.drawImage(exitButton, 1325, 6, this);
		g2d.drawImage(nextButton, 1305, 293, this);
		g2d.drawImage(prevButton, 10, 293, this);
		if(selectedR != null){
			g2d.setColor(Color.white);
			g2d.drawLine(selectedR.x, selectedR.y, selectedR.x+selectedR.width, selectedR.y);
			g2d.drawLine(selectedR.x, selectedR.y, selectedR.x, selectedR.y+selectedR.height);
			g2d.drawLine(selectedR.x+selectedR.width, selectedR.y, selectedR.x+selectedR.width, selectedR.y+selectedR.height);
			g2d.drawLine(selectedR.x, selectedR.y+selectedR.height, selectedR.x+selectedR.width, selectedR.y+selectedR.height);
		}
		
		Toolkit.getDefaultToolkit().sync();
		g2d.dispose();
		addNotify();
	}
	
	private class MyMouseListener implements MouseListener{
		
		@Override public void mouseClicked(MouseEvent e){}
	
		@Override public void mousePressed(MouseEvent e){
			if(selected == -1){
				if(overExit)warn();
				
				else if((mx > 10 &&  mx < 50) && (my > 293 && my < 357)){
					diffX-=6; if(diffX<0)diffX = 0;
					overPrev = true;
				}
				else if((mx > 1305 &&  mx < 1345) && (my > 293 && my < 357)){
					diffX+=6; if(diffX>3000)diffX = 3000;
					overNext = true;
				}
				else{
					for(int i = 0; i < things.size(); i++){
						Thing t = things.get(i);
						if(((t.getX() < mx+diffX && t.getX()+t.getW() > mx+diffX) && (t.getY() < my && t.getY()+t.getH() > my) && t.isVisible())){
							selected = i;
						}
					}
				}
				
				if(selected == -1)selectedR = new Rectangle(mx, my, 0, 0);
			}
			else selected = -1;
			newItem = false;
			
			if(e.getButton() == MouseEvent.BUTTON3 && selected != -1){things.remove(selected); repaint();}
			
			if(sThings != null){
				for(Thing t : sThings)if(!things.contains(t))things.add(t);
				sThings.clear();
				sThings = null;
				getThingsIn();
			}
		}

		@Override public void mouseReleased(MouseEvent e){
			if(newItem == false)selected = -1;
			overPrev = overNext = false;
			
			if(selectedR != null){//=================================================================================
				if(selectedR.width <0){selectedR.width *=-1; selectedR.x = selectedR.x-selectedR.width ;}
				if(selectedR.height<0){selectedR.height*=-1; selectedR.y = selectedR.y-selectedR.height;}
				
				if(sAction.equals("align")){align(); repaint();}
				
				
				else if(sAction.equals("copy")){
					newItem = true;
					sThings = new ArrayList<Thing>();
					for(int i = 0; i < things.size(); i++){
						Thing t = things.get(i);
						Rectangle thingR = new Rectangle(t.getX()-diffX, t.getY(), t.getW(), t.getH());
						
						if(selectedR.contains(thingR) && t.getLevelFrom() == frame.getLevelOn()){
							sThings.add(new Thing(t.getName(), t.getX()-2, t.getY()-2, t.getLevelFrom()));
						}
					}
					for(int i = 0; i < sThings.size(); i++){//this for loop centers the selected things on the mouse
						Thing t = sThings.get(i);
						
						if(t.getX()<selectedR.x+selectedR.width+diffX)t.setrX(t.getX()-selectedR.width/2);
						else t.setrX(t.getX()+selectedR.width/2);
						
						if(t.getY()<selectedR.y+selectedR.height)t.setrY(t.getY()+selectedR.height/2);
						else t.setrY(t.getY()-selectedR.height/2);
					}//=============================================================================================
				}
				else if(sAction.equals("move")){
					
					newItem = true;
					if(sThings != null)sThings.clear();
					sThings = new ArrayList<Thing>();
					for(int i = 0; i < things.size(); i++){
						Thing t = things.get(i);
						
						if(selectedR.contains(new Rectangle(t.getX()-diffX, t.getY(), t.getW(), t.getH()))
								&& t.getLevelFrom() == frame.getLevelOn()){t.setrX(t.getX()-2); t.setrY(t.getY()-2);
								sThings.add(t);/*things.remove(i);*/}
					}
					for(int i = 0; i < sThings.size(); i++){//this for loop centers the selected things on the mouse
						Thing t = sThings.get(i);
						
						if(t.getX()<selectedR.x+selectedR.width+diffX)t.setrX(t.getX()-selectedR.width/2);
						else t.setrX(t.getX()+selectedR.width/2);
						
						if(t.getY()<selectedR.y+selectedR.height)t.setrY(t.getY()+selectedR.height/2);
						else t.setrY(t.getY()-selectedR.height/2);
					}//=============================================================================================
				}
				//===================================================================================================
				else if(sAction.equals("delete")){
					
					boolean bad = true;
					while(bad){bad = false;
						for(int i = 0; i < things.size(); i++){
							Thing t = things.get(i);
							if(selectedR.intersects(new Rectangle(t.getX()-diffX, t.getY(), t.getW(), t.getH()))
									&& t.getLevelFrom() == frame.getLevelOn()){things.remove(i); bad=true;}
						}
					}
					repaint();
				}
				selectedR = null;
			}//======================================================================================================
		}
		
		@Override public void mouseEntered(MouseEvent e){/*do nothing*/}

		@Override public void mouseExited(MouseEvent e){/*do nothing*/}
	}
	
	private class MyMouseMotionListener implements MouseMotionListener{

		@Override public void mouseDragged(MouseEvent e){
			if(selected!=-1){
				mx = e.getX(); my = e.getY();//<<<<<<<<<<<<<<<<
				
				Thing t = things.get(selected);
				if(my<30+t.getH()/2)my = 30+t.getH()/2;
				else if(my>593-t.getH()/2)my = 593-t.getH()/2;
				
				t.setX(mx+diffX);
				t.setY(my);
				repaint();
			}else if(selectedR != null){
				mx = e.getX(); my = e.getY();//<<<<<<<<<<<<<<<<
				
				selectedR.width = mx - selectedR.x;
				selectedR.height= my - selectedR.y;
				repaint();
			}
			else if(sThings != null){
				int dX = e.getX() - mx;
				int dY = e.getY() - my;
				mx = e.getX();
				my = e.getY();
				
				boolean inRange = true;
				for(Thing t : sThings)if(t.getY()+t.getH()+dY > 593 || t.getY()+dY < 30)inRange = false;
				
				if(inRange)for(Thing t : sThings){
					t.setrX(t.getX()+dX);//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
					t.setrY(t.getY()+dY);
				}
				repaint();
			}
		}

		@Override public void mouseMoved(MouseEvent e){
			if(newItem)mouseDragged(e);
			else mx = e.getX(); my = e.getY();
			
			if((1325 < mx && 1325+eW > mx) && (6 < my && 6+eH > my) && overExit == false){
				overExit = true;
				exitButton = new ImageIcon(getClass().getResource("/images/exitbutton2.png")).getImage();
				repaint();
			}else if((1325 > mx || 1325+eW < mx || 6 > my || 6+eH < my) && overExit == true){
				overExit = false;
				exitButton = new ImageIcon(getClass().getResource("/images/exitbutton1.png")).getImage();
				repaint();
			}
		}
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
	
	public void warn(){
		warning = new JFrame("What? Quitting?");
		JLabel label = new JLabel("Are you sure you want to exit?");
		warning.setIconImage(new ImageIcon(getClass().getResource("/images/AlienShip1.png")).getImage());
		JButton ok = new JButton("Yes");
		JButton no = new JButton("No!");
		ok.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent arg0){
				frame.dispose();
				warning.dispose();
				System.exit(0);
			}
		});
		no.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent arg0){
				warning.dispose();
			}
		});
		JPanel panel = new JPanel();
		panel.setBackground(Color.GREEN);
		panel.add(ok); panel.add(no);
		JPanel bigPanel = new JPanel();
		bigPanel.setBackground(Color.GREEN);
		bigPanel.add(label); bigPanel.add(panel);
		warning.add(BorderLayout.CENTER, bigPanel);

		warning.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		warning.setSize(300, 100);
		warning.setLocationRelativeTo(null);
		warning.setAlwaysOnTop(true);
		warning.setResizable(false);
		warning.setVisible(true);
	}
	
	public void align(){
		boolean bad;
		int x, y;
		
		do{bad = false;
			for(int I = 0; I < things.size(); I++){
				Thing t = things.get(I);
				
				if((!sAction.equals("align") || selectedR == null || new Rectangle(t.getX()-diffX, t.getY(), t.getW(), t.getH()).intersects(selectedR)) && t.getLevelFrom() == frame.getLevelOn()){
					x = t.getX();
					y = t.getY();
					
					for(int i = 0; i < things.size(); i++){
						Thing t2 = things.get(i);
						
						if(!t2.equals(t) && (!sAction.equals("align") || new Rectangle(t.getX()-diffX, t.getY(), t.getW(), t.getH()).intersects(selectedR))){
							int x2 = t2.getX(), y2 = t2.getY();
							
							if((x-x2 > -400 && x-x2 < 400) && (x2 < x && x2+15 > x) || (x2 > x && x2-15 < x)){
								t2.setrX(x + (t.getW()-t2.getW())/2);
							}
							if((y-y2 > -400 && y-y2 < 400) && (y2 < y && y2+15 > y) || (y2 > y && y2-15 < y)){
								t2.setrY(y + (t.getH()-t2.getH())/2);
							}
							
							if(x == x2 && y == y2){things.remove(i); bad = true;}
						}
					}
				}//if: sAction
			}//for: things
		}while(bad);
		getThingsIn();
		repaint();
	}
	
	public void spaceOut(){
		boolean bad;
		int t = 0;
		do{bad = false; t++;
			for(Thing t1 : things){
				if(t1.getLevelFrom() == frame.getLevelOn()){
					
					int x1 = t1.getX();
					int y1 = t1.getY();
					int w1 = t1.getW();
					int h1 = t1.getH();
					Rectangle r1 = t1.getBounds();
					
					for(int i = 0; i < things.size(); i++){
						Thing t2 = things.get(i);
						
						int x2 = t2.getX(), y2 = t2.getY();
						int w2 = t2.getW(), h2 = t2.getH();
						
						if(r1.intersects(t2.getBounds())){//if their intersecting
							if(x1 < x2 && x1 + w1 > x2){t2.setrX(x1 + w1 + 1); bad = true;}
							if(x1 > x2 && x1 - w2 < x2){t2.setrX(x1 - w2 - 1); bad = true;}
						}
						if(r1.intersects(t2.getBounds())){//if still intersecting
							if(y1 < y2 && y1 + h1 > y2){t2.setrY(y1 + h1 + 1); bad = true;}
							if(y1 > y2 && y1 - h2 < y2){t2.setrY(y1 - h2 - 1); bad = true;}
						}
					}//end for: things
				}//end if: level matches
			}//end for: things
		}while(t < 10 && bad);
		getThingsIn();
	}
	
	public void checkMouse(){
		if(overNext){
			diffX+=4; if(diffX>3000)diffX = 3000;
			repaint();
		}
		else if(overPrev){
			diffX-=4; if(diffX<0)diffX = 0;
			repaint();
		}
//		//check things
//		for(Thing t : things){
//			int y = t.getY(), h = y + t.getH();
//			if(y < 30){
//				if(sThings != null)for(int i = 0; i < sThings.size(); i++){
//					Thing T = sThings.get(i);
//					T.setrY(T.getY() + 30-y);
//				}else t.setrY(30);
//			}
//			else if(y + h > 593){
//				if(sThings != null)for(int i = 0; i < sThings.size(); i++){
//					Thing T = sThings.get(i);
//					T.setrY(T.getY() + 593-y - h);
//				}else t.setrY(30);
//			}
//		}		
	}
	
	public void addThing(String s){
		newItem = true;
		things.add(new Thing(s, mx+diffX, my, frame.getLevelOn()));
		selected = things.size()-1;
	}
	
	public void getThingsIn(){
		for(Thing t : things){
			
			if(t.getY()<30)t.setrY(30);
			else if(t.getY()>593-t.getH())t.setrY(593-t.getH());
		}
	}
	
	public void clear(){things.clear();}
	
	public ArrayList<Thing> getThings(){return things;}
}