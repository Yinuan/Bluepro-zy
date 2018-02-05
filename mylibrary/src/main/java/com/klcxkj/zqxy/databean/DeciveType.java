package com.klcxkj.zqxy.databean;

import java.io.Serializable;

/**
 * autor:OFFICE-ADMIN
 * time:2017/11/15
 * email:yinjuan@klcxkj.com
 * description:
 */

public class DeciveType implements Serializable {

 private int typeid;//设备类型
    private String devname;//设备名

    public int getTypeid() {
        return typeid;
    }

    public void setTypeid(int typeid) {
        this.typeid = typeid;
    }

    public String getDevname() {
        return devname;
    }

    public void setDevname(String devname) {
        this.devname = devname;
    }
}
