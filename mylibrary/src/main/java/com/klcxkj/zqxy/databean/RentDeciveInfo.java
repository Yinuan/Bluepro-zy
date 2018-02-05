package com.klcxkj.zqxy.databean;

/**
 * autor:OFFICE-ADMIN
 * time:2017/12/5
 * email:yinjuan@klcxkj.com
 * description:
 */

public class RentDeciveInfo {

    /**
     * dictnum : 500
     * typeid : 340
     * typename : 走廊洗衣机
     */
    private String dictnum;
    private int typeid;
    private String typename;

    public void setDictnum(String dictnum) {
        this.dictnum = dictnum;
    }

    public void setTypeid(int typeid) {
        this.typeid = typeid;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    public String getDictnum() {
        return dictnum;
    }

    public int getTypeid() {
        return typeid;
    }

    public String getTypename() {
        return typename;
    }
}
