package com.klcxkj.zqxy.databean;

import java.util.List;

/**
 * autor:OFFICE-ADMIN
 * time:2017/12/17
 * email:yinjuan@klcxkj.com
 * description:
 */

public class MsgPushResult {

    /**
     * data : [{"pushcontent":"<p>11111111<br><\/p>","pushURL":"","contentTxt":"11111111","messageID":8,
     * "pushtitle":"111","pushDT":"2017-12-16 16:42:33.0"},{"pushcontent":"<p>11111111<br><\/p>",
     * "pushURL":"","contentTxt":"11111111","messageID":7,"pushtitle":"111","pushDT":"2017-12-16
     * 16:42:26.0"},{"pushcontent":"<p>撒旦法发生发生发撒旦法撒发撒点发射得分<\/p><p>&nbsp;&nbsp;&nbsp;&nbsp;&n
     * bsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp; 法国人法国华人社团<span style=\"background-col
     * or: rgb(255, 255, 0);\">和瑞士人的身体还是人祸<\/span><\/p><p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp; 的vasdfasfasfasf<b>aefaewfewrfrewgrggwrgrea<\/b><span style=\
     * "background-color: rgb(255, 255, 0);\"><br><\/span><br><\/p>","pushURL":"","contentTxt":"撒旦法发生发生发撒旦法撒发撒
     * 点发射得分法国人法国华人社团和瑞士人的身体还是人祸的vasdfasfasfasfaefaewfewrfrewgrggwrgrea","messageID":6,"pushtitle":"一个
     * 测试标题","pushDT":"2017-12-16 14:31:17.0"},{"pushcontent":"<h3><span style=\"background-color: rgb(255, 255, 0);\"
     * ><b>汇演安排<\/b><br><\/span><\/h3><h4><span style=\"background-color: rgb(255, 255, 0);\">每个班级请安排好时间到礼堂进行彩排
     * ，预定时间2017-12-20.<\/span><br><\/h4>","pushURL":"","contentTxt":"汇演安排每个班级请安排好时间到礼堂进行彩排，预定时间2017-12-20.
     * ","messageID":5,"pushtitle":"元旦汇演","pushDT":"2017-12-16 12:00:05.0"},{"pushcontent":"<h3><span style=\"background-colo
     * r: rgb(255, 255, 0);\"><b>汇演安排<\/b><br><\/span><\/h3><h4><span style=\"background-color: rgb(255, 255, 0);\">每个班级请
     * 安排好时间到礼堂进行彩排，预定时间2017-12-20.<\/span><br><\/h4>","pushURL":"","contentTxt":"汇演安排每个班级请安排好
     * 时间到礼堂进行彩排，预定时间2017-12-20.","messageID":4,"pushtitle":"元旦汇演","pushDT":"2017-12-16 11:54:26.0"},{
     * "pushcontent":"<h3><span style=\"background-color: rgb(255, 255, 0);\"><b>汇演安排<\/b><br><\/span><\/h3><h4><
     * span style=\"background-color: rgb(255, 255, 0);\">每个班级请安排好时间到礼堂进行彩排，预定时间2017-12-20.<
     * \/span><br><\/h4>","pushURL":"","contentTxt":"汇演安排每个班级请安排好时间到礼堂进行彩排，预定时间2017-12-20.","m
     * essageID":3,"pushtitle":"元旦汇演","pushDT":"2017-12-16 11:54:12.0"},{"pushcontent":"<h3><span style=\"bac
     * kground-color: rgb(255, 255, 0);\"><b>汇演安排<\/b><br><\/span><\/h3><h4><span style=\"background-color: rgb(
     * 255, 255, 0);\">每个班级请安排好时间到礼堂进行彩排，预定时间2017-12-20.<\/span><br><\/h4>","pushURL":"","contentTxt":
     * "汇演安排每个班级请安排好时间到礼堂进行彩排，预定时间2017-12-20.","messageID":2,"pushtitle":"元旦汇演","pushDT":"2017-12-1
     * 6 11:54:09.0"},{"pushcontent":"<p>efefefewewe<\/p>","pushURL":"","contentTxt":"efefefewewe","messageID":1,"pu
     * shtitle":"werwe","pushDT":"2017-12-16 11:06:37.0"}]
     * error_code : 0
     *
     */
    private List<MsgPush> data;
    private String error_code;

    public void setData(List<MsgPush> data) {
        this.data = data;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public List<MsgPush> getData() {
        return data;
    }

    public String getError_code() {
        return error_code;
    }

    public class DataEntity {
        /**
         * pushcontent : <p>11111111<br></p>
         * pushURL :
         * contentTxt : 11111111
         * messageID : 8
         * pushtitle : 111
         * pushDT : 2017-12-16 16:42:33.0
         */
        private String pushcontent;
        private String pushURL;
        private String contentTxt;
        private int messageID;
        private String pushtitle;
        private String pushDT;

        public void setPushcontent(String pushcontent) {
            this.pushcontent = pushcontent;
        }

        public void setPushURL(String pushURL) {
            this.pushURL = pushURL;
        }

        public void setContentTxt(String contentTxt) {
            this.contentTxt = contentTxt;
        }

        public void setMessageID(int messageID) {
            this.messageID = messageID;
        }

        public void setPushtitle(String pushtitle) {
            this.pushtitle = pushtitle;
        }

        public void setPushDT(String pushDT) {
            this.pushDT = pushDT;
        }

        public String getPushcontent() {
            return pushcontent;
        }

        public String getPushURL() {
            return pushURL;
        }

        public String getContentTxt() {
            return contentTxt;
        }

        public int getMessageID() {
            return messageID;
        }

        public String getPushtitle() {
            return pushtitle;
        }

        public String getPushDT() {
            return pushDT;
        }
    }
}
