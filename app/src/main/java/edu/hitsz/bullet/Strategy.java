package edu.hitsz.bullet;

import java.util.List;

import edu.hitsz.aircraft.AbstractAircraft;

public interface Strategy {
    public abstract List<BaseBullet> executeShooting(AbstractAircraft aircraft);
}
