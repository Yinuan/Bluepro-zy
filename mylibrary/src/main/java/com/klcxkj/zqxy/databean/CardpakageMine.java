package com.klcxkj.zqxy.databean;

import java.io.Serializable;

/**
 * autor:OFFICE-ADMIN
 * time:2017/12/6
 * email:yinjuan@klcxkj.com
 * description:
 */

public class CardpakageMine implements Serializable {


    /**
     * depositxtype : 51
     * YKTCtype : 0
     * YKTCtimes : null
     * MonthHadTimes : 10
     * YKmoney : 100.0
     * MonthTimes : 10
     * monthcardID : null
     * Monthbuyday : 30
     * TelPhone : 18512121212
     * accid : 1
     * deposittype : 6
     * YKname : 室内洗衣机月卡
     */
    private int depositxtype;
    private int YKTCtype;
    private String YKTCtimes;
    private int MonthHadTimes;
    private String YKmoney;
    private int MonthTimes;
    private String monthcardID;
    private int Monthbuyday;
    private String TelPhone;
    private int accid;
    private int deposittype;
    private String YKname;

    public int getDepositxtype() {
        return depositxtype;
    }

    public void setDepositxtype(int depositxtype) {
        this.depositxtype = depositxtype;
    }

    public int getYKTCtype() {
        return YKTCtype;
    }

    public void setYKTCtype(int YKTCtype) {
        this.YKTCtype = YKTCtype;
    }

    public String getYKTCtimes() {
        return YKTCtimes;
    }

    public void setYKTCtimes(String YKTCtimes) {
        this.YKTCtimes = YKTCtimes;
    }

    public int getMonthHadTimes() {
        return MonthHadTimes;
    }

    public void setMonthHadTimes(int monthHadTimes) {
        MonthHadTimes = monthHadTimes;
    }

    public String getYKmoney() {
        return YKmoney;
    }

    public void setYKmoney(String YKmoney) {
        this.YKmoney = YKmoney;
    }

    public int getMonthTimes() {
        return MonthTimes;
    }

    public void setMonthTimes(int monthTimes) {
        MonthTimes = monthTimes;
    }

    public String getMonthcardID() {
        return monthcardID;
    }

    public void setMonthcardID(String monthcardID) {
        this.monthcardID = monthcardID;
    }

    public int getMonthbuyday() {
        return Monthbuyday;
    }

    public void setMonthbuyday(int monthbuyday) {
        Monthbuyday = monthbuyday;
    }

    public String getTelPhone() {
        return TelPhone;
    }

    public void setTelPhone(String telPhone) {
        TelPhone = telPhone;
    }

    public int getAccid() {
        return accid;
    }

    public void setAccid(int accid) {
        this.accid = accid;
    }

    public int getDeposittype() {
        return deposittype;
    }

    public void setDeposittype(int deposittype) {
        this.deposittype = deposittype;
    }

    public String getYKname() {
        return YKname;
    }

    public void setYKname(String YKname) {
        this.YKname = YKname;
    }
}
