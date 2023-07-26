package edu.hitsz.aircraft;

import edu.hitsz.game.ImageManager;
import edu.hitsz.activity.MainActivity;

public class BossCreator implements EnemyCreator{
    @Override
    public Enemy createEnemy(int hp) {
        Enemy bossEnemy;
        bossEnemy = new BossEnemy(
                MainActivity.screenWidth / 2,
                ImageManager.BOSS_ENEMY_IMAGE.getHeight() / 2,
                5, 0, hp);
        bossEnemy.setShootNum(3);
        bossEnemy.setPower(30);
        bossEnemy.setDirection(1);
        return bossEnemy;
    }
}
