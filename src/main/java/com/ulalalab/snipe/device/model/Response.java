package com.ulalalab.snipe.device.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ulalalab.snipe.infra.constant.ResultEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {

    private String code;
    private String message;
    private LocalDateTime timestamp;
    private T data;

    public Response(T data) {
        this.init();
        this.data = data;
    }

    public Response(String message) {
        this.init();
        this.message = message;
    }

    public Response(Code code, String message) {
        this.init();
        this.code = code.getCode();
        this.message = message;
    }

    private void init() {
        this.code = Code.SUCCESS.getCode();
        this.timestamp = LocalDateTime.now();
    }

    public enum Code {
        SUCCESS("000", "성공"),
        FAIL("999", "실패");

        private String code;
        private String desc;

        Code(String code, String desc) {
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
}
