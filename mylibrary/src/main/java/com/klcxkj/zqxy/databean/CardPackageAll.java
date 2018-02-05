package com.klcxkj.zqxy.databean;

import java.io.Serializable;

/**
 * autor:OFFICE-ADMIN
 * time:2017/12/6
 * email:yinjuan@klcxkj.com
 * description:
 */

public class CardPackageAll implements Serializable{


    /**
     * descname : 10次/月
     * typevalue : 40元
     * typeid : 21
     * typename : 洗衣机月卡
     * PrjID : 1
     */
    private String descname;
    private String typevalue;
    private int typeid;
    private String typename;
    private String PrjID;
    private String title;
    private String remark;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setDescname(String descname) {
        this.descname = descname;
    }

    public void setTypevalue(String typevalue) {
        this.typevalue = typevalue;
    }

    public void setTypeid(int typeid) {
        this.typeid = typeid;
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

    public String getTypevalue() {
        return typevalue;
    }

    public int getTypeid() {
        return typeid;
    }

    public String getTypename() {
        return typename;
    }

    public String getPrjID() {
        return PrjID;
    }
}
