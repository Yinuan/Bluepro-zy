package com.klcxkj.zqxy.databean;

import java.util.List;

/**
 * autor:OFFICE-ADMIN
 * time:2017/12/16
 * email:yinjuan@klcxkj.com
 * description:
 */

public class WashingQueryResult {

    /**
     * data : [{"isUesd":0,"DevName":"室内洗衣机-A栋-4层-403房","LastOrderStopDT":"2017-12-04 00:00:00.0","LastXFDT":"2017-12-16 14:17:19.0","IsDevUsed":0,"DevID":11}]
     * error_code : 0
     * message : 获取成功
     */
    private List<WashingQueryBean> data;
    private String error_code;
    private String message;

    public void setData(List<WashingQueryBean> data) {
        this.data = data;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<WashingQueryBean> getData() {
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
}
