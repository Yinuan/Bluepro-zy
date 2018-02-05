package com.klcxkj.zqxy.databean;

/**
 * autor:OFFICE-ADMIN
 * time:2017/12/16
 * email:yinjuan@klcxkj.com
 * description:预约的洗衣机
 */

public class WashingOrderBean {

    /**
     * devId : 11
     * Hhastime : 4
     * DevBigtypeId : 6
     * DevtypeId : 46
     * error_code : 0
     * DevtypeName : 室内洗衣机-A栋-4层-403房
     * message : 查询预约信息成功
     * status : 2
     */
    private int devId;
    private int Hhastime;
    private int DevBigtypeId;
    private int DevtypeId;
    private String error_code;
    private String DevtypeName;
    private String message;
    private int status;

    public void setDevId(int devId) {
        this.devId = devId;
    }

    public void setHhastime(int Hhastime) {
        this.Hhastime = Hhastime;
    }

    public void setDevBigtypeId(int DevBigtypeId) {
        this.DevBigtypeId = DevBigtypeId;
    }

    public void setDevtypeId(int DevtypeId) {
        this.DevtypeId = DevtypeId;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public void setDevtypeName(String DevtypeName) {
        this.DevtypeName = DevtypeName;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getDevId() {
        return devId;
    }

    public int getHhastime() {
        return Hhastime;
    }

    public int getDevBigtypeId() {
        return DevBigtypeId;
    }

    public int getDevtypeId() {
        return DevtypeId;
    }

    public String getError_code() {
        return error_code;
    }

    public String getDevtypeName() {
        return DevtypeName;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }
}
