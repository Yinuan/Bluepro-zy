package com.klcxkj.zqxy.viewpager;


public class CardItem {

    private String text1;
    private String text2;
    private String text3;
    private int type;

    public CardItem(String text1, String text2, String text3) {
        this.text1 = text1;
        this.text2 = text2;
        this.text3 = text3;
    }

    public CardItem(String text1, String text2, String text3, int type) {
        this.text1 = text1;
        this.text2 = text2;
        this.text3 = text3;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getText1() {
        return text1;
    }

    public void setText1(String text1) {
        this.text1 = text1;
    }

    public String getText2() {
        return text2;
    }

    public void setText2(String text2) {
        this.text2 = text2;
    }

    public String getText3() {
        return text3;
    }

    public void setText3(String text3) {
        this.text3 = text3;
    }
}
