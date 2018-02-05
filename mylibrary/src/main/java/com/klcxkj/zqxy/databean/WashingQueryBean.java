package com.klcxkj.zqxy.databean;

/**
 * autor:OFFICE-ADMIN
 * time:2017/12/16
 * email:yinjuan@klcxkj.com
 * description:预约查询到的洗衣机实体类
 */

public class WashingQueryBean {


    /**
     * isUesd : 0
     * DevName : 室内洗衣机-A栋-4层-403房
     * LastOrderStopDT : 2017-12-04 00:00:00.0
     * LastXFDT : 2017-12-16 14:17:19.0
     * IsDevUsed : 0
     * DevID : 11
     */
    private int isUesd;
    private String DevName;
    private String LastOrderStopDT;
    private String LastXFDT;
    private int IsDevUsed;
    private int DevID;

    public void setIsUesd(int isUesd) {
        this.isUesd = isUesd;
    }

    public void setDevName(String DevName) {
        this.DevName = DevName;
    }

    public void setLastOrderStopDT(String LastOrderStopDT) {
        this.LastOrderStopDT = LastOrderStopDT;
    }

    public void setLastXFDT(String LastXFDT) {
        this.LastXFDT = LastXFDT;
    }

    public void setIsDevUsed(int IsDevUsed) {
        this.IsDevUsed = IsDevUsed;
    }

    public void setDevID(int DevID) {
        this.DevID = DevID;
    }

    public int getIsUesd() {
        return isUesd;
    }

    public String getDevName() {
        return DevName;
    }

    public String getLastOrderStopDT() {
        return LastOrderStopDT;
    }

    public String getLastXFDT() {
        return LastXFDT;
    }

    public int getIsDevUsed() {
        return IsDevUsed;
    }

    public int getDevID() {
        return DevID;
    }
}
