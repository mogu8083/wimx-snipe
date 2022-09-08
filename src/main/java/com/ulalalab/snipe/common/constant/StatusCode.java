package com.ulalalab.snipe.common.constant;

public enum StatusCode {
    SUCCESS("000", "Success"),
    ERROR_NULL("911", "데이터가 Null 입니다."),
    ERROR("999", "데이터가 오류");

    private String status;
    private String desc;

    StatusCode(String status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public String getStatus() {
        return this.status;
    }

    public String getDesc() {
        return this.desc;
    }
}
