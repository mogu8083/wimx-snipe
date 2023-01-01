//package com.ulalalab.snipe.device.service;
//
//import com.ulalalab.snipe.device.model.Response;
//import io.netty.buffer.ByteBuf;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.util.Properties;
//
//@Slf4j
//@Service
//public class CommandService {
//
//    public void set(ByteBuf buffer, byte cmd) {
//
//        switch (cmd) {
//            case 0x00:
//                this.cmd0x00(buffer);
//                break;
//            case 0x01:
//                this.cmd0x01(buffer);
//                break;
//        }
//    }
//
//    private void getInit(ByteBuf buffer) {
//
//    }
//
//    private void cmd0x01(ByteBuf buffer) {
//
//    }
//}