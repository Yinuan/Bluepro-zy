package com.klcxkj.zqxy.databean;

import java.util.List;

/**
 * autor:OFFICE-ADMIN
 * time:2017/12/5
 * email:yinjuan@klcxkj.com
 * description:
 */

public class DeciveTypeResult2 {


    /**
     * data : [{"typename2":"室内洗衣机","devtypeid":341,"devtypepid":332,"typename":"洗衣机"},
     * {"typename2":"走廊洗衣机","devtypeid":340,"devtypepid":332,"typename":"洗衣机"}]
     * error_code : 0
     * message : 获取成功
     */
    private List<DeciveInfo2> data;
    private String error_code;
    private String message;

    public void setData(List<DeciveInfo2> data) {
        this.data = data;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DeciveInfo2> getData() {
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
         * typename2 : 室内洗衣机
         * devtypeid : 341
         * devtypepid : 332
         * typename : 洗衣机
         */
        private String typename2;
        private int devtypeid;
        private int devtypepid;
        private String typename;

        public void setTypename2(String typename2) {
            this.typename2 = typename2;
        }

        public void setDevtypeid(int devtypeid) {
            this.devtypeid = devtypeid;
        }

        public void setDevtypepid(int devtypepid) {
            this.devtypepid = devtypepid;
        }

        public void setTypename(String typename) {
            this.typename = typename;
        }

        public String getTypename2() {
            return typename2;
        }

        public int getDevtypeid() {
            return devtypeid;
        }

        public int getDevtypepid() {
            return devtypepid;
        }

        public String getTypename() {
            return typename;
        }
    }
}
