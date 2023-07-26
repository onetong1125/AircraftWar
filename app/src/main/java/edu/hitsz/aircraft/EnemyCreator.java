package edu.hitsz.aircraft;

public interface EnemyCreator {
    /**
     * 创建敌机方法
     * @return 创建的敌机
     */
    public abstract Enemy createEnemy(int hp);
}
