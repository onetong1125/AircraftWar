package edu.hitsz.props;

public interface PropCreator {
    public abstract AbstractProps createProp(
            int locationX, int locationY, int speedX, int speedY);
}
