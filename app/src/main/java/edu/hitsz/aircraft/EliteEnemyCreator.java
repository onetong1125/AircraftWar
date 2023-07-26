package edu.hitsz.aircraft;

import edu.hitsz.game.ImageManager;
import edu.hitsz.activity.MainActivity;

public class EliteEnemyCreator implements EnemyCreator{
    @Override
    public Enemy createEnemy(int hp){
        Enemy eliteEnemy;
        eliteEnemy = new EliteEnemy(
                (int) (Math.random() * (MainActivity.screenWidth - ImageManager.MOB_ENEMY_IMAGE.getWidth())),
                (int) (Math.random() * MainActivity.screenWidth * 0.05),
                4,
                10,
                hp
        );
        eliteEnemy.setShootNum(1);
        eliteEnemy.setPower(30);
        eliteEnemy.setDirection(1);
        return eliteEnemy;
    }
}
