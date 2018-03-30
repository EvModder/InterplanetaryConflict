package playerClasses;

public class Craft_Shuttle extends PlayerCraft{
	private int times;
	public Craft_Shuttle(Board p, int x, int y){super(p, x, y,"Escape Shuttle");
	   	super.speed++;
	   	super.health=2;
	}
	
	public void move(){
        x += dx; y += dy;
        if(roundUniverse){
	        if(x<-width/2)x=parent.getW()-10-width/2;
	        else if(x > parent.getW()-width/2-10){x=-width/2;}
	        if(y<-height/2){y=parent.getH()-60-height/2;}
	        else if(y > parent.getH()-height/2-60){y=-height/2;}//Minus ship_height and buttonPanel height
        }
        else{
	        if(x<1){x=1;}
	        else if(x > parent.getW()-width-10){x=parent.getW()-width-10;}
	        if(y<1){y=1;}
	        else if	(y > parent.getH()-height-60){y=parent.getH()-height-60;}//Minus ship_height and buttonPanel height
        }
        if(times==2)times = 0;
        if(times==0)speed++; else speed--;
        times++;
        
    }
}