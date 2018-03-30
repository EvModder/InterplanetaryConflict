package playerClasses;

public class Craft_Defector extends PlayerCraft{
	public Craft_Defector(Board p, int x, int y){super(p, x, y, "Defector");
	   	nukesLeft+=5;
	   	shotsLeft=250;
	   	MsSound = "";
	}
	
	public void fire(){shotsLeft--;
		parent.frame.addMessage("new missile/"+(x + width-4)+"/"+(y + height/4+2)+"/"+ (speed+1)+":"+
								"new missile/"+(x + width-4)+"/"+(y + (height*3/4-3))+"/"+(speed+1));
	}
}