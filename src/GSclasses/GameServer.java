package GSclasses;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import java.io.BufferedReader;
import java.net.ServerSocket;
import java.awt.BorderLayout;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Rectangle;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.io.FileReader;
import javax.swing.Timer;
import java.util.Random;
import java.net.Socket;
import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.io.File;

public class GameServer implements ActionListener{
	
	ArrayList<PrintWriter> clientOutputStreams;
	ArrayList<ClientHandler> readers;
	ArrayList<Thread> readerThreads;
	ArrayList<BufferedImage> clientCrafts;
	
	private int levelNum=1, levels=15, alienHealth=1, speed=2, chosen,
	numOfAliens, numOfShips, numOfBonuses, period, pausedC, doneC;
	private int Height=720, Width=1355, cx, cy, timesThrough, started;
	private boolean ifWon, paused, ingame, over, aggressive;
	private final int placementHeight = 580;
	private Random rand = new Random();
	private ArrayList<Point> crashes;
	private InfoBox infobox;
	private String message;
	private Timer timer;
	private File file;
	
	private ArrayList<AlienShip> alienShips;
	private ArrayList<CraftInfo> craft;
	private ArrayList<Bonus> bonuses;
	private ArrayList<Alien> aliens;
	private ServerSocket serSock;
	
	public class ClientHandler implements Runnable{
		BufferedReader reader; Socket sock;
		String incomingMessage; int index;
		
		public ClientHandler(Socket clientSocket, int i){
			try {index=i;
				sock = clientSocket;
				InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
				reader = new BufferedReader(isReader);
				infobox.append("got a connection to "+clientSocket);
			}catch(Exception ex){ex.printStackTrace();}
		}// Close constructor
		
		@SuppressWarnings("deprecation")
		public void run(){
			try {String message;
				while((message = reader.readLine()) != null){
					
					if(message.startsWith("Join Craft-")){
						String name = message.replace("Join Craft-", "");
						
						byte[] bytes = name.getBytes();
						InputStream in = new ByteArrayInputStream(bytes);
						BufferedImage bi = ImageIO.read(in);
						try{
							Image image = bi.getScaledInstance(bi.getWidth(), bi.getHeight(), Image.SCALE_SMOOTH);
							craft.add(new CraftInfo(image));
						}
						catch(NullPointerException ex){
							craft.add(new CraftInfo(name));
						}
						infobox.append("A craft, "+name+", has joined");
					}
					else if(craft.size() > index)scanThis(message, index);
				}
			}catch(Exception ex){
//JOptionPane.showMessageDialog(null,"Those nasty players are always quiting and messing up the game","Arrrgg", JOptionPane.CLOSED_OPTION);
//				clientOutputStreams.remove(index);
//				if(craft.size()>index)craft.remove(index);
				infobox.append("Somebody rudely left the game.");
				ex.printStackTrace();
				
				for(int i = index+1; i < clientOutputStreams.size(); i++)readers.get(i).index--;
				craft.remove(index);
				readers.remove(index);
				clientOutputStreams.remove(index);
				readerThreads.get(index).stop();//.destroy();
				readerThreads.remove(index);
			}
		}// Close run
	}// Close inner class
	
	public void go(String stats){
		aliens = new ArrayList<Alien>();
		bonuses = new ArrayList<Bonus>();
		crashes = new ArrayList<Point>();
		craft = new ArrayList<CraftInfo>();
		alienShips = new ArrayList<AlienShip>();
		clientOutputStreams = new ArrayList<PrintWriter>();
		readers = new ArrayList<ClientHandler>();
		readerThreads = new ArrayList<Thread>();
		
		try{
			serSock = new ServerSocket(9990);
			
			infobox = new InfoBox();
			String[]  info = infobox.getGameInfo().split("/");
			chosen = Integer.parseInt(info[0]);
			
			final int max;
			if(chosen == 0){
				file = infobox.getFile();
				max = loadFile(file, 1);
			}else max = Integer.parseInt(info[1]);
			
			infobox.append(stats);
			
			int i=0;
			while(cy+100<Width && i < max) {
				Socket clientSocket = serSock.accept();
				PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
				clientOutputStreams.add(writer);
				
				Thread t = new Thread(new ClientHandler(clientSocket, i));
				t.start(); i++;
				readerThreads.add(t);
			}// Close while
			
			while(craft.size() < max)Thread.yield();
			//Send the number of levels for this game to all the players
			Iterator<PrintWriter> it = clientOutputStreams.iterator();
			while(it.hasNext()){
				PrintWriter writer = (PrintWriter) it.next();
				
				writer.println((levels)+"/"+infobox.getGameType());writer.flush();
			}
			startGame();
		}catch(Exception ex){
			JFrame ff = new JFrame("Adress Already in Use: JVM_Bind");
			ff.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			JPanel panel = new JPanel();
			panel.setBackground(new Color(255, 8, 20));
			javax.swing.JLabel label = new javax.swing.JLabel("This address is already in use; you will have to wait until it is availiable again");
			panel.add(label);
			ff.add(BorderLayout.CENTER, panel);
			ff.setSize(440, 70);
			ff.setResizable(false);
			ff.setLocationRelativeTo(null);
			ff.setVisible(true);
			ex.printStackTrace();
		}
	}//Close go
	
	public void resetCraftLocations(){
		cx=40; cy=40;
		for(int i = 0; i < craft.size(); i++){
			CraftInfo c = craft.get(i);
			c.addMessage("new location:"+cx+":"+cy+"/");
			c.clearMissiles();
			
			cy+=c.getH()+5;
			if(cy+c.getH()>Height-50){cy=40; cx+=80;}
			
		}sendMessage("");
	}
	
	public void startGame(){
		message="";
		resetCraftLocations();
		if(chosen != 0){
			numOfAliens = 20;
			numOfBonuses=rand.nextInt(numOfAliens/25+2);
			
			timer = new Timer(30, this);
			
			if(chosen == 2)timer = new Timer(10, this);
			else if(chosen == 3)timer = new Timer(40, this);
			else if(chosen == 4)numOfBonuses=rand.nextInt(numOfAliens);
			else if(chosen == 5)numOfAliens*=1.5;
			else if(chosen == 6){numOfShips=rand.nextInt((int) (numOfAliens/1.5));numOfAliens/=1.5;}
			else if(chosen == 7)aggressive = true;
			else if(chosen == 8)numOfAliens = 8;
			else if(chosen == 9){numOfAliens = 45; numOfShips = 5;}
			else if(chosen ==10){numOfAliens = 0;  numOfShips = 4;}
			
			initBonuses(); initAliens();
		}
		for(CraftInfo playerShip : craft){
			if(playerShip.image != null){
				message += "Image:"+playerShip.getName()+":"+playerShip.image;
			}
		}sendMessage("");
		
		ingame = over = true;
		timer.start();
	}

	public void restart(){
		if(levelNum < levels){
			
			resetCraftLocations();
			alienShips.clear(); aliens.clear(); bonuses.clear();
			
			if(chosen != 0){
		
				if(ifWon){levelNum++;
					if(chosen != 10){
						if(chosen == 5)numOfAliens*=1.5;
						
						numOfShips=0;
						for(int i=0;i<numOfAliens;i++){int shipPercent = rand.nextInt(100)+1;if(shipPercent==100)numOfShips++;}
						
						int total = numOfAliens+numOfShips;
						
						if(levelNum<10){numOfAliens+=numOfAliens/2; if(total>300){numOfAliens=299-total; numOfShips++;}}
						else if(levelNum<13)alienHealth++;
						else if(levelNum<16)speed++;
						else if(levelNum<19)numOfAliens+=numOfAliens/3;
						else if(levelNum<22){numOfAliens+=numOfAliens/5;}
						else if(levelNum<levels)speed++;
					}else numOfAliens = 0; numOfShips*=1.5;
					
					if(levelNum>=levels)timer.stop();
				}ifWon=false;
				
				if(chosen == 4 && numOfAliens > 0)numOfBonuses=rand.nextInt(numOfAliens);
				else {int n = (int)numOfAliens/25+2; if(n > 0)numOfBonuses=rand.nextInt(n);}
				if(chosen == 6){numOfShips=rand.nextInt((int) (numOfAliens/1.5));}
				if(chosen == 10)numOfBonuses=rand.nextInt(numOfAliens/2);
				
				initBonuses();initAliens();
				
			}else{
				if(ifWon){levelNum++;
					if(levelNum>=levels)timer.stop();
					ifWon = false;
				}
				
				loadFile(file, levelNum);
			}
			
			message+="restart/unpaused/";
			ingame = true;
			paused=false;
			period=0;
		}//end if that confirms that there are more levels
	}

	public void initBonuses(){
		bonuses = new ArrayList<Bonus>();

		for (int i = 0; i < numOfBonuses; i++){
			int type = rand.nextInt(Bonus.getBonuses())+1;
			if(type == 1 && rand.nextInt(3) == 0)type = 2;
			int point1 = rand.nextInt(Width+3000)+Width-350;
			int point2 = rand.nextInt(placementHeight)+20;
			Bonus b = new Bonus(point1, point2, speed, type);
			
			boolean bad=true;
			while(bad==true){bad=false;
				Rectangle r1 = b.getBounds();
				for(int j = 0; j < bonuses.size(); j++){
					Rectangle r2 = bonuses.get(j).getBounds();
					if(r1.intersects(r2)){
						point1 = rand.nextInt(Width+3000)+Width-350;point2 = rand.nextInt(placementHeight)+20;
						b = new Bonus(point1, point2, speed, type); bad = true; break;
					}
				}
			}bonuses.add(b);
		}
	}
	
	public void initAliens(){
		aliens = new ArrayList<Alien>();
		alienShips = new ArrayList<AlienShip>();
		Random rand = new Random();
		
		for (int i = 0; i < numOfAliens; i++){
			
			int point1 = rand.nextInt(Width+3000)+Width-350;
			int point2 = rand.nextInt(placementHeight)+20;
			Alien a = new Alien(point1, point2, alienHealth, speed);
			
			boolean bad=true;
			while(bad==true){bad=false;
				Rectangle r1 = a.getBounds();
				for(int j = 0; j < aliens.size(); j++){
					Rectangle r2 = aliens.get(j).getBounds();
					if(r1.intersects(r2)){
						point1 = rand.nextInt(Width+3000)+Width-300;point2 = rand.nextInt(placementHeight)+20;
						a = new Alien(point1, point2, alienHealth, speed);
						bad=true; break;
					}
				}for(int j = 0; j < numOfBonuses; j++){
					Rectangle r2 = bonuses.get(j).getBounds();
					if(r1.intersects(r2)){
						point1 = rand.nextInt(Width+3000)+Width-300;point2 = rand.nextInt(placementHeight)+20;
						a = new Alien(point1, point2, alienHealth, speed);
						bad=true; break;
					}
				}
			}aliens.add(a);
		}int shipHealth;if(alienHealth>1){shipHealth=alienHealth*3;}else shipHealth=4;
		
		for(int i = 0; i < numOfShips; i++){
			int point1 = rand.nextInt(Width+3000)+Width-300;
			int point2 = rand.nextInt(placementHeight)+20;
			AlienShip s = new AlienShip(point1, point2, shipHealth, speed);
			
			boolean bad=true;
			while(bad==true){bad=false;
			Rectangle r1 = s.getBounds();
				for(int j = 0; j < aliens.size(); j++){
					Rectangle r2 = aliens.get(j).getBounds();
					if(r1.intersects(r2)){
						point1 = rand.nextInt(Width+3000)+Width-300;point2 = rand.nextInt(placementHeight)+20;
						s.setLoc(point1, point2);
						bad=true;
					}
				}for(int j = 0; j < numOfBonuses; j++){
					Rectangle r2 = bonuses.get(j).getBounds();
					if(r1.intersects(r2)){
						point1 = rand.nextInt(Width+3000)+Width-300;point2 = rand.nextInt(placementHeight)+20;
						s.setLoc(point1, point2);
						bad=true;
					}
				}for(int j = 0; j < alienShips.size(); j++){
					Rectangle r3 = alienShips.get(j).getBounds();
					if(r1.intersects(r3)){
						point1 = rand.nextInt(Width+3000)+Width-300;point2 = rand.nextInt(placementHeight)+20;
						s.setLoc(point1, point2);
						bad=true;
					}
				}
			}alienShips.add(s);
		}
	}

	public void actionPerformed(ActionEvent e){
		if(e.getSource() == timer){
			if(ingame && paused==false){//===============================================================
				if(period==0){
					int aliensLeft = aliens.size() + alienShips.size();
					for (int i = 0; i < aliens.size(); i++)aliens.get(i).move(Width, Height, aliensLeft);
					
					for (int i = 0; i < bonuses.size(); i++)bonuses.get(i).move(Width);
					
					for(int i = 0; i < alienShips.size(); i++){
						alienShips.get(i).move(craft, Width, Height, alienHealth, speed+1, aliensLeft);
					}//end for
					
				}else period--;
				
				//Craft & Missiles do not freeze when the rest of the board does*******
				for(CraftInfo c : craft){
					if(c.getMissiles()!=null){
						ArrayList<Missile> ms = c.getMissiles();
						for(int i = 0; i < ms.size(); i++){
							Missile m = ms.get(i);
							if(m.isVisible()) m.move(); else ms.remove(m);
						}c.setMissiles(ms);
						
						if(c.shieldedTime>0)c.shieldedTime--;
					}//end if
				}//********************************************************************
				
				for(int i = 0; i < alienShips.size(); i++){
					AlienShip ship = alienShips.get(i);
					
					if(ship.isMother()){
						for(AlienShip a : ship.getShipsShot())alienShips.add(a);
					}
					else for(Alien a : ship.getAliensShot())aliens.add(a);
							
					ship.clearAlienList();
				}
				
				checkCollisions();
				for(CraftInfo c : craft)if(c.justShot())c.setShot(false);
			}//below this point happens even when the game is not running or paused
			
			//Next section
			if(doneC>clientOutputStreams.size()*4/5){restart(); doneC=0;infobox.append("level begun/restarted");}
			else{
				//determine if won=================================================================
				int lost=0; for(CraftInfo c : craft){if(c.isOperational()==false)lost++;}
				if(lost == craft.size()){message+="level over-lost/"; ifWon=false;ingame=false;}

				else if(aliens.size()==0&&alienShips.size()==0){message+="level over-won/"; ifWon=true;ingame=false;}
				
				
				//Write all the objects=====================================================================================
				else if(ingame){
					if(crashes.size() > 0){
						for(Point p : crashes){
							message+="object,bang,"+(p.x-20)+","+(p.y-20)+"/";
						}
					}
					if(timesThrough < 10){timesThrough++; if(timesThrough==6)crashes.clear();}
					
					for(Alien a : aliens){message+="object,alien,"+a.getX()+","+a.getY()+"/";}
					for(Bonus b : bonuses){message+="object,Bonus"+b.getNumber()+","+b.getX()+","+b.getY()+"/";}
					for(AlienShip s : alienShips){message+="object,AlienShip"+s.num+","+s.getX()+","+s.getY()+"/";}
					
					for(int I = 0; I < craft.size(); I++){//for each craft
						CraftInfo c = craft.get(I);
						//write things that will be sent to everyone (this craft and its missiles)
						if(c.isOperational()){
							if(c.getName().startsWith("C:"))message+="object,"+c.getName()+","+c.getX()+","+c.getY()+"/";
							else message+="object,Craft-"+c.getName()+","+c.getX()+","+c.getY()+"/";
							
							if(c.shieldedTime>0){
								if(c.sNum == 3)c.sNum=1; else c.sNum++;
								if(c.shieldedTime<=0)c.shieldedTime = 0;
								message+="object,sheild"+c.sNum+","+(c.getX()-8)+","+(c.getY()-10)+"/";
							}
						}
						
						if(c.getMissiles() != null){
							for(int i = 0; i < c.getMissiles().size(); i++){
								Missile m = c.getMissiles().get(i);
								message+="object,"+c.getType()+","+m.getX()+","+m.getY()+"/";
							}//end for (missiles)
						}//end if
						
						//write thing that will be sent only to this craft(other craft's crosshairs)
						for(int i = 0; i < craft.size(); i++){
							if(i != I){
								CraftInfo C = craft.get(i);
								if(C.hasCrosshairs())c.addMessage("object,otherCrosshairs,"+C.getCHX()+","+C.getCHY()+"/");
							}
						}
					}//end for (craft)
				}//end else that finds if the game is still valid
			}//end else for if the players have not selected done
			message+="alienNum:"+(aliens.size()+alienShips.size())+"/";
			sendMessage(message);
			message="";
			
		}//end if that makes sure this ActionEvent comes from the timer
	}// end actionPerformed method

	public void checkCollisions(){
		boolean gotC = false, hit = false;
		
		for(int i = 0; i<craft.size(); i++){//craft loop=================================
			CraftInfo c = craft.get(i);
			Rectangle craftR = c.getBounds();
			
			if(c.isOperational()){
				for (int j = 0; j < bonuses.size(); j++){
					Bonus b = bonuses.get(j);
					Rectangle bonusR = b.getSmallBounds();
					if (craftR.intersects(bonusR)){
						if(b.getNumber()==12)period+=350;
						else if(b.getNumber()==14)c.shieldedTime += 800;
						else c.addMessage("got bonus"+b.getNumber()+"/");
						bonuses.remove(b);
					}
				}
				
				for (int j = 0; j < aliens.size(); j++){
					Alien a = aliens.get(j);
					Rectangle alienR = a.getBounds();
					if(craftR.intersects(alienR)){
						if(c.shieldedTime > 0){
							if(over){
								over = false;
								int newX = c.getX(), newY = c.getY();
								
								if(alienR.intersects(new Rectangle(c.getX()-3, c.getY(), c.getW()-3, c.getH())) &&
													alienR.intersects(new Rectangle(c.getX()+3, c.getY(), c.getW()+3, c.getH()))){
									if(c.getY()<a.getY())newY = c.getY()-4;
									else newY = c.getY()+4;
								}
								c.setY(newY);
								craftR = c.getBounds();
								if(craftR.intersects(alienR)){
									if(c.getX()<a.getX())newX = c.getX()-4;
									else newX = c.getX()+4;
								}
								c.setX(newX);
								
								c.addMessage("new location:"+newX+":"+newY+"/");
								sendMessage("");
								over = true;
							}//end if (to make sure it isn't already doing this)
						}//end if that sees if the craft is sheilded
						
						else{
							gotC = true; c.addMessage("RI/");
							a.setHealth(a.getHealth()-1);
							if(a.getHealth()<=0)aliens.remove(a);
						}
					}//end intersection-checking if
				}//end for loop for the aliens
				for (int j = 0; j < alienShips.size(); j++){
					AlienShip ship = alienShips.get(j);
					
					Rectangle shipR = ship.getBounds();
					if(craftR.intersects(shipR)){
						int sH = ship.getHealth();
						ship.setHealth(sH - 1);
						if(c.shieldedTime > 0)ship.setHealth(sH - 2);
						if(ship.getHealth()<=0)alienShips.remove(ship);
						gotC = true; c.addMessage("RI/");
					}
				}
				
				// if it hits a craft...
				if(aggressive && started > 50 && over)
				for (int j = 0; j < craft.size(); j++){
					if(j != i){
						CraftInfo c2 = craft.get(j);
						Rectangle craft2R = c2.getBounds();
						
						if(craftR.intersects(craft2R) && c2.isOperational() && over && timesThrough == 10){
							over = false;
							if(c2.shieldedTime == 0)gotC = true; c2.addMessage("RI/");
							if(c.shieldedTime == 0)gotC = true; c.addMessage("RI/");
							
							if(c.shieldedTime == 0 || c2.shieldedTime == 0){
								
								Point p = new Point(c.getX()+c.getW()/2, c.getY()+c.getH());
								
								int newX1 = c.getX(), newY1 = c.getY(), newX2 = c2.getX(), newY2 = c2.getY();
								
								if(craftR.intersects(new Rectangle(c2.getX()-3, c2.getY(), c2.getW()-3, c2.getH())) &&
										craftR.intersects(new Rectangle(c2.getX()+3, c2.getY(), c2.getW()+3, c2.getH()))){						
									if(c.getY()<c2.getY()){
										newY1 = c.getY()-3; newY2 = c2.getY()+3;
										p = new Point(c.getX()+c.getW()/2, c.getY()+c.getH());
									}
									else{
										newY1 = c.getY()+3; newY2 = c2.getY()-3;
										p = new Point(c.getX()+c.getW()/2, c.getY());
									}
								}
								
								c.setX(newX1); c.setY(newY1);
								c2.setX(newX2);c2.setY(newY2);
								craftR = c.getBounds();
								craft2R = c2.getBounds();
								if(craftR.intersects(craft2R)){
									if(c.getX()<c2.getX()){
										newX1 = c.getX()-3; newX2 = c2.getX()+3;
										p = new Point(c.getX()+c.getW(), c.getY()+c.getH()/2);
									}
									else{
										newX1 = c.getX()+3; newX2 = c2.getX()-3;
										p = new Point(c.getX(), c.getY()+c.getH()/2);
									}
								}
								
								crashes.add(p);
								timesThrough = 0;
								
								c.addMessage("new location:"+newX1+":"+newY1+"/");
								c2.addMessage("new location:"+newX2+":"+newY2+"/");
								sendMessage("");
								over = true;
							}//end if confirming that the craft are not both sheilded
						}//end if (intersection)
					}//end if (different craft)
				}//end for (for all craft)
				else started++;
				
				//This craft's missiles>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
				if(c.getMissiles()!=null){
					ArrayList<Missile> ms = c.getMissiles();
					for (int x = 0; x < ms.size(); x++){
						Missile m = ms.get(x);
						if(m.getX()>Width)m.setVisible(false);
						
						Rectangle missileR = m.getBounds();
						
						for (int j = 0; j < aliens.size(); j++){
							Alien a = aliens.get(j);
							Rectangle alienR = a.getBounds();
							if (missileR.intersects(alienR)){
								if(c.isPower()==false)ms.remove(m);
								a.setHealth(a.getHealth()-1);
								if(a.getHealth()<=0)aliens.remove(a);
								hit = true;
							}
						}
						for (int j = 0; j < alienShips.size(); j++){
							AlienShip ship = alienShips.get(j);
							Rectangle alienShipR = ship.getBounds();
			
							if (missileR.intersects(alienShipR)){
								if(c.isPower()==false)ms.remove(m);
								ship.setHealth(ship.getHealth()-1);
								if(ship.getHealth()<=0)alienShips.remove(ship);
								hit = true;
							}
						}
						if(aggressive)for (int j = 0; j < craft.size(); j++){
							CraftInfo c2 = craft.get(j);
							Rectangle craft2R = c2.getBounds();
							craft2R.setBounds((int)craft2R.getMinX(),
									(int)craft2R.getMinY()+2, (int)craft2R.getWidth()/3, (int)craft2R.getHeight()-2);
							
							if(missileR.intersects(craft2R)){
								if(c.isPower()==false)ms.remove(m);
								c2.addMessage("RI/");
								hit = true;
							}
						}//end for
					}// end of if loop for missiles
					
				}//end of if to see if there are any missiles
				
				//This is the craft's crosshairs>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\
				if(c.hasCrosshairs() && c.justShot()){
					Point center = new Point();
					center.setLocation(c.getCrosshairBounds().getCenterX(), c.getCrosshairBounds().getCenterY());
					
					for(int j = 0; j < aliens.size(); j++){
						Alien a = aliens.get(j);
						Rectangle alienR = a.getBounds();
						c.setShot(false);
						if(alienR.contains(center)){
							a.setHealth(a.getHealth()-1);
							if(a.getHealth()<=0)aliens.remove(a);
							hit = true; break;
						}
					}
					for (int j = 0; j < alienShips.size(); j++){
						AlienShip ship = alienShips.get(j);
						Rectangle alienShipR = ship.getBounds();
		
						if(alienShipR.contains(center)){
							c.setShot(false); ship.setHealth(ship.getHealth()-1);
							if(ship.getHealth()<=0)alienShips.remove(ship);
							hit = true; break;
						}
					}
				}//end crosshairs section
			}//end if (operational)
			
			craft.set(i, c);//Set craft at i to this craft, c.
			
		}//end of craft loop
		if(hit)message+="hit/";
		if(gotC)message+="gotcha/";
	}// end of method
	
	public void sendMessage(String message){
		Iterator<PrintWriter> it = clientOutputStreams.iterator();
		
		int x = 0;
		while(it.hasNext()){
			PrintWriter writer = (PrintWriter) it.next();
			
			writer.println(message+craft.get(x).getMessage());//the overall message plus special messages for this craft alone
			writer.flush();
			
			craft.get(x).clearMessage();//clear this craft's personal messages
			x++;
		}// End while
	}// Close sendMessage
	
	public void scanThis(String message, int i){
		
		if(message.startsWith("place craft")){
			String[] edits = message.split("/");
			
			if(edits[1].equals("on")){
				craft.get(i).setOperational(true);
				
				craft.get(i).setX(Integer.parseInt(edits[2]));
				craft.get(i).setY(Integer.parseInt(edits[3]));
				
				if(edits[4].equals("on"))craft.get(i).setPower(true);
				else craft.get(i).setPower(false);
				
				craft.get(i).setType(edits[5]);
				
				if(edits[6].startsWith("on")){
					craft.get(i).setCrosshairs(true);
					
					String[] location = edits[6].split(":");
					if(location.length > 2){
						craft.get(i).setCHX(Integer.parseInt(location[1]));
						craft.get(i).setCHY(Integer.parseInt(location[2]));
					}
					
					if(location.length > 3 && location[3].equals("shot")/* && aggressive == false*/)craft.get(i).setShot(true);
				}
				else craft.get(i).setCrosshairs(false);
				
			}else craft.get(i).setOperational(false);
		}
		
		else if(message.startsWith("new missile")){
			String[] ms = message.split(":");
			for(int a = 0; a < ms.length; a++){
				String[] info = ms[a].split("/");
				if(craft.get(i).getMissiles() == null){craft.get(i).setMissiles(new ArrayList<Missile>());}
				
				int x = Integer.parseInt(info[1]), y = Integer.parseInt(info[2]), speed = Integer.parseInt(info[3]);
				craft.get(i).add(new Missile(x, y, speed, craft.get(i).getType()));
			}
		}
		
		else if(message.equals("[paused]")){pausedC++;
			infobox.append("player"+(i+1)+" paused");
			if(pausedC>clientOutputStreams.size()*4/5){
				/*this.message+="paused/";*/sendMessage("paused/");
				paused=true; infobox.append("Game paused" );
			}
		}
		else if(message.equals("[unpaused]")){pausedC--;
		infobox.append("player"+(i+1)+" unpaused");
			if(pausedC-1<clientOutputStreams.size()*4/5){
				/*this.message+="unpaused/";*/sendMessage("unpaused/");
				paused=false;infobox.append("Game resumed");
			}
		}
		
		else if(message.equals("[done]")){doneC++;infobox.append("player"+(i+1)+" has selected {done}");}
		
		else this.message+=message;
	}
	
	public int loadFile(File file, int levelOn){
		int maxP = 2;// Number of players
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			
			if(levelOn == 1){
				String[] data = reader.readLine().split("/");
				levels = Integer.parseInt(data[0]);
				timer = new Timer(Integer.parseInt(data[1]), this);
				maxP = Integer.parseInt(data[2]);
			}
				
			while(!reader.readLine().equals("Level"+(levelOn)+":")){}//wait until we get to the right level
			
			String[] levelInfo = reader.readLine().split("/");
			speed = Integer.parseInt(levelInfo[0]);
			if(levelInfo[1].equals("on"))aggressive = true;
			else aggressive = false;
			
			String line = null;
			while ((line = reader.readLine()) != null && !line.equals("Level"+(levelOn+1)+":")){
				String[] data = line.split("/");
				
				if(data[0].equals("alien"))aliens.add(new Alien(Integer.parseInt(data[1]), Integer.parseInt(data[2]), alienHealth, speed));
				
				else if(data[0].startsWith("Bonus"))bonuses.add(new Bonus(
							Integer.parseInt(data[1]), Integer.parseInt(data[2]), speed, Integer.parseInt(data[0].replace("Bonus", ""))));
				
				else if(data[0].startsWith("AlienShip")){
					int shipHealth;if(alienHealth>1){shipHealth=alienHealth*3;}else shipHealth=4;
					int type = Integer.parseInt(data[0].replace("AlienShip", ""));
					
					alienShips.add(new AlienShip(Integer.parseInt(data[1]), Integer.parseInt(data[2]), shipHealth, speed, type));
				}
			}reader.close();
		}catch(Exception ex){
			JOptionPane.showMessageDialog(infobox, "Could not load the game file", message, JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
		return maxP;
	}
	
	public static void main(String[] args){
		String[] valid = confirmPurchase.go().split("/");
		if(valid[0].equals("1"))new GameServer().go(valid[1]);
	}
}