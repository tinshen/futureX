package com.songbai.futurex.model.mine;

/**
 * @author yangguangda
 * @date 2018/8/3
 */
public class InviteAwardHistory {

    /**
     * coinType : bfb
     * createTime : 1532421242000
     * value : 4.00000000
     */

    private String coinType;
    private long createTime;
    private String svalue;
    private String wid;

    public String getCoinType() {
        return coinType;
    }

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getValue() {
        return svalue;
    }

    public void setValue(String value) {
        this.svalue = value;
    }

    public String getWid() {
        return wid;
    }

    public void setWid(String wid) {
        this.wid = wid;
    }
}
