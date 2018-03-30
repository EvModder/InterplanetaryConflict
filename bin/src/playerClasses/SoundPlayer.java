package playerClasses;
import javazoom.jl.decoder.JavaLayerException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.BufferedInputStream;
import javazoom.jl.player.Player;
import javax.sound.sampled.Clip;
import javax.swing.JOptionPane;
import java.io.FileInputStream;
import java.io.InputStream;

public class SoundPlayer{
	private Clip clip;
	private Board board;
	private Player player;
	private Thread playerThread;
	
	public void playSound(Board b, String url){
  		this.board = b;
		try{InputStream is;
			if (url.startsWith("file:")) is = new FileInputStream(url.substring(5));
			else is = getClass().getResourceAsStream("/sound/" + url);
			
			if(url.endsWith(".mp3") && board.playMps)playWithJL(url, is);
			
			else if(!url.endsWith(".mp3") && board.playWvs){
			
				AudioInputStream ais = AudioSystem.getAudioInputStream(is);
				clip = AudioSystem.getClip();
				clip.open(ais);
				clip.start();
			}
		}catch (Exception e){
			String message = "Got Exception playing sound file "+url+"\n"+e.getMessage();
			JOptionPane.showMessageDialog(board, message, "SoundPlayer Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	@SuppressWarnings("deprecation")
	private void playWithJL(String url, InputStream is) {
		BufferedInputStream bis = new BufferedInputStream(is);
		try{
			if(player != null)player.close();
			if(playerThread != null && playerThread.isAlive())playerThread.stop();
			
			player = new Player(bis);
		}
		catch (JavaLayerException e1){
			String message = "Got Exception playing sound mp3 file "+url+"\n"+e1.getMessage();
			JOptionPane.showMessageDialog(board, message, "SoundPlayer Error", JOptionPane.ERROR_MESSAGE);
		}

		// run in new thread to play in background
		playerThread = new Thread(){
			public void run(){
				try{player.play();}
				catch(Exception e){
					String message = "Got Exception playing sound mp3 file \n"+e.getMessage();
					JOptionPane.showMessageDialog(board, message, "SoundPlayer Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		};
		playerThread.start();
	}
	
	@SuppressWarnings("deprecation")
	public void killMp3(){if(player != null)player.close(); if(playerThread != null && playerThread.isAlive())playerThread.stop();}
}