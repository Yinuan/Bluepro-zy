package com.klcxkj.zqxy.databean;

import java.util.List;

/**
 * autor:OFFICE-ADMIN
 * time:2017/12/6
 * email:yinjuan@klcxkj.com
 * description:
 */

public class CardPackageResult {

    /**
     * data : [{"AccName":"18565651403","MonthHadTimes":30,"TelPhone":18565651403,"accid":157,"MonthbuyMoney":61}]
     * error_code : 0
     * message : 获取成功
     */
    private List<CardpakageMine> data;
    private String error_code;
    private String message;

    public void setData(List<CardpakageMine> data) {
        this.data = data;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<CardpakageMine> getData() {
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
         * AccName : 18565651403
         * MonthHadTimes : 30
         * TelPhone : 18565651403
         * accid : 157
         * MonthbuyMoney : 61
         */
        private String AccName;
        private int MonthHadTimes;
        private String TelPhone;
        private int accid;
        private int MonthbuyMoney;

        public void setAccName(String AccName) {
            this.AccName = AccName;
        }

        public void setMonthHadTimes(int MonthHadTimes) {
            this.MonthHadTimes = MonthHadTimes;
        }

        public void setTelPhone(String TelPhone) {
            this.TelPhone = TelPhone;
        }

        public void setAccid(int accid) {
            this.accid = accid;
        }

        public void setMonthbuyMoney(int MonthbuyMoney) {
            this.MonthbuyMoney = MonthbuyMoney;
        }

        public String getAccName() {
            return AccName;
        }

        public int getMonthHadTimes() {
            return MonthHadTimes;
        }

        public String getTelPhone() {
            return TelPhone;
        }

        public int getAccid() {
            return accid;
        }

        public int getMonthbuyMoney() {
            return MonthbuyMoney;
        }
    }
}
