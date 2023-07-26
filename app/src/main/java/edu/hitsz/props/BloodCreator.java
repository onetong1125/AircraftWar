package edu.hitsz.props;

public class BloodCreator implements PropCreator{
    @Override
    public AbstractProps createProp(int locationX, int locationY, int speedX, int speedY){
        return new Blood(locationX, locationY, speedX, speedY);
    }
}
