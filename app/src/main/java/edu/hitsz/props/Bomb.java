package edu.hitsz.props;

import java.util.ArrayList;
import java.util.List;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.audio.AudioManager;
import edu.hitsz.basic.Subscriber;
import edu.hitsz.bullet.BaseBullet;

public class Bomb extends AbstractProps{
    private List<Subscriber> subscribers = new ArrayList<>();
    protected Bomb(int locationX, int locationY, int speedX, int speedY){
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void getEffect(HeroAircraft heroAircraft, List<AbstractAircraft> enemyAircrafts, List<BaseBullet> enemyBullets){
        System.out.println("BombSupply active!");
        AudioManager.bombStart();
        for(AbstractAircraft enemy : enemyAircrafts){
            if(!enemy.notValid()) {
                addSubscriber(enemy);
            }
        }
        for(BaseBullet bullet : enemyBullets){
            if(!bullet.notValid()) {
                addSubscriber(bullet);
            }
        }
        notifyAllSubscribers();
    }

    public void addSubscriber(Subscriber s) {
        subscribers.add(s);
    }

    public void removeSubscriber(Subscriber s) {
        subscribers.remove(s);
    }

    public void notifyAllSubscribers() {
        for(Subscriber s : subscribers) {
            s.update();
        }
    }
}
