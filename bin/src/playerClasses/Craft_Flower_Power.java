package playerClasses;

public class Craft_Flower_Power extends PlayerCraft{
	public Craft_Flower_Power(Board p, int x, int y){super(p, x, y, "Flower_Power");
	   	super.typeMs="Massive_Missile";
	   	super.MsSound = "bloop.wav";
	   	super.health+=5;
	   	super.speed=1;
	}
	
	public void fire(){shotsLeft--;
		parent.frame.addMessage("new missile/"+(x + width-15)+"/"+(y + height/2-4)+"/"+ speed);
    }
	
    protected void bomb(){String b = "";
		for(int m=height/2-misNum*10-10;m<height+misNum*10-10;m+=20)b += "new missile/"+(x+width)+"/"+(y+m)+"/"+speed+":";
		parent.frame.addMessage(b);
	}
   
    public void nuke(){String n="";
    	for(int m = 13; m < parent.getH()-60; m+=30)n+="new missile/"+(x+width)+"/"+m+"/"+speed+":";
    	parent.frame.addMessage(n);
    }
}