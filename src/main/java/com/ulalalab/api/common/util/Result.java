//package com.ulalalab.api.common.util;
//
//import com.ulalalab.api.common.constant.StatusCode;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Getter
//@Setter
//public class Result<T> {
//
//    // 코드
//    private String code;
//
//    // 메세지
//    private String message;
//
//    // 요청 타임스탬프
//    private Long timestamp;
//
//    // 요청 데이트포맷
//    private String dateTime;
//
//    // 데이터
//    @JsonInclude(value = JsonInclude.Include.NON_NULL)
//    private T data;
//
//    // 데이터 갯수 (목록일 경우..)
//    @JsonInclude(value = JsonInclude.Include.NON_NULL)
//    private Integer listCount;
//
//
//    public Result(T data) {
//        if(data==null) {
//            this.code = StatusCode.ERROR_NULL.getStatus();
//            this.message = StatusCode.ERROR_NULL.getDesc();
//        } else {
//
//            // 성공
//            this.code = StatusCode.SUCCESS.getStatus();
//            this.message = StatusCode.SUCCESS.getDesc();
//            this.data = data;
//
//            if(data instanceof List) {
//                this.listCount = ((List<?>) data).size();
//            }
//        }
//    }
//
//    public Long getTimestamp() {
//        return LocalDateUtil.getTimestmp(LocalDateTime.now());
//    }
//
//    public String getDateTime() {
//        return LocalDateUtil.getLocalDateTimeString(LocalDateTime.now(), LocalDateUtil.DATE_TIME_FORMAT);
//    }
//}