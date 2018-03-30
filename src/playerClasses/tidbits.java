package playerClasses;
import java.awt.Color;
import java.awt.event.KeyEvent;

public class tidbits {
	public static Color getRandomColor() {
			
		int red = (int) (Math.random() * 256);
		int blue = (int) (Math.random() * 256);
		int green = (int) (Math.random() * 256);
		return new Color(red, green, blue);
	}
	
	public static String getKey(int key) {
		String re = "";
        if(key == KeyEvent.VK_S)re+="s";
        else if(key == KeyEvent.VK_N)re+="n";
        else if(key == KeyEvent.VK_G)re+="g";
        else if(key == KeyEvent.VK_F)re+="f";
        else if(key == KeyEvent.VK_I)re+="i";
        else if(key == KeyEvent.VK_E)re+="e";
        else if(key == KeyEvent.VK_A)re+="a";
        else if(key == KeyEvent.VK_R)re+="r";
        else if(key == KeyEvent.VK_C)re+="c";
        else if(key == KeyEvent.VK_T)re+="t";
        else if(key == KeyEvent.VK_H)re+="h";
        else if(key == KeyEvent.VK_S)re+="s";
        else if(key == KeyEvent.VK_D)re+="d";
        else if(key == KeyEvent.VK_L)re+="L";
        else if(key == KeyEvent.VK_M)re+="m";
        else if(key == KeyEvent.VK_O)re+="o";
		return re;
	}
}