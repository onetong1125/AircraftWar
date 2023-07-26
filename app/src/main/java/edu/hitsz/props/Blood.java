package edu.hitsz.props;

import java.util.List;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.bullet.BaseBullet;

public class Blood extends AbstractProps{
    private int power = 30;
    protected Blood(int locationX, int locationY, int speedX, int speedY){
        super(locationX, locationY, speedX, speedY);
    }

    public int getPower() {
        return power;
    }

    @Override
    public void getEffect(HeroAircraft heroAircraft, List<AbstractAircraft> enemyAircrafts, List<BaseBullet> enemyBullets){
        heroAircraft.increaseHp(power);
    }
}
