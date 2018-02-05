package com.klcxkj.zqxy.databean;

import java.util.List;

/**
 * autor:OFFICE-ADMIN
 * time:2017/12/25
 * email:yinjuan@klcxkj.com
 * description:
 */

public class Recghangebean {

    /**
     * data : [{"czprjid":"1","czid":120,"czvalue":"50"},
     * {"czprjid":"1","czid":121,"czvalue":"100"},
     * {"czprjid":"1","czid":122,"czvalue":"500"},{"czprjid":"1","czid":123,"czvalue":"200"}]
     * error_code : 0
     * message : 获取成功
     */
    private List<DataEntity> data;
    private String error_code;
    private String message;

    public void setData(List<DataEntity> data) {
        this.data = data;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DataEntity> getData() {
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
         * czprjid : 1
         * czid : 120
         * czvalue : 50
         */
        private String czprjid;
        private int czid;
        private String czvalue;

        public void setCzprjid(String czprjid) {
            this.czprjid = czprjid;
        }

        public void setCzid(int czid) {
            this.czid = czid;
        }

        public void setCzvalue(String czvalue) {
            this.czvalue = czvalue;
        }

        public String getCzprjid() {
            return czprjid;
        }

        public int getCzid() {
            return czid;
        }

        public String getCzvalue() {
            return czvalue;
        }
    }
}
