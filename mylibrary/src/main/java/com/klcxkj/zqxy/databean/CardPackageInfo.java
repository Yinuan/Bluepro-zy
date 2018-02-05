package com.klcxkj.zqxy.databean;

/**
 * autor:OFFICE-ADMIN
 * time:2017/11/8
 * email:yinjuan@klcxkj.com
 * description:卡片套餐信息
 */

public class CardPackageInfo {

    private String monney;
    private String count;
    private int chosed;

    public CardPackageInfo(String monney, String count, int chosed) {
        this.monney = monney;
        this.count = count;
        this.chosed = chosed;
    }

    public String getMonney() {
        return monney;
    }

    public void setMonney(String monney) {
        this.monney = monney;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public int getChosed() {
        return chosed;
    }

    public void setChosed(int chosed) {
        this.chosed = chosed;
    }
}
