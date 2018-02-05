package com.klcxkj.zqxy.databean;

import java.io.Serializable;

/**
 * autor:OFFICE-ADMIN
 * time:2017/12/17
 * email:yinjuan@klcxkj.com
 * description:消息中心的消息
 */

public class MsgPush implements Serializable{

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
