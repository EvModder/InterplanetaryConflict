package InterplanetaryConflictDesigner;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.io.BufferedWriter;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import java.awt.BorderLayout;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JButton;
import java.io.FileWriter;
import java.awt.Dimension;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JMenu;
import javax.swing.Timer;
import java.awt.Toolkit;
import java.awt.Color;
import java.io.File;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ICDesigner extends JFrame{
	private static final long serialVersionUID = 1L;
	
	private int Width, Height, gSpeed, on;
	private JTextField levels, players;
	private JTextField alienSpeed;
	private final int SLnum = 3;// Starting Number of Levels
	private JPanel optionPanel;
	private JComboBox levelOn;
	private String[] aSpeed;
	private boolean[] warOn;
	private Board board;
	
	private String[] otherLevels;
	
	private String[] bs = new String[]{"Bonus1", "Bonus2", "Bonus3", "Bonus4", "Bonus5", "Bonus6",
			"Bonus7", "Bonus8", "Bonus9", "Bonus10", "Bonus11", "Bonus12", "Bonus13", "Bonus14"};
	
	public ICDesigner(){
		final JFrame w = new JFrame("Setup");
		JLabel wl = new JLabel("Setting up the frame...");
		w.setIconImage(new ImageIcon(getClass().getResource("/images/AlienShip1.png")).getImage());
		w.add(BorderLayout.CENTER, wl);
		w.setSize(180, 60);
		w.setLocationRelativeTo(null);
		w.setVisible(true);
		Thread reader = new Thread(){
			public void run(){
				while(true){
					w.requestFocus();
					try{Thread.sleep(10);}catch (InterruptedException e1) {e1.printStackTrace();}
				}
			}
		};reader.start();
		//Begin setup >>>>>>>>>>>>>>>>>>>>>>>>>>
		//set window icon
		setIconImage(new ImageIcon(getClass().getResource("/images/AlienShip1.png")).getImage());
		
		//set up items
		optionPanel = new JPanel();
		optionPanel.setBackground(Color.DARK_GRAY);
		
		//===============================================================
		JLabel warning = new JLabel("Changing this will clear all levels >>>");
		warning.setForeground(Color.BLACK);
		optionPanel.add(warning);
		
		//===============================================================
		levels = new JTextField(SLnum+"");
		JPanel holder = new JPanel();
		JLabel label = new JLabel("Number of Levels");
		label.setForeground(Color.GREEN);
		holder.setLayout(new BoxLayout(holder, BoxLayout.Y_AXIS));
		holder.setBackground(Color.DARK_GRAY);
		holder.add(label);
		holder.add(levels);
		optionPanel.add(holder);
		
		//===============================================================
		players = new JTextField("2");
		JPanel holder2 = new JPanel();
		JLabel label2 = new JLabel("Number of Players");
		label2.setForeground(Color.GREEN);
		holder2.setLayout(new BoxLayout(holder2, BoxLayout.Y_AXIS));
		holder2.setBackground(Color.DARK_GRAY);
		holder2.add(label2);
		holder2.add(players);
		optionPanel.add(holder2);
		
		//===============================================================
		aSpeed = new String[SLnum];
		for(int i = 0; i < SLnum; i++){aSpeed[i] = "1";}
		
		alienSpeed = new JTextField("1");
		JPanel holder3 = new JPanel();
		JLabel label3 = new JLabel("Alien Speed");
		label3.setForeground(Color.GREEN);
		holder3.setLayout(new BoxLayout(holder3, BoxLayout.Y_AXIS));
		holder3.setBackground(Color.DARK_GRAY);
		holder3.add(label3);
		holder3.add(alienSpeed);
		optionPanel.add(holder3);
		
		//===============================================================
				gSpeed = 30;
				JComboBox gameSpeed = new JComboBox(new String[]{
					"GamePlay: slow", "GamePlay: slow-normal", "GamePlay: normal", "GamePlay: normal-fast", "GamePlay: fast"
				});
				gameSpeed.setSelectedIndex(0);
				gameSpeed.addActionListener(new ActionListener(){
					@Override public void actionPerformed(ActionEvent e){
						JComboBox cb = (JComboBox) e.getSource();
						int a =  cb.getSelectedIndex();
						if(a == 0)gSpeed = 10;
						else if(a == 1)gSpeed = 20;
						else if(a == 2)gSpeed = 30;
						else if(a == 3)gSpeed = 35;
						else if(a == 4)gSpeed = 40;
					}
				});
				optionPanel.add(gameSpeed);
		
		//===============================================================
		warOn = new boolean[SLnum];
		JButton aggressive = new JButton("Aggressive Play: OFF");
		aggressive.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent e){
				JButton b = (JButton) e.getSource();
				
				int level = levelOn.getSelectedIndex();
				
				if(b.getText().equals("Aggressive Play: OFF")){
					warOn[level] = true;
					b.setText("Aggressive Play: ON ");
				}else{
					warOn[level] = false;
					b.setText("Aggressive Play: OFF");
				}
			}
		});
		optionPanel.add(aggressive);
		
		//===============================================================
		JComboBox aliens = new JComboBox(new String[]{"Alien", "Alien Ship1", "Alien Ship2", "Mother Ship"});
		aliens.setSelectedIndex(0);
		aliens.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent e){
				JComboBox cb = (JComboBox) e.getSource();
				int a =  cb.getSelectedIndex();
				if(a == 0)board.addThing("alien");
				if(a == 1)board.addThing("AlienShip1");
				if(a == 2)board.addThing("AlienShip2");
				if(a == 3)board.addThing("AlienShip3");
			}
		});
		optionPanel.add(aliens);
		
		//===============================================================		
		JComboBox bonuses = new JComboBox(bs);
		bonuses.setSelectedIndex(0);
		bonuses.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent e){
				JComboBox cb = (JComboBox) e.getSource();
				int a =  cb.getSelectedIndex();
				board.addThing(bs[a]);
			}
		});
		optionPanel.add(bonuses);
		
		//===============================================================
		otherLevels = new String[SLnum];
		for(int i = 0; i < otherLevels.length; i++)otherLevels[i] = "Edit Level "+(i+1);
		
		levelOn = new JComboBox(otherLevels);
		levelOn.setSelectedIndex(0);
		levelOn.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent e){
				aSpeed[on] = alienSpeed.getText();
				
				ArrayList<Thing> things = board.getThings();
				JComboBox cb = (JComboBox) e.getSource();				
				on =  cb.getSelectedIndex();
				
				alienSpeed.setText(aSpeed[on]);
				
				for(int i = 0; i < things.size(); i++){
					Thing t = things.get(i);
					if(t.getLevelFrom() != (on+1)){t.setVisible(false);}
					else t.setVisible(true);
				}board.repaint();
			}
		});
		optionPanel.add(levelOn);
		
		//===============================================================
		
		add(BorderLayout.SOUTH, optionPanel);
		
		board = new Board(this);
		add(BorderLayout.CENTER, board);

		//set the frame up
		addMenuItems();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Height= (int)screenSize.getHeight()-10;//-50;
		Width = (int)screenSize.getWidth ();//-5;
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setSize(Width, Height);
		setLocationRelativeTo(null);
		setTitle("ICDesigner");
		//setResizable(false);																											<<<
		setVisible(true);
		w.dispose();
		
		Timer timer = new Timer(20, new ActionListener(){
			@Override public void actionPerformed(ActionEvent e){
				board.checkMouse();
				try{
					if(otherLevels.length != Integer.parseInt(levels.getText())){
						board.clear();
						
						int newLength = Integer.parseInt(levels.getText());
						otherLevels = new String[newLength];
						aSpeed = new String[newLength];
						
						for(int i = 0; i < otherLevels.length; i++){
							otherLevels[i] = "Edit Level "+(i+1);
							aSpeed[i] = "1";
						}
						
						levelOn.setSelectedIndex(0);
						setVisible(false);
						optionPanel.remove(levelOn);
						
						levelOn = new JComboBox(otherLevels);
						levelOn.addActionListener(new ActionListener(){
							@Override public void actionPerformed(ActionEvent e){
								ArrayList<Thing> things = board.getThings();
								JComboBox cb = (JComboBox) e.getSource();
								int on =  cb.getSelectedIndex()+1;
								
								alienSpeed.setText(aSpeed[on-1]);
								
								for(int i = 0; i < things.size(); i++){
									Thing t = things.get(i);
									if(t.getLevelFrom() != on){t.setVisible(false);}
									else t.setVisible(true);
								}board.repaint();
							}
						});
						
						optionPanel.add(levelOn);
						setVisible(true);
					}//end if
				}catch(NumberFormatException ex){}
			}
		});timer.start();
	}
	
	public void addMenuItems(){
		//Menu Items! :) :) :) :) :) :) :) :) :) :) :) :) :) :) :) :) :) :) :) :) :) :) :) :) :) :) :) :) :) :) :) :) :) :) :) :) :) :) :) :) :) :)
		JMenuBar menuBar = new JMenuBar();
				
		//This is the File Menu>>>>>>>>>>>>>>>>>>>>>>>>>
		JMenu fileMenu = new JMenu("File");
		JMenuItem newMenuItem = new JMenuItem("New");
		JMenuItem saveMenuItem = new JMenuItem("Save");
//		JMenuItem loadMenuItem = new JMenuItem("Load");
				
		newMenuItem.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent ev){board.clear();}
		});
		saveMenuItem.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent ev){saveGame();}
		});
//		loadMenuItem.addActionListener(new ActionListener(){
//			@Override public void actionPerformed(ActionEvent ev){/* ??? */}
//		});
		
		fileMenu.add(newMenuItem);
		fileMenu.add(saveMenuItem);
//		fileMenu.add(loadMenuItem);
		menuBar.add(fileMenu);	
			
		//This is the Edit Menu>>>>>>>>>>>>>>>>>>>>>>>>>
		JMenu editMenu = new JMenu("Edit");
		JMenuItem lineUpMenuItem = new JMenuItem("Align");
		JMenuItem spaceOMenuItem = new JMenuItem("Space");
		JMenuItem zoomInMenuItem = new JMenuItem("zoom in");
		JMenuItem zoomOutMenuItem = new JMenuItem("zoom out");
		
		lineUpMenuItem.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent ev){board.align();}
		});
		spaceOMenuItem.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent ev){
				board.spaceOut();board.spaceOut();board.repaint();
			}
		});
		zoomInMenuItem.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent ev){
				if(board.zoom>.75)board.zoom-=.25;
				board.repaint();
			}
		});
		zoomInMenuItem.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent ev){
				if(board.zoom<5)board.zoom+=.25;
				board.repaint();
			}
		});
		
		editMenu.add(lineUpMenuItem);
		editMenu.add(spaceOMenuItem);
		editMenu.add(zoomInMenuItem);
		editMenu.add(zoomOutMenuItem);
		menuBar.add(editMenu);
		
		//This is the Selection Menu>>>>>>>>>>>>>>>>>>>>>>>>>
		JMenu grabMenu = new JMenu("Selection Result");
		JMenuItem copyMenuItem = new JMenuItem("Copy");
		JMenuItem moveMenuItem = new JMenuItem("Move");
		JMenuItem alignMenuItem = new JMenuItem("Align");
		JMenuItem deltMenuItem = new JMenuItem("Delete");
		
		copyMenuItem.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent ev){board.sAction = "copy";}
		});
		moveMenuItem.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent ev){board.sAction = "move";}
		});
		alignMenuItem.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent ev){board.sAction = "align";}
		});
		deltMenuItem.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent ev){board.sAction = "delete";}
		});
		
		grabMenu.add(copyMenuItem);
		grabMenu.add(moveMenuItem);
		grabMenu.add(alignMenuItem);
		grabMenu.add(deltMenuItem);
		menuBar.add(grabMenu);
		
		setJMenuBar(menuBar);
	}
	
	public void saveGame(){
		JFileChooser fileSave = new JFileChooser();
		fileSave.showSaveDialog(this);
		File file = fileSave.getSelectedFile();
		
		if(file != null){
			ArrayList<Thing> things = board.getThings();
			
			int pNum = 2;
			try{pNum = Integer.parseInt(players.getText());}catch(NumberFormatException e){pNum = 2;}
			
			try{BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			
				board.getThingsIn();
				
				String allInfo = levels.getText()+"/"+gSpeed+"/"+pNum+"\n";
				
				int length;
				try{length = Integer.parseInt(levels.getText());}
				
				catch(NumberFormatException e){
					JOptionPane.showMessageDialog(null, "The number of levels could not be determined","NOOOOO", JOptionPane.OK_OPTION);
					length = 3;
				}
				
				for(int i = 0; i < length; i++){
					
					allInfo+="Level"+(i+1)+":\n"+aSpeed[i]+"/";
					if(warOn[i])allInfo+="on\n";
					else allInfo+="off\n";
					
					for(Thing t : things){
						if(t.getLevelFrom() == i+1)allInfo+=(t.getName()+"/"+t.getX()+"/"+t.getY()+"\n");
					}
				}
				writer.write(allInfo);
				writer.close();
				
			}catch(IOException ex){
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null, "(?) Couldn't save the game file","NOOOOO", JOptionPane.OK_OPTION);
			}
		}
	}
	
	public static void main(String[] args){new ICDesigner();}
	
	public int getLevelOn(){return(levelOn.getSelectedIndex()+1);}
}
	
