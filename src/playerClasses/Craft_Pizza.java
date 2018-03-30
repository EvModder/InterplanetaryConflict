package playerClasses;

public class Craft_Pizza extends PlayerCraft{
	public Craft_Pizza(Board p, int x, int y){super(p, x, y, "Pizza");
	   	//MsSound = "";
		typeMs="pizza_slice";
	   	speed=1;
	}
	protected void fire(){shotsLeft--;
		parent.soundPlayer.playSound(parent, MsSound);
		parent.frame.addMessage("new missile/"+(x + width-10)+"/"+(y + height/2)+"/"+ (speed+1)+"\n");
	}
	  
	protected void bomb(){String b = "";
		bombsLeft--;
		parent.soundPlayer.playSound(parent, MsSound);
		for(int m=height/2-misNum*10-10;m<height+misNum*10-10;m+=20)b += "new missile/"+(x+width-10)+"/"+(y+m)+"/"+(speed+1)+":";
		parent.frame.addMessage(b);
	}
	protected void nuke(){String n="";
		nukesLeft--;
		for(int m = 13; m < parent.getH()-60; m+=20)n+="new missile/"+(x+width-10)+"/"+m+"/"+(speed+1)+":";
		parent.frame.addMessage(n);
	}
}
