package com.klcxkj.zqxy.databean;

/**
 * autor:OFFICE-ADMIN
 * time:2017/12/18
 * email:yinjuan@klcxkj.com
 * description:
 */

public class WashingPerMonneyResult {


    /**
     * data : {"DevName":"室内洗衣机-C栋-3层-306房","dictid":35,"typeId":46,"PerMoney":"2000","typename":"单脱水"}
     * error_code : 0
     * message : 查询成功
     */
    private WashingPerMonneyBean data;
    private String error_code;
    private String message;

    public void setData(WashingPerMonneyBean data) {
        this.data = data;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public WashingPerMonneyBean getData() {
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
         * DevName : 室内洗衣机-C栋-3层-306房
         * dictid : 35
         * typeId : 46
         * PerMoney : 2000
         * typename : 单脱水
         */
        private String DevName;
        private int dictid;
        private int typeId;
        private String PerMoney;
        private String typename;

        public void setDevName(String DevName) {
            this.DevName = DevName;
        }

        public void setDictid(int dictid) {
            this.dictid = dictid;
        }

        public void setTypeId(int typeId) {
            this.typeId = typeId;
        }

        public void setPerMoney(String PerMoney) {
            this.PerMoney = PerMoney;
        }

        public void setTypename(String typename) {
            this.typename = typename;
        }

        public String getDevName() {
            return DevName;
        }

        public int getDictid() {
            return dictid;
        }

        public int getTypeId() {
            return typeId;
        }

        public String getPerMoney() {
            return PerMoney;
        }

        public String getTypename() {
            return typename;
        }
    }
}
