package edu.hitsz.aircraft;



import java.util.LinkedList;
import java.util.List;

import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.basic.Subscriber;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.Strategy;

/**
 * 所有种类飞机的抽象父类：
 * 敌机（BOSS, ELITE, MOB），英雄飞机
 *
 * @author hitsz
 */
public abstract class AbstractAircraft extends AbstractFlyingObject implements Subscriber {
    /**
     * 子弹一次发射数量
     */
    private int shootNum;
    /**
     * 子弹伤害
     */
    private int power;

    /**
     * 子弹射击方向 (向上发射：1，向下发射：-1)
     */
    private int direction;
    /**
     * 生命值
     */
    protected int maxHp;
    protected int hp;
    private Strategy fireStrategy;

    public AbstractAircraft(int locationX, int locationY, int speedX, int speedY, int hp, Strategy strategy) {
        super(locationX, locationY, speedX, speedY);
        this.hp = hp;
        this.maxHp = hp;
        this.fireStrategy = strategy;
    }

    public void decreaseHp(int decrease){
        hp -= decrease;
        if(hp <= 0){
            hp=0;
            vanish();
        }
    }
    public void increaseHp(int increase){
        hp += increase;
        if(hp >= maxHp){
            hp = maxHp;
        }
    }
    public int getHp() {
        return hp;
    }

    void setFireStrategy(Strategy strategy) {
        this.fireStrategy = strategy;
    }

    public void setShootNum(int shootNum) {
        this.shootNum = shootNum;
    }

    public int getShootNum() {
        return shootNum;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getPower() {
        return power;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getDirection() {
        return direction;
    }

    public void setStrategy(Strategy strategy) {
        this.fireStrategy = strategy;
    }
    /**
     * 飞机射击方法，可射击对象必须实现
     * @return
     *  可射击对象需实现，返回子弹
     *  非可射击对象空实现，返回null
     */
    public List<BaseBullet> shoot() {
        List<BaseBullet> res = new LinkedList<>();
        if(this.fireStrategy == null) {
            return res;
        }
        res = fireStrategy.executeShooting(this);
        return res;
    }

    public int getPropSupplyNum(){
        return 1;
    }

}


