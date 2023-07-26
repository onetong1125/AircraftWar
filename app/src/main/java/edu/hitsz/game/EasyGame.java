package edu.hitsz.game;

import android.content.Context;
import android.os.Handler;

public class EasyGame extends BaseGame{

    public EasyGame(Context context, Handler handler, String mode) {
        super(context,handler,mode);
        this.backGround = ImageManager.BACKGROUND1_IMAGE;
    }
    //***********************
    //      重写方法
    //***********************

    //简单模式不召唤boss
    @Override
    public void callBoss(){}
    /**
     * 实现游戏难度增加的细节
     */
    @Override
    public void enemyMaxNumGrow() {}
    @Override
    public void eliteEnemyHpGrow() {}
    @Override
    public void eliteEnemyPropGrow() {}
    @Override
    public void bossThresholdFall() {}
    @Override
    public void gameCycleFall() {}

}
