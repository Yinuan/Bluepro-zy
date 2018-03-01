package com.klcxkj.zqxy.databean;

/**
 * autor:OFFICE-ADMIN
 * time:2018/3/1
 * email:yinjuan@klcxkj.com
 * description:
 */

public class PerBean {

    /**
     * data : {"ServerTel":"13245678901","UserID":1,"PrjName":"我的宿舍","PrjYKMoney":3,"IsUse":1,"WXPartnerKey":"无","PrjDescript":"我的宿舍","WXAPPID":"无","WXPartner":"无","WXSevret":"无","PrjID":1}
     * error_code : 0
     */
    private DataEntity data;
    private String error_code;

    public void setData(DataEntity data) {
        this.data = data;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public DataEntity getData() {
        return data;
    }

    public String getError_code() {
        return error_code;
    }

    public class DataEntity {
        /**
         * ServerTel : 13245678901
         * UserID : 1
         * PrjName : 我的宿舍
         * PrjYKMoney : 3.0
         * IsUse : 1
         * WXPartnerKey : 无
         * PrjDescript : 我的宿舍
         * WXAPPID : 无
         * WXPartner : 无
         * WXSevret : 无
         * PrjID : 1
         */
        private String ServerTel;
        private int UserID;
        private String PrjName;
        private double PrjYKMoney;
        private int IsUse;
        private String WXPartnerKey;
        private String PrjDescript;
        private String WXAPPID;
        private String WXPartner;
        private String WXSevret;
        private int PrjID;

        public void setServerTel(String ServerTel) {
            this.ServerTel = ServerTel;
        }

        public void setUserID(int UserID) {
            this.UserID = UserID;
        }

        public void setPrjName(String PrjName) {
            this.PrjName = PrjName;
        }

        public void setPrjYKMoney(double PrjYKMoney) {
            this.PrjYKMoney = PrjYKMoney;
        }

        public void setIsUse(int IsUse) {
            this.IsUse = IsUse;
        }

        public void setWXPartnerKey(String WXPartnerKey) {
            this.WXPartnerKey = WXPartnerKey;
        }

        public void setPrjDescript(String PrjDescript) {
            this.PrjDescript = PrjDescript;
        }

        public void setWXAPPID(String WXAPPID) {
            this.WXAPPID = WXAPPID;
        }

        public void setWXPartner(String WXPartner) {
            this.WXPartner = WXPartner;
        }

        public void setWXSevret(String WXSevret) {
            this.WXSevret = WXSevret;
        }

        public void setPrjID(int PrjID) {
            this.PrjID = PrjID;
        }

        public String getServerTel() {
            return ServerTel;
        }

        public int getUserID() {
            return UserID;
        }

        public String getPrjName() {
            return PrjName;
        }

        public double getPrjYKMoney() {
            return PrjYKMoney;
        }

        public int getIsUse() {
            return IsUse;
        }

        public String getWXPartnerKey() {
            return WXPartnerKey;
        }

        public String getPrjDescript() {
            return PrjDescript;
        }

        public String getWXAPPID() {
            return WXAPPID;
        }

        public String getWXPartner() {
            return WXPartner;
        }

        public String getWXSevret() {
            return WXSevret;
        }

        public int getPrjID() {
            return PrjID;
        }
    }
}
