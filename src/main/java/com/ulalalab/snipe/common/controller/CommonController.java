//package com.ulalalab.snipe.common.controller;//package com.ulalalab.snipe.device.service;
//
//import com.ulalalab.snipe.common.service.CommonService;
//import com.ulalalab.snipe.device.model.Response;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@Slf4j(topic = "HTTP")
//@RestController
//@RequiredArgsConstructor
//public class CommonController {
//
//    private final CommonService commonService;
//
//    /**
//     * 서버 정보
//     */
//    @GetMapping("/server/info")
//    public Response serverInfo() throws Exception {
//        return commonService.serverInfo();
//    }
//
//    /**
//     * 서버 Perform GC
//     */
//    @GetMapping("/server/perform-gc")
//    public Response performGc() throws Exception {
//        return commonService.performGc();
//    }
//}