package com.klcxkj.zqxy.response;

import java.io.Serializable;

/**
 * autor:OFFICE-ADMIN
 * time:2018/1/19
 * email:yinjuan@klcxkj.com
 * description:
 */

public class PublicGetDataDev implements Serializable{

    /**
     * data : {"DevID":4}
     * error_code : 0
     */
    private static final long serialVersionUID = 10L;
    public String message;
    public String error_message;
    public DeviceInfo2 data;
    public String error_code;




}
