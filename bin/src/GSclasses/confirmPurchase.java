package GSclasses;
import java.awt.Font;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.Graphics;
import java.io.FileReader;
import java.io.FileWriter;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics2D;
import java.io.IOException;
import javax.swing.JButton;
import java.net.InetAddress;
import java.awt.FontMetrics;
import java.awt.BorderLayout;
import javax.swing.ImageIcon;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;

public class confirmPurchase{
	static boolean done;
	
	public static String go(){
		BufferedReader reader=null;
		boolean valid = true;
		
		String oldCompName;
		int timesPLPlayed;
		int timesGSPlayed;
		int timesPassed;
		String more="";
		
		boolean found = true;
		try{reader = new BufferedReader(new FileReader("host IP and storage"));
		}catch(FileNotFoundException e){found = false;
			try{
				BufferedWriter writer = new BufferedWriter(new FileWriter("host IP and storage"));
				writer.write("Nate-PC\n0\n0\n0\nCommander Nate");// C name {enter} timesPassed {enter} times player played {enter} times GS run
				writer.close();
				System.out.println("Program launched, host: Nate-PC");
			}
			catch(IOException e1){e1.printStackTrace();}
			valid = false;
		}
		if(found){
			try{
				oldCompName = reader.readLine();
				timesPassed = Integer.parseInt(reader.readLine());
				timesPLPlayed = Integer.parseInt(reader.readLine());
				timesGSPlayed = Integer.parseInt(reader.readLine());
				String name = reader.readLine();
				reader.close();
					
				String thisCompName;
				thisCompName = InetAddress.getLocalHost().getHostName();
				
				if(timesPassed>2)valid=false;
				if(!thisCompName.equals(oldCompName)){timesPassed++;
					if(timesPassed>2)valid=false;
					else {timesGSPlayed = timesPLPlayed = 0; welcomeUser();}
				}
				
				if(valid){
					timesGSPlayed++;
					more = "© Interplanetary Conflict 2012 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~\n" +
					"Host device: "+thisCompName+" - - - previous hosts: "+timesPassed+" - - - times GameServer run (on this device): "+timesGSPlayed;
				}
				
				BufferedWriter writer = new BufferedWriter(new FileWriter("host IP and storage"));
				writer.write(thisCompName+"\n"+timesPassed+"\n"+timesPLPlayed+"\n"+timesGSPlayed+"\n"+name);
				writer.close();
				
			}catch(IOException e){e.printStackTrace();}
		}
		if(valid)return "1/"+more;
		else return "0";
	}
	
	public static void welcomeUser(){
		JFrame frame = new JFrame("Hello, Space Host!");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel = new JPanel(){
			private static final long serialVersionUID = 1L;
			public void paint(Graphics g){super.paint(g);
				Graphics2D g2d = (Graphics2D) g;
				ImageIcon ii = new ImageIcon(getClass().getResource("/images/copyright.png"));
				g2d.drawImage(ii.getImage(),/*377, 123 / 0,0 /*/
						(getWidth()-ii.getIconWidth())/2, (getHeight()-ii.getIconHeight())/2 + 50, this);
				
				g.setFont(new Font("Times New Roman", Font.PLAIN, 20));
				g2d.setColor(Color.GREEN);
				
				String info = "Welcome to Interplanetary Conflict GS! Press start to run your first Game Server";
				
				Font small = new Font("Times New Roman", Font.BOLD, 14);
				FontMetrics metr = this.getFontMetrics(small);
				
				g2d.setColor(Color.GREEN); g.setFont(small);
				g2d.drawString(info, (getWidth() - metr.stringWidth(info)) / 2, getHeight() / 2);
				
				Toolkit.getDefaultToolkit().sync();
				g.dispose();
			}
		};	
		
		panel.setBackground(new Color(9, 85, 213));
		JButton ok = new JButton("Start");
		frame.add(BorderLayout.CENTER, panel);
		frame.add(BorderLayout.SOUTH, ok);
		frame.setResizable(false);
		frame.setSize(520, 250);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		panel.repaint();
		
		ok.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent e) {
				done = true;//done is another variable for something else, but it is OK to use it here
			}
		});
		
		while(done == false){try{Thread.sleep(50);}catch(InterruptedException e1){}}
		frame.dispose();
	}
}
