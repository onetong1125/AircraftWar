package edu.hitsz.aircraft;

import edu.hitsz.activity.MainActivity;
import edu.hitsz.bullet.Strategy;

public abstract class Enemy extends AbstractAircraft{
    protected Enemy(int locationX, int locationY, int speedX, int speedY, int hp, Strategy strategy){
        super(locationX, locationY, speedX, speedY, hp, strategy);
    }

    @Override
    public void forward() {
        super.forward();
        // 判定 y 轴向下飞行出界
        if (locationY >= MainActivity.screenHeight) {
            vanish();
        }
    }
}
