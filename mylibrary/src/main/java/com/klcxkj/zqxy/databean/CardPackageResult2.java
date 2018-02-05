package com.klcxkj.zqxy.databean;

import java.util.List;

/**
 * autor:OFFICE-ADMIN
 * time:2017/12/6
 * email:yinjuan@klcxkj.com
 * description:
 */

public class CardPackageResult2 {
    private List<CardPackageType> data;
    private String error_code;
    private String message;

    public List<CardPackageType> getData() {
        return data;
    }

    public void setData(List<CardPackageType> data) {
        this.data = data;
    }

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
