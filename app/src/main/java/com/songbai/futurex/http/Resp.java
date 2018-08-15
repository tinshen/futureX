package com.songbai.futurex.http;


public class Resp<T> {

    public interface Code {
        int IMAGE_AUTH_CODE_REQUIRED = 224; // 验证码请求过多 需要图片验证码
        int IMAGE_AUTH_CODE_TIMEOUT = 225;  // 图片验证码超时
        int IMAGE_AUTH_CODE_FAILED = 226;  // 图片验证码失败
        int CASH_PWD_NONE = 2112;  // 未设置资金密码
        int NEEDS_PRIMARY_CERTIFICATION = 3660;  // 需要初级认证
        int NEEDS_SENIOR_CERTIFICATION = 3661;  // 需要高级认证
        int NEEDS_MORE_DEAL_COUNT = 3662;  // 您的历史交易笔数未达到对方要求
        int NEEDS_FUND_PASSWORD = 3018; // 需要资金密码
    }

    private int code;
    private String msg;
    private T data;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public boolean isSuccess() {
        return code == 200;
    }

    public boolean isTokenExpired() {
        return code == 211 || code == 214; // token timeout || user not login
    }

    @Override
    public String toString() {
        return "Resp{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
