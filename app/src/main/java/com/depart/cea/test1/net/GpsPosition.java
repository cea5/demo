package com.depart.cea.test1.net;

/**
 * Created by zhongpeng on 18/3/25.
 */
public class GpsPosition {

    private long gtime;
    private int starnum;
    private int x;
    private int y;
    private double speed;
    private double radius;

    public long getGtime() {
        return gtime;
    }

    public void setGtime(long gtime) {
        this.gtime = gtime;
    }

    public int getStarnum() {
        return starnum;
    }

    public void setStarnum(int starnum) {
        this.starnum = starnum;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
}
