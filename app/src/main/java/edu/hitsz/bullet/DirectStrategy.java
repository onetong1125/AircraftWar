package edu.hitsz.bullet;

import java.util.LinkedList;
import java.util.List;

import edu.hitsz.aircraft.AbstractAircraft;

public class DirectStrategy implements Strategy{
    @Override
    public List<BaseBullet> executeShooting(AbstractAircraft aircraft){
        List<BaseBullet> res = new LinkedList<>();
        int direction = aircraft.getDirection();
        int shootNum = aircraft.getShootNum();
        int x = aircraft.getLocationX();
        int y = aircraft.getLocationY() + direction*2;
        int speedX = 0;
        int speedY = aircraft.getSpeedY() + direction*5;
        BaseBullet bullet;
        int num = aircraft.getPropSupplyNum();
        if (num==2) {
            for(int i=0; i<shootNum; i++){
                // 子弹发射位置相对飞机位置向前偏移
                // 多个子弹横向分散
                bullet = new HeroBullet(x + (i*2 - shootNum + 1)*10,
                        y, speedX, speedY, aircraft.getPower());
                res.add(bullet);
            }
        }
        if (num==1 || num==3) {
            for(int i=0; i<shootNum; i++){
                // 子弹发射位置相对飞机位置向前偏移
                // 多个子弹横向分散
                bullet = new EnemyBullet(x + (i*2 - shootNum + 1)*10,
                        y, speedX, speedY, aircraft.getPower());
                res.add(bullet);
            }
        }
        return res;
    }
}
