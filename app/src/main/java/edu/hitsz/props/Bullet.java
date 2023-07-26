package edu.hitsz.props;

import java.util.List;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.DirectStrategy;
import edu.hitsz.bullet.ScatterStrategy;

public class Bullet extends AbstractProps{
    protected Bullet(int locationX, int locationY, int speedX, int speedY){
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void getEffect(HeroAircraft heroAircraft, List<AbstractAircraft> enemyAircrafts, List<BaseBullet> enemyBullets){
        System.out.println("FireSupply active!");
        Runnable r = () -> {
            heroAircraft.setShootNum(3);
            heroAircraft.setStrategy(new ScatterStrategy());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            heroAircraft.setStrategy(new DirectStrategy());
            heroAircraft.setShootNum(1);
        };
        new Thread(r,"火力道具线程").start();

    }
}
