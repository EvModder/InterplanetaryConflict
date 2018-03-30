package GSclasses;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import javax.swing.ImageIcon;
import javax.swing.JTextArea;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Color;
import java.io.File;

public class InfoBox extends JFrame{JTextArea label;
	private static final long serialVersionUID = 1L;
	
	private File file;
	private int chosen;
	private boolean selected;
	
	private String[] gameTypes = new String[]{"Custom", "Default", "Speedy", "Dawdling", "Extra Bonuses", "All-Out Attack",
											"Alien Fleet", "Player vs. Player", "Easy Startoff", "Tough Startoff", "All Aboard"};
	
	public void append(String str){label.setText(label.getText()+str+"\n");
		String[] lines = label.getText().split("\n");
		if(lines.length > 5){
			String less = "";
			for(int i = 1; i < lines.length; i++)less+=lines[i]+"\n";
			label.setText(less);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String getGameInfo(){
		setVisible(false);
		setTitle("Create Game");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIconImage(new ImageIcon(getClass().getResource("/images/Bonus13.png")).getImage());
		
		// Get the game type=======================================================================================================
		setSize(205, 50);
		setLocationRelativeTo(null);
		setResizable(false);
		
		JComboBox gameList = new JComboBox(gameTypes);
		
		gameList.setSelectedIndex(chosen = 1);
		gameList.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent e){
				JComboBox cb = (JComboBox) e.getSource();
				chosen = (int) cb.getSelectedIndex();
				
				if(chosen == 0){
					setVisible(false);
					JFileChooser fileLoad = new JFileChooser();
					fileLoad.showOpenDialog(InfoBox.this);
					file = fileLoad.getSelectedFile();
					setVisible(true);
				}
				selected = true;
			}
		});
		add(BorderLayout.CENTER, gameList);
		setVisible(true);
		
		addKeyListener(new MyKeyListener()); requestFocus();
		selected=false; while(selected==false){try{Thread.sleep(50);}catch(InterruptedException e1){}}
		
		//================================================================================================================
		String message = chosen+"/";
		remove(gameList);
		if(chosen != 0){
			JPanel inputPanel = new JPanel();
			inputPanel.setBackground(new Color(9, 85, 213));
			JLabel info = new JLabel("How many players should this game have?");
			JTextField box = new JTextField(" 2                    ");box.selectAll();
			box.setSize(box.getWidth()+100, box.getHeight());
			JButton ok = new JButton("OK");
			ok.addActionListener(new ActionListener(){@Override public void actionPerformed(ActionEvent e){selected = true;}});
			
			inputPanel.add(info);
			inputPanel.add(box);
			inputPanel.add(ok);
			
			setVisible(false);
			setSize(250, 90);
			add(BorderLayout.CENTER, inputPanel);
			setLocationRelativeTo(null);
			setVisible(true);
			
			selected=false; while(selected==false){try{Thread.sleep(50);}catch(InterruptedException e1){}}
			String s = box.getText().replace(" ", "");
			
			int num;
			try{num = Integer.parseInt(s);
			}catch(NumberFormatException e){num = 2;}
			remove(inputPanel);
			message+=num;
		}//end if
		
		setVisible(false);
		setTitle("~ ~ InfoBox ~ ~");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		label = new JTextArea();
		label.setWrapStyleWord(true);
		label.setEditable(false);
		label.setLineWrap(true);
		add(BorderLayout.CENTER, label);
		setSize(550, 150);
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
		
		return message;
	}
	
	public File getFile(){return file;}
	public String getGameType(){return gameTypes[chosen];}
		
	private class MyKeyListener implements KeyListener{
		@Override public void keyTyped(KeyEvent ev){/*do nothing*/}
		@Override public void keyReleased(KeyEvent ev){/*do nothing*/}
		@Override public void keyPressed(KeyEvent e){
			int key = e.getKeyCode();
	        
			if(key == KeyEvent.VK_ENTER)selected = true;
			else if(key == KeyEvent.VK_SHIFT)selected = true;
			else if(key == KeyEvent.VK_SPACE)selected = true;
			selected = true;
		}
	}
}