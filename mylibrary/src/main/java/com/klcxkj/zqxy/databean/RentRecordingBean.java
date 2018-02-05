package com.klcxkj.zqxy.databean;

import java.io.Serializable;

/**
 * autor:OFFICE-ADMIN
 * time:2017/12/8
 * email:yinjuan@klcxkj.com
 * description:租赁申请记录
 */

public class RentRecordingBean implements Serializable {


    /**
     * phone : 18565651403
     * deposit : 0
     * accid : 157
     * devname : 租赁空调
     * applicateID : 1
     * devtypeid : 354
     * applicateDT : 1512697697000
     * status : 0
     */
    private String phone;
    private int deposit;
    private String accid;
    private String devname;
    private int applicateID;
    private String devtypeid;
    private String applicateDT;
    private int status;

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setDeposit(int deposit) {
        this.deposit = deposit;
    }

    public void setAccid(String accid) {
        this.accid = accid;
    }

    public void setDevname(String devname) {
        this.devname = devname;
    }

    public void setApplicateID(int applicateID) {
        this.applicateID = applicateID;
    }

    public void setDevtypeid(String devtypeid) {
        this.devtypeid = devtypeid;
    }

    public void setApplicateDT(String applicateDT) {
        this.applicateDT = applicateDT;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public int getDeposit() {
        return deposit;
    }

    public String getAccid() {
        return accid;
    }

    public String getDevname() {
        return devname;
    }

    public int getApplicateID() {
        return applicateID;
    }

    public String getDevtypeid() {
        return devtypeid;
    }

    public String getApplicateDT() {
        return applicateDT;
    }

    public int getStatus() {
        return status;
    }
}
