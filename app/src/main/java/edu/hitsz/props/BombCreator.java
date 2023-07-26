package edu.hitsz.props;

public class BombCreator implements PropCreator{
    @Override
    public AbstractProps createProp(int locationX, int locationY, int speedX, int speedY){
        return new Bomb(locationX, locationY, speedX, speedY);
    }
}
