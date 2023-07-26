package edu.hitsz.game;

import android.content.Context;
import android.os.Handler;

public class HardGame extends BaseGame{
    public HardGame(Context context, Handler handler, String mode) {
        super(context,handler,mode);
        this.backGround = ImageManager.BACKGROUND3_IMAGE;
    }

    /**
     * 困难模式每次召唤boss增加血量100
     */
    @Override
    public boolean bossGrow(){
        return true;
    }

    /**
     * 实现游戏难度增加的细节
     */
    @Override
    public void enemyMaxNumGrow() {
        if(cycleCounter % 20 == 0) {
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
        if(cycleCounter % 10 == 0 && eliteEnemyprob < 1) {
            eliteEnemyprob += 0.03;
            System.out.println("精英敌机出现概率增为"+ (int)(eliteEnemyprob*100) +"%");
        }
    }
    @Override
    public void bossThresholdFall() {
        if(cycleCounter % 40 == 0 ){
            bossThreshold -= 20;
            System.out.println("boss机出现阈值降为"+bossThreshold);
        }
    }
    @Override
    public void gameCycleFall() {
        if(cycleCounter % 10 == 0 && cycleCounter > 300) {
            cycleDuration -= 50;
            System.out.println("飞机射击周期，敌机更新周期降为"+cycleDuration+"ms");
        }
    }
}
