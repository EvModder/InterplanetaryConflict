package playerClasses;

public class Craft_Blastin_Robin extends PlayerCraft{
	public Craft_Blastin_Robin(Board p, int x, int y){super(p, x, y, "Blastin' Robin");
		height-=10; width-=4;
	   	MsSound = "raspy_bird_call.wav";
	   	speed++; typeMs="Worm";
	   	super.nukesLeft+=4;
	}
	
	public void fire(){shotsLeft--;
		parent.frame.addMessage("new missile/"+(x + width)+"/"+(y + height/3)+"/"+ (speed+1));
	}
   
    public void bomb(){String b = ""; for(int m=0;m<misNum;m++){
    	b += "new missile/"+(x + width)+"/"+(y + (height/3+misNum*7)-m*12-9)+"/"+ (speed+1)+":";}
    	parent.frame.addMessage(b);
    }
}