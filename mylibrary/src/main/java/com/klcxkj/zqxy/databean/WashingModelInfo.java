package com.klcxkj.zqxy.databean;

import java.io.Serializable;

/**
 * autor:OFFICE-ADMIN
 * time:2017/12/5
 * email:yinjuan@klcxkj.com
 * description:
 */

public class WashingModelInfo implements Serializable{


    /**
     * descname : 一般日常的洗涤衣物
     * times : 35
     * money : 5000
     * typevalue : 5元/35分钟
     * typeid : 33
     * devtypeid : 45
     * typename : 标准洗
     * PrjID : 1
     */
    private String descname;
    private String times;
    private String money;
    private String typevalue;
    private int typeid;
    private String devtypeid;
    private String typename;
    private String PrjID;

    public void setDescname(String descname) {
        this.descname = descname;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public void setTypevalue(String typevalue) {
        this.typevalue = typevalue;
    }

    public void setTypeid(int typeid) {
        this.typeid = typeid;
    }

    public void setDevtypeid(String devtypeid) {
        this.devtypeid = devtypeid;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    public void setPrjID(String PrjID) {
        this.PrjID = PrjID;
    }

    public String getDescname() {
        return descname;
    }

    public String getTimes() {
        return times;
    }

    public String getMoney() {
        return money;
    }

    public String getTypevalue() {
        return typevalue;
    }

    public int getTypeid() {
        return typeid;
    }

    public String getDevtypeid() {
        return devtypeid;
    }

    public String getTypename() {
        return typename;
    }

    public String getPrjID() {
        return PrjID;
    }
}
