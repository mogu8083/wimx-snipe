package com.ulalalab.snipe.infra.constant;

public enum ResultEnum {
    SUCCESS("000", "성공"),
    FAIL("999", "실패");

    private String code;
    private String desc;

    ResultEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
