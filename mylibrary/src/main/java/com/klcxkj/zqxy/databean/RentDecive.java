package com.klcxkj.zqxy.databean;

import java.util.List;

/**
 * autor:OFFICE-ADMIN
 * time:2017/12/5
 * email:yinjuan@klcxkj.com
 * description:
 */

public class RentDecive {

    /**
     * data : [{"dictnum":500,"typeid":340,"typename":"走廊洗衣机"},{"dictnum":100,"typeid":342,"typename":"吹风机"}]
     * error_code : 0
     * message : 获取成功
     */
    private List<RentDeciveInfo> data;
    private String error_code;
    private String message;

    public void setData(List<RentDeciveInfo> data) {
        this.data = data;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<RentDeciveInfo> getData() {
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
         * dictnum : 500
         * typeid : 340
         * typename : 走廊洗衣机
         */
        private int dictnum;
        private int typeid;
        private String typename;

        public void setDictnum(int dictnum) {
            this.dictnum = dictnum;
        }

        public void setTypeid(int typeid) {
            this.typeid = typeid;
        }

        public void setTypename(String typename) {
            this.typename = typename;
        }

        public int getDictnum() {
            return dictnum;
        }

        public int getTypeid() {
            return typeid;
        }

        public String getTypename() {
            return typename;
        }
    }
}
