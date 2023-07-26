package edu.hitsz.props;

public class BulletCreator implements PropCreator{
    @Override
    public AbstractProps createProp(int locationX, int locationY, int speedX, int speedY){
        return new Bullet(locationX, locationY, speedX, speedY);
    }
}
