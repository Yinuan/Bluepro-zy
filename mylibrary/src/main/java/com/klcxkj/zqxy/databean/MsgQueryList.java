package com.klcxkj.zqxy.databean;

import java.util.List;

/**
 * autor:OFFICE-ADMIN
 * time:2017/12/15
 * email:yinjuan@klcxkj.com
 * description:
 */

public class MsgQueryList {

    /**
     * data : [{"spreadPIC":"","spreadTitle":"yayayayaya","spreadURL":"sda",
     * "createrDT":null,"spreadContent":"666666"},{"spreadPIC":"dsfasffas","spreadTitle":"推广测试",
     * "spreadURL":"http://www.baidu.com","createrDT":null,"spreadContent":"等等等等等等"}]
     * error_code : 0
     * message : 获取成功
     */
    private List<MsgQuerySpread> data;
    private String error_code;
    private String message;

    public void setData(List<MsgQuerySpread> data) {
        this.data = data;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<MsgQuerySpread> getData() {
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
         * spreadPIC :
         * spreadTitle : yayayayaya
         * spreadURL : sda
         * createrDT : null
         * spreadContent : 666666
         */
        private String spreadPIC;
        private String spreadTitle;
        private String spreadURL;
        private String createrDT;
        private String spreadContent;

        public void setSpreadPIC(String spreadPIC) {
            this.spreadPIC = spreadPIC;
        }

        public void setSpreadTitle(String spreadTitle) {
            this.spreadTitle = spreadTitle;
        }

        public void setSpreadURL(String spreadURL) {
            this.spreadURL = spreadURL;
        }

        public void setCreaterDT(String createrDT) {
            this.createrDT = createrDT;
        }

        public void setSpreadContent(String spreadContent) {
            this.spreadContent = spreadContent;
        }

        public String getSpreadPIC() {
            return spreadPIC;
        }

        public String getSpreadTitle() {
            return spreadTitle;
        }

        public String getSpreadURL() {
            return spreadURL;
        }

        public String getCreaterDT() {
            return createrDT;
        }

        public String getSpreadContent() {
            return spreadContent;
        }
    }
}
