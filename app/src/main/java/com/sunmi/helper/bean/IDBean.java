package com.sunmi.helper.bean;

/**
 * @Author lbr
 * 功能描述:身份证实体类
 * 创建时间: 2018-10-23 15:44
 */
public class IDBean {
    private byte[] image;
    private String cardnum;//身份证号码
    private String Sex;//性别
    private String Born;//出生日期
    private String Name;//姓名
    private String Address;//地址
    private String Nation;//民族

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getCardnum() {
        return cardnum;
    }

    public void setCardnum(String cardnum) {
        this.cardnum = cardnum;
    }

    public String getSex() {
        return Sex;
    }

    public void setSex(String sex) {
        Sex = sex;
    }

    public String getBorn() {
        return Born;
    }

    public void setBorn(String born) {
        Born = born;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getNation() {
        return Nation;
    }

    public void setNation(String nation) {
        Nation = nation;
    }


}
