package edu.hitsz.aircraft;

import edu.hitsz.bullet.DirectStrategy;

public class EliteEnemy extends Enemy{
    protected EliteEnemy(int locationX, int locationY, int speedX, int speedY, int hp){
        super(locationX, locationY, speedX, speedY, hp, new DirectStrategy());
    }

    @Override
    public void update(){
        this.vanish();
    }
}
