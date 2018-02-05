package com.klcxkj.zqxy.databean;

import java.util.List;

/**
 * autor:OFFICE-ADMIN
 * time:2017/12/5
 * email:yinjuan@klcxkj.com
 * description:
 */

public class WashingModelResult {

    /**
     * data : [{"descname":"一般日常的洗涤衣物","money":"5","typeid":16,"typename":"标准洗","PrjID":"1","xytimes":"35"},
     * {"descname":"清污的小件衣物","money":"4","typeid":17,"typename":"快速洗","PrjID":"1","xytimes":"28"},
     * {"descname":"仅对衣物进行脱水甩干","money":"2","typeid":18,"typename":"单脱水","PrjID":"1","xytimes":"15"},
     * {"descname":"大件家纺类衣物的洗涤","money":"6","typeid":19,"typename":"大件洗","PrjID":"1","xytimes":"45"}]
     * error_code : 0
     * message : 获取成功
     */
    private List<WashingModelInfo> data;
    private String error_code;
    private String message;

    public void setData(List<WashingModelInfo> data) {
        this.data = data;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<WashingModelInfo> getData() {
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
         * descname : 一般日常的洗涤衣物
         * money : 5
         * typeid : 16
         * typename : 标准洗
         * PrjID : 1
         * xytimes : 35
         */
        private String descname;
        private String money;
        private int typeid;
        private String typename;
        private String PrjID;
        private String xytimes;

        public void setDescname(String descname) {
            this.descname = descname;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public void setTypeid(int typeid) {
            this.typeid = typeid;
        }

        public void setTypename(String typename) {
            this.typename = typename;
        }

        public void setPrjID(String PrjID) {
            this.PrjID = PrjID;
        }

        public void setXytimes(String xytimes) {
            this.xytimes = xytimes;
        }

        public String getDescname() {
            return descname;
        }

        public String getMoney() {
            return money;
        }

        public int getTypeid() {
            return typeid;
        }

        public String getTypename() {
            return typename;
        }

        public String getPrjID() {
            return PrjID;
        }

        public String getXytimes() {
            return xytimes;
        }
    }
}
