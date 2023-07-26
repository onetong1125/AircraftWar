package edu.hitsz.game;

import android.content.Context;
import android.os.Handler;

public class MediumGame extends BaseGame{
    public MediumGame(Context context, Handler handler,String mode) {
        super(context,handler,mode);
        this.backGround = ImageManager.BACKGROUND2_IMAGE;
    }

    /**
     * 实现游戏难度增加的细节
     */
    @Override
    public void enemyMaxNumGrow() {
        if(cycleCounter % 30 == 0) {
            enemyMaxNumber += 1;
            System.out.println("敌机最大数量增为"+enemyMaxNumber);
        }
    }
    @Override
    public void eliteEnemyHpGrow() {
        if(cycleCounter % 10 == 0) {
            eliteEnemyHp += 10;
            System.out.println("敌机血量增为"+eliteEnemyHp);
        }
    }
    @Override
    public void eliteEnemyPropGrow() {
        if(cycleCounter % 20 == 0 && eliteEnemyprob < 1) {
            eliteEnemyprob += 0.03;
            System.out.println("精英敌机出现概率增为"+eliteEnemyprob*100+"%");
        }
    }
    @Override
    public void bossThresholdFall() {}
    @Override
    public void gameCycleFall() {}

}
