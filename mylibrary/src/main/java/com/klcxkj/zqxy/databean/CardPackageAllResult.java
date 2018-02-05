package com.klcxkj.zqxy.databean;

import java.util.List;

/**
 * autor:OFFICE-ADMIN
 * time:2017/12/6
 * email:yinjuan@klcxkj.com
 * description:
 */

public class CardPackageAllResult {

    /**
     * data : [{"descname":"8次/月","typevalue":"30元","typeid":20,"typename":"洗衣机月卡","PrjID":"1"},
     * {"descname":"10次/月","typevalue":"40元","typeid":21,"typename":"洗衣机月卡","PrjID":"1"},
     * {"descname":"13次/月","typevalue":"50元","typeid":22,"typename":"洗衣机月卡","PrjID":"1"},
     * {"descname":"15次/月","typevalue":"55元","typeid":23,"typename":"洗衣机月卡","PrjID":"1"},
     * {"descname":"10次/月","typevalue":"40元","typeid":349,"typename":"洗衣机月卡","PrjID":"1"}]
     * error_code : 0
     * message : 获取成功
     */
    private List<CardPackageAll> data;
    private String error_code;
    private String message;

    public void setData(List<CardPackageAll> data) {
        this.data = data;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<CardPackageAll> getData() {
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
         * descname : 8次/月
         * typevalue : 30元
         * typeid : 20
         * typename : 洗衣机月卡
         * PrjID : 1
         */
        private String descname;
        private String typevalue;
        private int typeid;
        private String typename;
        private String PrjID;

        public void setDescname(String descname) {
            this.descname = descname;
        }

        public void setTypevalue(String typevalue) {
            this.typevalue = typevalue;
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

        public String getDescname() {
            return descname;
        }

        public String getTypevalue() {
            return typevalue;
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
    }
}
