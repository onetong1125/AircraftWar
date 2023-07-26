package edu.hitsz.aircraft;


import java.util.LinkedList;
import java.util.List;

import edu.hitsz.bullet.BaseBullet;


/**
 * 普通敌机
 * 不可射击
 *
 * @author hitsz
 */
public class MobEnemy extends Enemy {

    public MobEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp, null);
    }

    @Override
    public int getPropSupplyNum(){
        return 0;
    }

    @Override
    public void update() {
        this.vanish();
    }

}
