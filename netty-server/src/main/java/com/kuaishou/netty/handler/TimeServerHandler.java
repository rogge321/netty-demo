package com.kuaishou.netty.handler;

import static org.slf4j.LoggerFactory.getLogger;

import java.time.LocalDateTime;

import org.slf4j.Logger;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

/**
 * @author zhengpanlong <zhengpanlong@kuaishou.com>
 * Created on 2020-07-09
 */
public class TimeServerHandler  extends ChannelHandlerAdapter {
    private static final Logger logger = getLogger(TimeServerHandler.class);
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        String body = (String) msg;
        logger.info("服务端收到客户端的请求 : " + body);

        String dateStr = body.equalsIgnoreCase("request") ? LocalDateTime.now().toString() : "error  date time  request";

        ChannelFuture f = ctx.writeAndFlush(dateStr + System.getProperty("line.separator"));
        f.addListener(future -> {
            logger.info("发送完毕！");
            ctx.close();
        });
        f.addListener(ChannelFutureListener.CLOSE);
        ReferenceCountUtil.release(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        logger.info("...channelReadComplete...");
        ctx.flush();

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.info("...exceptionCaught...");
        ctx.close();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        logger.info("...channelRegistered...");
    }
}
