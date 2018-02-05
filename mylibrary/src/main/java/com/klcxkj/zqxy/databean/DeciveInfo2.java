package com.klcxkj.zqxy.databean;

/**
 * autor:OFFICE-ADMIN
 * time:2017/12/5
 * email:yinjuan@klcxkj.com
 * description:设备小类
 */

public class DeciveInfo2 {

    /**
     * typename2 : 热水表
     * devtypeid : 1
     * devtypepid : 4
     * typename : 热水表
     */
    private String typename2;
    private int devtypeid;
    private int devtypepid;
    private String typename;

    public void setTypename2(String typename2) {
        this.typename2 = typename2;
    }

    public void setDevtypeid(int devtypeid) {
        this.devtypeid = devtypeid;
    }

    public void setDevtypepid(int devtypepid) {
        this.devtypepid = devtypepid;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    public String getTypename2() {
        return typename2;
    }

    public int getDevtypeid() {
        return devtypeid;
    }

    public int getDevtypepid() {
        return devtypepid;
    }

    public String getTypename() {
        return typename;
    }
}
