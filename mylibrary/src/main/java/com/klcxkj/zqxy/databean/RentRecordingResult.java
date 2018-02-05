package com.klcxkj.zqxy.databean;

import java.util.List;

/**
 * autor:OFFICE-ADMIN
 * time:2017/12/8
 * email:yinjuan@klcxkj.com
 * description:
 */

public class RentRecordingResult {

    /**
     * data : [{"phone":"18565651403","deposit":0,"accid":"157","devname":"租赁空调","applicateID":1,"devtypeid":"354","status":0}]
     * error_code : 0
     * message : 获取成功
     */
    private List<RentRecordingBean> data;
    private String error_code;
    private String message;

    public void setData(List<RentRecordingBean> data) {
        this.data = data;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<RentRecordingBean> getData() {
        return data;
    }

    public String getError_code() {
        return error_code;
    }

    public String getMessage() {
        return message;
    }

    public class DataEntity {
        /**
         * phone : 18565651403
         * deposit : 0
         * accid : 157
         * devname : 租赁空调
         * applicateID : 1
         * devtypeid : 354
         * status : 0
         */
        private String phone;
        private int deposit;
        private String accid;
        private String devname;
        private int applicateID;
        private String devtypeid;
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

        public int getStatus() {
            return status;
        }
    }
}
