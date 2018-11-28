package com.depart.cea.test1.net;

/**
 * Created by zhongpeng on 18/2/21.
 */
public class QCPoint {
    public double x = 0;
    public double y = 0;

    public QCPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public QCPoint() {
    }

    public int getIntX() {
        return (int) (x * 1000000);
    }

    public int getIntY() {
        return (int) (y * 1000000);
    }
}
