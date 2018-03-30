package playerClasses;
import java.net.URL;
import java.awt.Font;
import java.awt.Color;
import java.awt.Image;
import java.net.Socket;
import java.util.Random;
import java.awt.Toolkit;
import java.awt.Graphics;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Graphics2D;
import javax.swing.JButton;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.swing.JCheckBox;
//import javax.swing.UIManager;
import javax.swing.JComboBox;
import javax.swing.ImageIcon;
import java.awt.BorderLayout;
import javax.swing.JTextArea;
import java.io.BufferedReader;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JFileChooser;
import java.io.InputStreamReader;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ScrollPaneConstants;

public class Player extends JFrame{private static Player p;
	private static final long serialVersionUID = 1L;
	
	private BufferedReader reader; private PrintWriter writer;
	private JTextField outgoing; private JTextArea incoming;
	private int Height, Width, levelOn, numOfLevels, cNum;
	private String pName, message, goingM, gameType;
	private JButton restartContinue, pauseResume;
	private Socket sock; private Board board;
 	private boolean paused, done, won, wait, alive;
	protected Random rand = new Random();
	private JCheckBox mp3, wav;
	protected boolean ingame;
	protected int alienNum;
	JFrame frame, wFrame;
	
	protected String[] craft = {"Search for Image", "Original", "Bird_Large", "Blastin' Robin", "Flower_Power",
	"Defector", "Pinker", "THE SHADOW", "Saber", "V_Plane", "Bingi", "Pizza", "Escape Shuttle", "choose for me"};
	
	static{
//		try{UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//		}catch(Exception e){e.printStackTrace();}
	}

	JTextField box;
	JButton ok = new JButton("OK");
	JPanel inputPanel = new JPanel();
	JLabel label = new JLabel("Please enter your username");
	private void setupFrame(String defaultName){
		//Get some info from the player=================================================================================
		frame = new JFrame("Setup");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setIconImage(new ImageIcon(getClass().getResource("/images/Craft-Original.png")).getImage());
		
		inputPanel.setBackground(new Color(9, 85, 213));
		box = new JTextField(defaultName);
		box.selectAll();
		inputPanel.add(label);
		inputPanel.add(box);
		inputPanel.add(ok);
		frame.add(BorderLayout.CENTER, inputPanel);
		frame.setSize(245, 100);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);
		
		ok.addActionListener(new ActionListener(){@Override public void actionPerformed(ActionEvent e){pickCraft();}});
	}

	private void pickCraft(){
		pName = box.getText();
		for(int i = pName.length()-1; i >=0; i--){
			if(pName.charAt(i) == ' '){
				String newN = "";
				for(int c = 0; c < pName.length()-1; c++){newN+=pName.charAt(c);}
				pName = newN;
			}else break;
		}
		
		//next section>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>		
		//create some other things...
		label.setText("Pick a craft from the list below:");
		final JPanel picture = new JPanel(){
			private static final long serialVersionUID = 1L;
			public void paint(Graphics g){
				if(cNum<craft.length-1 && cNum > 0){
					super.paint(g);
					Graphics2D g2d = (Graphics2D) g;
					ImageIcon ii = new ImageIcon(getClass().getResource("/images/Craft-"+craft[cNum]+".png"));
					g2d.drawImage(ii.getImage(), 0, 0, this);
					setSize(ii.getIconWidth()+60, ii.getIconHeight());
				}
			}
		};picture.repaint();
		picture.setBackground(new Color(9, 85, 213));
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		JComboBox craftList = new JComboBox(craft);
		craftList.setSelectedIndex(cNum = 8);
		craftList.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent e){
				@SuppressWarnings("rawtypes")
				JComboBox cb = (JComboBox) e.getSource();
				cNum = (int) cb.getSelectedIndex();
				picture.repaint();
			}
		});
		
		ok = new JButton("OK");
		
		frame.setVisible(false);
		frame.setSize(198, 10);
		inputPanel.removeAll();
		inputPanel.add(label);
		inputPanel.add(craftList);
		inputPanel.add(ok);
		inputPanel.add(picture);
		frame.setVisible(true);
		
		new Thread(){public void run(){
			for(int i = 10; i < 170; i++){frame.setSize(198, i);
				try{Thread.sleep(3);}catch(InterruptedException e1){}
			}
		}}.start();
		
		ok.addActionListener(new ActionListener(){@Override public void actionPerformed(ActionEvent e){getSocket();}});
	}
	
	public void getSocket(){
		if(cNum==craft.length-1)cNum = rand.nextInt(craft.length-1)+1;
		
		if(cNum == 0){
			while(true){//if it works then it will break
				JFileChooser fileLoad;
				fileLoad = new JFileChooser(((System.getenv("USERPROFILE"))/*+("\\My Pictures\\")*/));
				fileLoad.showOpenDialog(frame);
				craft[cNum] = fileLoad.getSelectedFile()+"";
				
				Image i = new ImageIcon(craft[cNum]).getImage();
				int h = i.getHeight(null), w = i.getWidth(null);
				String m;
				if(h > 110 && h>w)m = "Sorry, the image is to tall. (Max: 100)";
				else if(w > 110 && w>h)m = "Sorry, the image is to wide. (Max: 100)";
				else if(h > 110 && w > 110)m = "Sorry, that image is too large";
				else break;
				
				
				JOptionPane.showMessageDialog(null, m, "Oh no!", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		//label.setText("What GameServer do you want to join?");
		label.setText("Please enter a GameServer socket below");
		box.setText("127.0.0.1                ");
		
		ok = new JButton("OK");
		
		frame.setVisible(false);
		frame.setSize(250, 100);
		inputPanel.removeAll();
		inputPanel.add(label);
		inputPanel.add(box);
		inputPanel.add(ok);
		frame.setVisible(true);
		
		ok.addActionListener(new ActionListener(){@Override public void actionPerformed(ActionEvent e){joinGame();}});
	}
	
	private void joinGame(){
		String s = box.getText().replace(" ", "");
		
		frame.setVisible(false);
		
		try{sock = new Socket(s, 9990);
			InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
			reader = new BufferedReader(streamReader);
			writer = new PrintWriter(sock.getOutputStream());
			System.out.println("networking established");
				
			if(goingM == null){
				goingM="Join Craft-"+craft[cNum]+"\n";
				writer.println(goingM);writer.flush();
				System.out.println("PlayerCraft, "+craft[cNum]+", joined");
				goingM = "";
			}
			
			//this following section finds the number of levels and pops up a waiting window
			wFrame = new JFrame("Waiting to Start");
			wFrame.setIconImage(new ImageIcon(getClass().getResource("/images/Craft-Original.png")).getImage());
			wFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			wFrame.setVisible(false);
			label = new JLabel("waiting for other players");
			wFrame.add(BorderLayout.CENTER, label);
			wFrame.setSize(185, 100);
			wFrame.setLocationRelativeTo(null);
			wFrame.setResizable(true);
			wFrame.setVisible(true);
			
			Thread waiting = new Thread(){public void run(){
				if(wait == true){int time=0;
					while(wait){
						label.setText((label.getText()+".").replace("....", "."));
						wFrame.repaint();
						try{Thread.sleep(500);}catch(InterruptedException e){e.printStackTrace();}
						time+=1; if(time >= 1000)System.exit(0);
					}wFrame.dispose();
				}
			}};waiting.start();
			
			String m;
			wait = true;
			while((m = reader.readLine()) == null){Thread.yield();}
			String[] M = m.split("/");
			numOfLevels = Integer.parseInt(M[0]); gameType = M[1];
			System.out.println("Levels in uncoming game: "+numOfLevels);
			System.out.println("Game Type: "+gameType);
			wait = false;
			
			//everything has been set up correctly
			frame.dispose();
			wFrame.dispose();
			setUpScreen();
		}catch(IOException e){e.printStackTrace();
			frame.setVisible(false);
			JOptionPane.showMessageDialog(null, "The named server could not be found", null, JOptionPane.OK_OPTION);
			
			label.setText("Please re-enter the GameServer IP");
			box.setText("127.0.0.1                ");
			
			s = box.getText().replace(" ", "");
			frame.setVisible(true);
		}
	}
	
	private void setUpScreen(){
		System.out.println("Initializing Display...");
		if(craft[cNum].startsWith("C:\\"))setIconImage(new ImageIcon(craft[cNum]).getImage());
		else setIconImage(new ImageIcon(getClass().getResource("/images/Craft-"+craft[cNum]+".png")).getImage());
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Height= (int)screenSize.getHeight()-50;
		Width = (int)screenSize.getWidth ()-5;
		
		incoming = new JTextArea(3, 40);
		incoming.setWrapStyleWord(true);
		incoming.setEditable(false);
		incoming.setLineWrap(true);
		JScrollPane qScroller = new JScrollPane(incoming);
		qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		outgoing = new JTextField(20);
		JButton sendButton = new JButton("Send");
		sendButton.addActionListener(new SendButtonListener());
		
		//Pause/Resume======================================================================================================================
		pauseResume = new JButton();
		pauseResume.setText("Pause");
		pauseResume.setFocusable(false);
		pauseResume.addActionListener(new ActionListener(){@Override
		public void actionPerformed(ActionEvent arg0){
			if(!pauseResume.getText().startsWith("Collect Spoils")){
				if(paused){
					if(pauseResume.getText().equals("Resume")){pauseResume.setText("(Selected)");goingM+="[unpaused]\n";}
					else {pauseResume.setText("Resume"); goingM+="[paused]\n";}
				}
				else{
					if(pauseResume.getText().equals("Pause")){pauseResume.setText("(Selected)");goingM+="[paused]\n";}
					else {pauseResume.setText("Pause"); goingM+="[unpaused]\n";}
				}
			}
			else{board=null;
				pauseResume=null; restartContinue=null;
				removeAll(); setVisible(false); dispose();
			}
		}});
		
		restartContinue = new JButton();
		restartContinue.setText("Restart");
		restartContinue.setFocusable(false);
		restartContinue.addActionListener(new ActionListener(){@Override public void actionPerformed(ActionEvent e){
			if(done==false){goingM+="[done]\n";restartContinue.setText("(selected)");done=true;}}});
		//==================================================================================================================================
		
		//sound/on/off======================================================================================================================
		mp3 = new JCheckBox("Mp3s  are off");
		mp3.addActionListener(new ActionListener(){@Override public void actionPerformed(ActionEvent e){
			if(mp3.getText().equals("Mp3s  are off")){mp3.setText("Mp3s  are on");board.playMps=true;board.soundPlayer.playSound(board, board.song);}
			else{mp3.setText("Mp3s  are off");board.playMps=false;board.soundPlayer.killMp3();}
			board.requestFocus();
		}});
		
		wav = new JCheckBox("Wavs are off");
		wav.addActionListener(new ActionListener(){@Override public void actionPerformed(ActionEvent e){
			if(wav.getText().equals("Wavs are off")){wav.setText("Wavs are on");board.playWvs=true;}
			else{wav.setText("Wavs are off");board.playWvs=false;}
			board.requestFocus();
		}});
		//==================================================================================================================================
		
		//Set up the buttons and the board
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(qScroller);
		buttonPanel.add(outgoing);
		buttonPanel.add(sendButton);
		buttonPanel.add(pauseResume);
		buttonPanel.add(restartContinue);
		buttonPanel.add(wav);
		buttonPanel.add(mp3);
		buttonPanel.setBackground(Color.darkGray);
		
		board = new Board(this, cNum, 30);
		board.setFocusable(true);
		board.setBackground(Color.BLACK);
		board.setDoubleBuffered(true);
		
		//Set up the frame
		setTitle("INTERPLANETARY CONFLICT -- Craft: "+craft[cNum]);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(Width, Height);
		setResizable(false);
		setLocationRelativeTo(null);
		add(BorderLayout.CENTER, board);
		add(BorderLayout.SOUTH, buttonPanel);
		ingame = true; levelOn=1;
		board.requestFocus();
		setVisible(true);
		
		fallIntoWaitingLoop();
	}//end if that determines if the ServerSock is valid
	
	private void fallIntoWaitingLoop(){
		new Thread(){public void run(){
			try{boolean fast = true;
				
				alive = true;
				if(fast)while(alive)if(message != (message = reader.readLine())){scanThis(message);}
				
				else
				while(true){//secondReader
					if(message != (message = reader.readLine())){
						Thread reader = new Thread(){public void run(){scanThis(message);}};reader.start();
					}
				}
			}catch(IOException e){
			   JOptionPane.showMessageDialog(null, "You were cut off from the server (Probably someone killed it)","Oh My Gosh!", JOptionPane.OK_OPTION);
			}
		}}.start();
	}
		
	private void scanThis(String message){
		ArrayList<Thing> things = new ArrayList<Thing>();
		String[] tasks = message.split("/");
		for(String task : tasks){
			
			if(task.startsWith("user-")){incoming.append(task.replace("user-", "")+"\n");/*board.soundPlayer.playSound(board, "warning_beep.wav");*/}
			
			else if(task.startsWith("object") && ingame == true){
				String[] info = task.split(",");
				
				if(info[1].startsWith("C:"))
					things.add(new Thing(new ImageIcon(info[1]).getImage(), Integer.parseInt(info[2]), Integer.parseInt(info[3])));
				
				else{
					URL url = getClass().getResource("/images/"+info[1]+".png");
					things.add(new Thing(new ImageIcon(url).getImage(), Integer.parseInt(info[2]), Integer.parseInt(info[3])));
				}
			}
			else if(task.startsWith("new location:")){
				String[] info = task.split(":");
				board.thisCraft.setX(Integer.parseInt(info[1]));
				board.thisCraft.setY(Integer.parseInt(info[2]));
			}
			else if(task.equals("level over-won") && ingame){levelOn++;
				ingame=false; won=true; if(done == false)restartContinue.setText("Continue");
				board.thisCraft.takeHealth(-1);
				board.song = "file:sound/hallelujah-chorus.mp3";
				board.soundPlayer.playSound(board, "file:sound/hallelujah-chorus.mp3");
				if(levelOn>numOfLevels-1)gameOver();
				
			}
			else if(task.equals("level over-lost")&& ingame){
				ingame = won = false;
				board.song = "file:sound/fortuna.mp3";
				board.soundPlayer.playSound(board, "file:sound/fortuna.mp3");
			}			
			else if(task.equals("restart")){
				restartContinue.setText("Restart");
				done=false;ingame=true;
				board.soundPlayer.killMp3();
				board.restart(won);won=false;
			}
			else if(task.startsWith("alienNum:"))alienNum = Integer.parseInt(task.replace("alienNum:", ""));
			else if(task.equals("unpaused")){paused=false; pauseResume.setText("Pause" );}
			else if(task.equals("paused"))	{paused=true;  pauseResume.setText("Resume");}
			
			else if(task.equals("hit"))board.soundPlayer.playSound(board, "boom.wav");
			else if(task.equals("gotcha"))board.soundPlayer.playSound(board, "explosion-2.wav");
			
			else if(task.equals("RI")){board.hurtCraft();}//RI: recieved injury
			else if(task.startsWith("got bonus")){
				doBonus(Integer.parseInt(task.replace("got bonus", "")));
			board.soundPlayer.playSound(board, "faint_ping.wav");}

		}//end for (Whew!)
		if(alive)board.drawThings(things);
	}
	
	public void sendStats(){
		//Get all this craft's stats==========================================================================
		String alive = "";
		String onOff = "";
		String crossH = "";
		if(board.thisCraft.isVisible()){alive="on";//finds if alive or not
			if(board.crosshairs!=null){
				crossH="on"+":"+board.crosshairs.getX()+":"+board.crosshairs.getY()+":";
				if(board.crosshairs.justShot)crossH+="shot";else crossH+=" ";
			}else crossH = "off";
			
			if(board.thisCraft.isPower())onOff="on"; else onOff = "off";//finds visible or not
		}else alive = "off";
		
		
		//..and add them all to the end of a the outgoing message
		goingM +="place craft/"+alive+"/"+board.thisCraft.getX()+"/"+board.thisCraft.getY()+"/"+onOff+"/"+
										board.thisCraft.getMsType()+"/"+crossH;
		writer.println(goingM);
		writer.flush();
		goingM="";
	}
		
	private class SendButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent ev){
			if(!outgoing.getText().equals(""))goingM+="user-"+pName+": "+outgoing.getText()+"/\n";
			outgoing.setText("");
			board.requestFocus();
		}//close actionPerformed method
	}//close inner class
			
	public void takeLine(){
		String[] lines = incoming.getText().split("\n");
		int linesToKeep = lines.length;
		if(linesToKeep>15)linesToKeep=15;
		String less = "";for(int i = 1; i < linesToKeep; i++)less+=lines[i]+"\n";
		incoming.setText(less);
	}
	
	private void gameOver(){
		board.kill();
		alive = false;
		board = null;
		dispose();
		
		JPanel victoryPanel = new JPanel();victoryPanel.setBackground(Color.BLACK);
		JLabel victory1 = new JLabel("The aliens have been defeated.");
		JLabel victory2 = new JLabel("Victory is yours!");
		
		Font big = new Font("Times New Roman", Font.BOLD, 80);
		victory1.setFont(big);
		victory2.setFont(big);
		victoryPanel.add(victory1);
		victoryPanel.add(victory2);
		
		JButton spoils = new JButton("Collect Spoils (Not in Extended Version Spacewar(Intergalactic Warfare), sorry)");
		JButton replay = new JButton("Replay (faulty)");
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.DARK_GRAY);
		panel.add(spoils);
		panel.add(replay);
		
		replay.addActionListener(new ActionListener(){
		@Override public void actionPerformed(ActionEvent e){wFrame.dispose(); p = new Player();}});
		
		wFrame = new JFrame("Victory is Yours!");
		wFrame.add(BorderLayout.CENTER, victoryPanel);
		wFrame.add(BorderLayout.SOUTH, panel);
		wFrame.setSize(Width, Height);
		wFrame.setLocationRelativeTo(null);
		wFrame.setResizable(false);
		wFrame.setVisible(true);
	}
	
	// This bit replaces a part from the bonus class
	private void doBonus(int bonus){
		if(bonus==1&&board.thisCraft.getSpeed()>1){board.thisCraft.setSpeed(board.thisCraft.getSpeed()-1);board.thisCraft.addShots(100);}
		else if(bonus==2 || bonus==1){board.thisCraft.setSpeed(board.thisCraft.getSpeed()+1);}
		else if(bonus==3){board.thisCraft.addShots(50);}
		else if(bonus==4)board.thisCraft.addShots(70);
		else if(bonus==5)board.thisCraft.addBombs(3);
		else if(bonus==6)board.thisCraft.addBombs(4);
		else if(bonus==7)board.thisCraft.addNukes(1);
		else if(bonus==8)board.thisCraft.addNukes(2);
		else if(bonus==9)board.thisCraft.switchRound();
		else if(bonus==10)board.thisCraft.addSuperShots(1);
		else if(bonus==11)board.thisCraft.addMisNum(((int)Math.random()*3)+1);
		//if the bonus number is 12, the GameServer does it.
		else if(bonus==13)board.thisCraft.takeHealth(-1);
		//if the bonus number is 14, the GameServer does it.
		else if(bonus==15)board.thisCraft.addSuperShots(500);//doesn't exist yet
	}
	
	public static void main(String[] args){
		String[] valid = confirmPurchase.go().split("/");
		if(valid[0].equals("true"))p = new Player(); p.setupFrame(valid[1]);
	}
	
	public int getW(){return Width;}
	public int getH(){return Height;}
	public int getLevel(){return levelOn;}
	public boolean isIngame(){return ingame;}
	public boolean isPaused(){return paused;}
	public void addMessage(String m){goingM+=m+"\n";}
}