package com.klcxkj.zqxy.databean;

/**
 * autor:OFFICE-ADMIN
 * time:2017/12/8
 * email:yinjuan@klcxkj.com
 * description:
 */

public class CardPackageData {
    private CardPackageAll cardPackageAll;
    private int chosed;

    public CardPackageData(CardPackageAll cardPackageAll, int chosed) {
        this.cardPackageAll = cardPackageAll;
        this.chosed = chosed;
    }

    public CardPackageAll getCardPackageAll() {
        return cardPackageAll;
    }

    public void setCardPackageAll(CardPackageAll cardPackageAll) {
        this.cardPackageAll = cardPackageAll;
    }

    public int getChosed() {
        return chosed;
    }

    public void setChosed(int chosed) {
        this.chosed = chosed;
    }
}
