package com.measuredunitsetting.entity;

/**
 * Created by Administrator on 2018/1/16.
 */

public class Bluetooth {
    private String name;
    private String address;
    public Bluetooth(String name,String address)
    {
        this.name=name;
        this.address=address;
    }
    public String getName()
    {
        return name;
    }
    public String getAddress()
    {
        return  address;
    }
}
