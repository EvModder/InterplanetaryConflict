package playerClasses;

public class Craft_Bingi extends PlayerCraft{
	public Craft_Bingi(Board p, int x, int y){super(p, x, y, "Bingi");
	   	super.shotsLeft+=150;
	}
	public void fire(){shotsLeft--;
		parent.frame.addMessage("new missile/"+(x + width/2)+"/"+(y + height/2-4)+"/"+ (speed+4));
	}
}