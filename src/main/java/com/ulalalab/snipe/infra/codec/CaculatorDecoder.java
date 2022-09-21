package com.ulalalab.snipe.infra.codec;

import com.ulalalab.snipe.infra.util.ScriptUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.script.*;
import java.util.List;

public class CaculatorDecoder extends ByteToMessageDecoder {

    private static final Logger logger = LoggerFactory.getLogger(CaculatorDecoder.class);
    private Invocable invocable;

    public CaculatorDecoder() {
        invocable = ScriptUtils.getInvocable("function add(a) {return (a+999);}");
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        // 계산식이 존재 하는지 DB 조회
        Double d = (Double) ScriptUtils.getValue(invocable, "add", 2.0);
        System.out.println("##@@ " + d);
    }

    public static void main(String[] args) {
        Invocable invocable = ScriptUtils.getInvocable("function add(a) {return (a+999);}");


    }
}

