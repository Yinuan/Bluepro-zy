package com.klcxkj.zqxy.databean;

import java.util.List;

/**
 * autor:OFFICE-ADMIN
 * time:2017/12/4
 * email:yinjuan@klcxkj.com
 * description:设备类型（大类）
 */

public class DeciveTypeResult {

    /**
     * data : [{"typeid":333,"devname":"吹风机"},{"typeid":2,"devname":"开水器"},
     * {"typeid":332,"devname":"洗衣机"},{"typeid":1,"devname":"热水表"}]
     * error_code : 0
     * message : 获取成功
     */
    private List<DeciveType> data;
    private String error_code;
    private String message;

    public void setData(List<DeciveType> data) {
        this.data = data;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DeciveType> getData() {
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
         * typeid : 333
         * devname : 吹风机
         */
        private int typeid;
        private String devname;

        public void setTypeid(int typeid) {
            this.typeid = typeid;
        }

        public void setDevname(String devname) {
            this.devname = devname;
        }

        public int getTypeid() {
            return typeid;
        }

        public String getDevname() {
            return devname;
        }
    }
}
