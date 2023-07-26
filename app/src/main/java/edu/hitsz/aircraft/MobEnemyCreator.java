package edu.hitsz.aircraft;

import edu.hitsz.game.ImageManager;
import edu.hitsz.activity.MainActivity;

public class MobEnemyCreator implements EnemyCreator{
    @Override
    public Enemy createEnemy(int hp){
        return new MobEnemy(
                (int) (Math.random() * (MainActivity.screenWidth - ImageManager.MOB_ENEMY_IMAGE.getWidth())),
                (int) (Math.random() * MainActivity.screenHeight * 0.05),
                0,
                10,
                30
        );
    }
}
