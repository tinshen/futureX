package com.songbai.futurex.websocket.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Modified by john on 2018/6/13
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class MarketData implements Parcelable{


    /**
     * highestPrice : 0.0
     * lowestPrice : 0.0
     * preSetPrice : 122.99
     * code : ltc_usdt
     * lastVolume : 0.0
     * openPrice : 122.99
     * preClsPrice : 122.99
     * upDropSpeed : 0.0
     * volume : 0.0
     * upTime : 1528855920000
     * upDropPrice : 0.0
     * tradeDay : 2018-06-13
     * upTimeFormat : 2018-06-13 10:12:00
     * turnover : 0.0
     * lastPrice : 122.99
     * status : 1
     */

    private double highestPrice;
    private double lowestPrice;
    private double preSetPrice;
    private String code;
    private double lastVolume;
    private double openPrice;
    private double preClsPrice;
    private double upDropSpeed; // 涨跌幅
    private double volume;
    private long upTime;
    private double upDropPrice; // 涨跌值
    private String tradeDay;
    private String upTimeFormat;
    private double turnover;
    private double lastPrice;
    private int status;

    protected MarketData(Parcel in) {
        highestPrice = in.readDouble();
        lowestPrice = in.readDouble();
        preSetPrice = in.readDouble();
        code = in.readString();
        lastVolume = in.readDouble();
        openPrice = in.readDouble();
        preClsPrice = in.readDouble();
        upDropSpeed = in.readDouble();
        volume = in.readDouble();
        upTime = in.readLong();
        upDropPrice = in.readDouble();
        tradeDay = in.readString();
        upTimeFormat = in.readString();
        turnover = in.readDouble();
        lastPrice = in.readDouble();
        status = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(highestPrice);
        dest.writeDouble(lowestPrice);
        dest.writeDouble(preSetPrice);
        dest.writeString(code);
        dest.writeDouble(lastVolume);
        dest.writeDouble(openPrice);
        dest.writeDouble(preClsPrice);
        dest.writeDouble(upDropSpeed);
        dest.writeDouble(volume);
        dest.writeLong(upTime);
        dest.writeDouble(upDropPrice);
        dest.writeString(tradeDay);
        dest.writeString(upTimeFormat);
        dest.writeDouble(turnover);
        dest.writeDouble(lastPrice);
        dest.writeInt(status);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MarketData> CREATOR = new Creator<MarketData>() {
        @Override
        public MarketData createFromParcel(Parcel in) {
            return new MarketData(in);
        }

        @Override
        public MarketData[] newArray(int size) {
            return new MarketData[size];
        }
    };

    public double getHighestPrice() {
        return highestPrice;
    }

    public void setHighestPrice(double highestPrice) {
        this.highestPrice = highestPrice;
    }

    public double getLowestPrice() {
        return lowestPrice;
    }

    public void setLowestPrice(double lowestPrice) {
        this.lowestPrice = lowestPrice;
    }

    public double getPreSetPrice() {
        return preSetPrice;
    }

    public void setPreSetPrice(double preSetPrice) {
        this.preSetPrice = preSetPrice;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getLastVolume() {
        return lastVolume;
    }

    public void setLastVolume(double lastVolume) {
        this.lastVolume = lastVolume;
    }

    public double getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(double openPrice) {
        this.openPrice = openPrice;
    }

    public double getPreClsPrice() {
        return preClsPrice;
    }

    public void setPreClsPrice(double preClsPrice) {
        this.preClsPrice = preClsPrice;
    }

    public double getUpDropSpeed() {
        return upDropSpeed;
    }

    public void setUpDropSpeed(double upDropSpeed) {
        this.upDropSpeed = upDropSpeed;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public long getUpTime() {
        return upTime;
    }

    public void setUpTime(long upTime) {
        this.upTime = upTime;
    }

    public double getUpDropPrice() {
        return upDropPrice;
    }

    public void setUpDropPrice(double upDropPrice) {
        this.upDropPrice = upDropPrice;
    }

    public String getTradeDay() {
        return tradeDay;
    }

    public void setTradeDay(String tradeDay) {
        this.tradeDay = tradeDay;
    }

    public String getUpTimeFormat() {
        return upTimeFormat;
    }

    public void setUpTimeFormat(String upTimeFormat) {
        this.upTimeFormat = upTimeFormat;
    }

    public double getTurnover() {
        return turnover;
    }

    public void setTurnover(double turnover) {
        this.turnover = turnover;
    }

    public double getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(double lastPrice) {
        this.lastPrice = lastPrice;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
