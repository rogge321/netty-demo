package com.kuaishou.netty.handler;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

/**
 * @author zhengpanlong <zhengpanlong@kuaishou.com>
 * Created on 2020-07-09
 */
public class TimeClientHandler extends ChannelHandlerAdapter {
    private static final Logger logger = getLogger(TimeClientHandler.class);

    private final ByteBuf firstMessage;

    public TimeClientHandler() {

        byte[] req = ("request" + System.getProperty("line.separator")).getBytes();
        firstMessage = Unpooled.buffer(req.length);
        firstMessage.writeBytes(req);
        logger.info("...客户端发起请求...");
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.info("...exceptionCaught...");
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("...channelActive...");
        ctx.writeAndFlush("request" + System.getProperty("line.separator"));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        logger.info("...channelReadComplete...");
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            String body = (String) msg;
            logger.info("客户端收到服务端的时间 : " + body);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
}
