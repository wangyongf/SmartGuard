package com.yongf.smartguard.domain;

/**
 * @author Scott Wang
 * @Description:
 * 黑名单号码的业务bean
 * @date 2016/2/11 21:02
 * @Project SmartGuard
 */
public class BlackListInfo {
    private String number;
    private String mode;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
