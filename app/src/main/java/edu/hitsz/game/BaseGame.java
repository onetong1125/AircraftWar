package edu.hitsz.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.LinkedList;
import java.util.List;

import edu.hitsz.activity.MainActivity;
import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.BossCreator;
import edu.hitsz.aircraft.EliteEnemyCreator;
import edu.hitsz.aircraft.EnemyCreator;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.aircraft.MobEnemyCreator;
import edu.hitsz.audio.AudioManager;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.props.AbstractProps;
import edu.hitsz.props.BloodCreator;
import edu.hitsz.props.BombCreator;
import edu.hitsz.props.BulletCreator;
import edu.hitsz.props.PropCreator;


/**
 * 游戏逻辑抽象基类，遵循模板模式，action() 为模板方法
 * 包括：游戏主面板绘制逻辑，游戏执行逻辑。
 * 子类需实现抽象方法，实现相应逻辑
 * @author hitsz
 */
public abstract class BaseGame extends SurfaceView implements SurfaceHolder.Callback, Runnable{

    public static final String TAG = "BaseGame";
    boolean mbLoop = false; //控制绘画线程的标志位
    private SurfaceHolder mSurfaceHolder;
    private Canvas canvas;  //绘图的画布
    private Paint mPaint;
    private Handler handler;
    private String mode;

    //点击屏幕位置
    float clickX = 0, clickY=0;

    private int backGroundTop = 0;

    /**
     * 背景图片缓存，可随难度改变
     */
    protected Bitmap backGround;



    /**
     * 时间间隔(ms)，控制刷新频率
     */
    private int timeInterval = 16;

    private final HeroAircraft heroAircraft;
    private EnemyCreator enemyCreator;
    private PropCreator propCreator;
    protected final List<AbstractAircraft> enemyAircrafts;

    private final List<BaseBullet> heroBullets;
    private final List<BaseBullet> enemyBullets;
    private final List<AbstractProps> props;

    protected int enemyMaxNumber = 2;
    /**
     * 出现精英机的概率
     */
    protected double eliteEnemyprob = 0.3;
    /**
     * 精英机血量
     */
    protected int eliteEnemyHp = 90;
    /**
     * 出现boss敌机的阈值
     */
    protected int bossThreshold = 200;
    /**
     * boss机血量
     */
    private int bossEnemyHp = 300;
    /**
     * 标识boss敌机已出现
     */
    private boolean bossExist = false;

    public boolean gameOverFlag = false;
    private int score = 0;
    private int time = 0;
    private String rivalName = null;
    private int rivalScore = 0;
    private int rivalHp = 0;


    /**
     * 周期（ms)
     * 控制英雄机射击周期，默认值设为简单模式
     */
    protected int cycleDuration = 600;
    private int cycleTime = 0;
    protected int cycleCounter = 0;



    public BaseGame(Context context, Handler handler, String mode){
        super(context);
        this.handler = handler;
        this.mode = mode;
        mbLoop = true;
        mPaint = new Paint();  //设置画笔
        mSurfaceHolder = this.getHolder();
        mSurfaceHolder.addCallback(this);
        this.setFocusable(true);
        ImageManager.initImage(context);
        AudioManager.initAudio(context);

        // 初始化英雄机
        heroAircraft = HeroAircraft.getHeroAircraft();

        enemyAircrafts = new LinkedList<>();
        heroBullets = new LinkedList<>();
        enemyBullets = new LinkedList<>();
        props = new LinkedList<>();

        heroController();
    }
    /**
     * 游戏启动入口，执行游戏逻辑
     */
    public void action() {
        AudioManager.bgmStart();
        //new Thread(new Runnable() {
        Runnable task = () -> {

                time += timeInterval;

            // 周期性执行（控制频率）
            if (timeCountAndNewCycleJudge()) {
                cycleCounter = cycleCounter + 1;
                System.out.println(time);
                if (enemyAircrafts.size() < enemyMaxNumber) {
                    Log.d("BaseGame","produceEnemy");
                    double rand = Math.random();
                    if(rand >= eliteEnemyprob){
                        enemyCreator = new MobEnemyCreator();
                    } else {
                        enemyCreator = new EliteEnemyCreator();
                    }
                   enemyAircrafts.add(enemyCreator.createEnemy(eliteEnemyHp));
                }
                //召唤boss敌机
                callBoss();
                //飞机射出子弹
                shootAction();
                //增加游戏难度
                if(gameHarder()) {
                    harder();
                }
            }

            // 子弹移动
            bulletsMoveAction();
            // 道具移动
            propsMoveAction();
            // 飞机移动
            aircraftsMoveAction();

            // 撞击检测
            try {
                crashCheckAction();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 后处理
            postProcessAction();

            try {
                Thread.sleep(timeInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //}
        };
        new Thread(task).start();
    }

    public void heroController(){
        setOnTouchListener(new OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                clickX = motionEvent.getX();
                clickY = motionEvent.getY();
                heroAircraft.setLocation(clickX, clickY);

                if ( clickX<0 || clickX> MainActivity.screenWidth || clickY<0 || clickY>MainActivity.screenHeight){
                    // 防止超出边界
                    return false;
                }
                return true;
            }
        });
    }

    protected void callBoss(){
        if (!bossExist && score > bossThreshold && (score % bossThreshold)<= 0.1*bossThreshold) {
            bossExist = true;
            enemyCreator = new BossCreator();
            enemyAircrafts.add(enemyCreator.createEnemy(bossEnemyHp));
            if(bossGrow()) {bossEnemyHp = bossEnemyHp+100;}
            AudioManager.bossStart();
        }
    }

    private void shootAction() {
        // 英雄射击
        heroBullets.addAll(heroAircraft.shoot());
        //敌机射击
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            enemyBullets.addAll(enemyAircraft.shoot());
        }
    }

    private boolean timeCountAndNewCycleJudge() {
        cycleTime += timeInterval;
        if (cycleTime >= cycleDuration && cycleTime - timeInterval < cycleTime) {
            // 跨越到新的周期
            cycleTime %= cycleDuration;
            return true;
        } else {
            return false;
        }
    }

    private void bulletsMoveAction() {
        for (BaseBullet bullet : heroBullets) {
            bullet.forward();
        }
        for (BaseBullet bullet : enemyBullets) {
            bullet.forward();
        }
    }

    private void propsMoveAction(){
        for(AbstractProps prop : props){
            prop.forward();
        }
    }

    private void aircraftsMoveAction() {
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            enemyAircraft.forward();
        }
    }


    /**
     * 碰撞检测：
     * 1. 敌机攻击英雄
     * 2. 英雄攻击/撞击敌机
     * 3. 英雄获得补给
     */
    private void crashCheckAction() throws InterruptedException {
        // 敌机子弹攻击英雄
        for(int i=0;i<enemyBullets.size();i++) {
            BaseBullet bullet = enemyBullets.get(i);
            if (bullet.notValid()) {continue;}
            if (heroAircraft.notValid()){ continue; }
            if (heroAircraft.crash(bullet)) {
                heroAircraft.decreaseHp(bullet.getPower());
                bullet.vanish();
                AudioManager.hitStart();
            }
        }
        List<AbstractProps> newProps = new LinkedList<>();
        // 英雄子弹攻击敌机
        for (int i=0;i<heroBullets.size();i++) {
            BaseBullet bullet = heroBullets.get(i);
            if (bullet.notValid()) {
                continue;
            }
            for (int j=0; j<enemyAircrafts.size();j++){
//            for(AbstractAircraft enemyAircraft : enemyAircrafts){
                AbstractAircraft enemyAircraft = enemyAircrafts.get(j);
                if (enemyAircraft.notValid()) {
                    // 已被其他子弹击毁的敌机，不再检测
                    // 避免多个子弹重复击毁同一敌机的判定
                    continue;
                }
                if (enemyAircraft.crash(bullet)) {
                    // 敌机撞击到英雄机子弹
                    // 敌机损失一定生命值
                    enemyAircraft.decreaseHp(bullet.getPower());
                    AudioManager.hitStart();
                    bullet.vanish();
                }
                // 英雄机 与 敌机 相撞，均损毁
                if (enemyAircraft.crash(heroAircraft) || heroAircraft.crash(enemyAircraft)) {
                    enemyAircraft.vanish();
                    heroAircraft.decreaseHp(Integer.MAX_VALUE);
                }
            }
        }

        // 我方获得补给
        for (int i=0;i<props.size();i++){
            AbstractProps prop = props.get(i);
            if (prop.notValid()) {
                continue;
            }
            if (heroAircraft.notValid()){
                continue;
            }
            if (heroAircraft.crash(prop)){
                //英雄机撞到道具
                //道具生效
                AudioManager.propStart();
                prop.getEffect(heroAircraft, enemyAircrafts, enemyBullets);
                prop.vanish();
            }

        }
        //处理死亡敌机
        for (int j=0;j<enemyAircrafts.size();j++){
            AbstractAircraft enemyAircraft = enemyAircrafts.get(j);
            if(enemyAircraft.notValid()) {
                newProps.addAll(enemyDead(enemyAircraft));
            }
        }
        props.addAll(newProps);
    }

    /**
     * 敌机死亡时需要进行的操作
     * @param enemyAircraft 死亡的敌机
     * @return 返回的因敌机死亡而新增的道具列表
     */
    private List<AbstractProps> enemyDead(AbstractAircraft enemyAircraft) {
        List<AbstractProps> res = new LinkedList<>();
        score += 10;
        int num = enemyAircraft.getPropSupplyNum();
        if (num == 3) {
            bossExist = false;
            AudioManager.bossDead();
        }
        //产生道具
        for (int k=0; k<num; k++){
            double rand = Math.random();
            if(rand <= 0.3){
                propCreator = new BloodCreator();
            }
            else if (rand <= 0.6) {
                propCreator = new BombCreator();
            }
            else {
                propCreator = new BulletCreator();
            }
            res.add(propCreator.createProp(
                    //多个道具横向分散
                    enemyAircraft.getLocationX() + (k*2 - num + 1)*30,
                    enemyAircraft.getLocationY(),
                    -3+3*k,
                    10));
        }
        return res;
    }
    /**
     * 后处理：
     * 1. 删除无效的子弹
     * 2. 删除无效的敌机
     * 3. 检查英雄机生存
     * <p>
     * 无效的原因可能是撞击或者飞出边界
     */
    private void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
        props.removeIf(AbstractFlyingObject::notValid);

        if (heroAircraft.notValid()) {
            gameOverFlag = true;
            mbLoop = false;
            Log.i(TAG, "heroAircraft is not Valid");
            AudioManager.gameOver();
            HeroAircraft.resetHeroAircraft();

        }

    }


    //***********************
    //      游戏难度相关
    //***********************
    //是否每次增加boss血量的钩子方法
    public boolean bossGrow(){
        return false;
    }
    //是否增加游戏难度的钩子方法
    public boolean gameHarder() {return true;}

    //游戏难度增加的方法
    public final void harder(){
        enemyMaxNumGrow();
        eliteEnemyHpGrow();
        eliteEnemyPropGrow();
        bossThresholdFall();
        gameCycleFall();
    }

    public abstract void enemyMaxNumGrow();
    public abstract void eliteEnemyHpGrow();
    public abstract void eliteEnemyPropGrow();
    public abstract void bossThresholdFall();

    /**
     * 游戏周期缩小
     * 飞机射击周期缩小
     * 产生新敌机周期缩小
     */
    public abstract void gameCycleFall();

    //***********************
    //      Draw 各部分
    //***********************
    public void draw() {
        canvas = mSurfaceHolder.lockCanvas();
        if(mSurfaceHolder == null || canvas == null){
            return;
        }

        //绘制背景，图片滚动
        canvas.drawBitmap(backGround,0,this.backGroundTop-backGround.getHeight(),mPaint);
        canvas.drawBitmap(backGround,0,this.backGroundTop,mPaint);
        backGroundTop +=1;
        if (backGroundTop == MainActivity.screenHeight)
            this.backGroundTop = 0;

        //先绘制子弹，后绘制飞机
        paintImageWithPositionRevised(enemyBullets); //敌机子弹


        paintImageWithPositionRevised(heroBullets);  //英雄机子弹

        paintImageWithPositionRevised(props);

        paintImageWithPositionRevised(enemyAircrafts);//敌机


        canvas.drawBitmap(ImageManager.HERO_IMAGE,
                heroAircraft.getLocationX() - ImageManager.HERO_IMAGE.getWidth() / 2,
                heroAircraft.getLocationY()- ImageManager.HERO_IMAGE.getHeight() / 2,
                mPaint);

        //画生命值
        paintScoreAndLife();
        if (mode.equals("double")) {
            //通知activity更新对手生命值
            notifyUpdateRival();
            //画对手生命值
            paintRival();
        }

        mSurfaceHolder.unlockCanvasAndPost(canvas);

    }

    private void paintImageWithPositionRevised(List<? extends AbstractFlyingObject> objects) {
        if (objects.size() == 0) {
            return;
        }

        for (int i=0;i<objects.size();i++) {
            AbstractFlyingObject object = objects.get(i);
            Bitmap image = object.getImage();
            assert image != null : objects.getClass().getName() + " has no image! ";
            canvas.drawBitmap(image, object.getLocationX() - image.getWidth() / 2,
                    object.getLocationY() - image.getHeight() / 2, mPaint);
        }
    }
    private void notifyUpdateRival() {
        Message msg = Message.obtain();
        msg.what = 0x231;
        msg.arg1 = score;
        msg.arg2 = heroAircraft.getHp();
        handler.sendMessage(msg);
    }

    /**
     * 由GameActivity调用，用于更新对手信息
     * @param rivalName 对手用户名
     * @param score 对手分数
     * @param hp 对手血量
     */
    public void updateRival(String rivalName, int score, int hp) {
        this.rivalName = rivalName;
        this.rivalScore = score;
        this.rivalHp = hp;
    }
    private void paintScoreAndLife() {
        int x = 10;
        int y = 40;

        mPaint.setColor(Color.RED);
        mPaint.setTextSize(50);
        canvas.drawText("YOU:", x, y, mPaint);
        y = y + 60;
        canvas.drawText("SCORE:" + this.score, x, y, mPaint);
        y = y + 60;
        canvas.drawText("LIFE:" + this.heroAircraft.getHp(), x, y, mPaint);
    }
    private void paintRival() {
        int x = 300;
        int y = 40;

        mPaint.setColor(Color.RED);
        mPaint.setTextSize(50);
        canvas.drawText("YOUR RIVAL:" + this.rivalName, x, y, mPaint);
        y = y + 60;
        canvas.drawText("SCORE:" + this.rivalScore, x, y, mPaint);
        y = y + 60;
        canvas.drawText("LIFE:" + this.rivalHp, x, y, mPaint);
    }
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        new Thread(this).start();
        Log.i(TAG, "start surface view thread");
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        MainActivity.screenWidth = i1;
        MainActivity.screenHeight = i2;
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        mbLoop = false;
    }

    @Override
    public void run() {

        while (mbLoop){   //游戏结束停止绘制
            synchronized (mSurfaceHolder){
                action();
                draw();
            }
        }
        Message message = Message.obtain();
        message.what = 0x230;
        message.arg1 = score;
        handler.sendMessage(message);

    }
}
