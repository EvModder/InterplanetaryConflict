package playerClasses;

public class Craft_Shadow extends PlayerCraft{
	public Craft_Shadow(Board p, int x, int y){super(p, x, y, "Shadow");
	   	super.nukesLeft+=4;x=0;
	}
	
	public void move(){
        x=0; y += dy;
        if(roundUniverse){
            if(y<-height/2){y=parent.getH()-100-height/2;}
            else if(y+height/2>parent.getH()-100){y=-height/2;}

        }else{
	        if(y<1){y=1;}
	        else if	(y+height > parent.getH()-60){y=parent.getH()-height-60;}//Minus ship_height and buttonPanel height
        }
    }
}