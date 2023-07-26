package edu.hitsz.aircraft;

import edu.hitsz.bullet.ScatterStrategy;

public class BossEnemy extends Enemy{
    protected BossEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp, new ScatterStrategy());
    }

    @Override
    public int getPropSupplyNum(){
        return 3;
    }

    @Override
    public void update() {
        this.decreaseHp(100);
    }
}
