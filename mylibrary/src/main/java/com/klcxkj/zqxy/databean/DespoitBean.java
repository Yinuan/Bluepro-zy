package com.klcxkj.zqxy.databean;

/**
 * autor:OFFICE-ADMIN
 * time:2017/12/7
 * email:yinjuan@klcxkj.com
 * description:
 */

public class DespoitBean {

    /**
     * savedeposit : 0
     * sumdeposit : 0
     * accid : 157
     * remark : vghg
     * zfaccid : 18565651403
     * deposittype : 342
     * status : 0
     */
    private String savedeposit;
    private int sumdeposit;
    private String accid;
    private String remark;
    private String zfaccid;
    private int deposittype;
    private int status;

    public void setSavedeposit(String savedeposit) {
        this.savedeposit = savedeposit;
    }

    public void setSumdeposit(int sumdeposit) {
        this.sumdeposit = sumdeposit;
    }

    public void setAccid(String accid) {
        this.accid = accid;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setZfaccid(String zfaccid) {
        this.zfaccid = zfaccid;
    }

    public void setDeposittype(int deposittype) {
        this.deposittype = deposittype;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSavedeposit() {
        return savedeposit;
    }

    public int getSumdeposit() {
        return sumdeposit;
    }

    public String getAccid() {
        return accid;
    }

    public String getRemark() {
        return remark;
    }

    public String getZfaccid() {
        return zfaccid;
    }

    public int getDeposittype() {
        return deposittype;
    }

    public int getStatus() {
        return status;
    }
}
