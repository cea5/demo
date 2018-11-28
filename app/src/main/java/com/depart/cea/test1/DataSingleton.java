package com.depart.cea.test1;

/**
 * Created by Administrator on 2018/4/6 0006.
 */

class DataSingleton {
    private static final DataSingleton ourInstance = new DataSingleton();

    static DataSingleton getInstance() {
        return ourInstance;
    }
    private String token;
    private DataSingleton() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
