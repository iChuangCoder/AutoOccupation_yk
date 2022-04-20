package com.example.autooccupation.bean;

import java.util.Objects;

/**
 * @Author iCHuang
 * @Date 2022/4/11 21:56
 */
public class AutoBean {
    private String sqid;
    private String cookie;
    private String seatid;
    private Boolean enable;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getSqid() {
        return sqid;
    }

    public void setSqid(String sqid) {
        this.sqid = sqid;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getSeatid() {
        return seatid;
    }

    public void setSeatid(String seatid) {
        this.seatid = seatid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AutoBean autoBean = (AutoBean) o;
        return Objects.equals(sqid, autoBean.sqid);
    }

    @Override
    public int hashCode() {
        return 3324123;
    }

}
