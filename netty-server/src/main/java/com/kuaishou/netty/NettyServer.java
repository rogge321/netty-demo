package com.kuaishou.netty;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.kuaishou.netty.handler.TimeServerHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author zhengpanlong <zhengpanlong@kuaishou.com>
 * Created on 2020-07-09
 */
public class NettyServer {
    private static final Logger logger = getLogger(NettyServer.class);
    public void bind(int port) {

        // 配置 服务端的nio 线程组,  第一个线程组接收client端连接的
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        //实际的逻辑操作
        EventLoopGroup childGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(parentGroup, childGroup)
                    .channel(NioServerSocketChannel.class)//  指定nio 的模式
                    .childOption(ChannelOption.SO_KEEPALIVE, true) //是否启用心跳机制，保持连接。
                    .childOption(ChannelOption.TCP_NODELAY, true) //是否一有数据就马上发送。
                    .option(ChannelOption.SO_BACKLOG, 1024)//  设置 tcp 的参数.服务端处理线程全忙后，允许多少个新请求进入等待。
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new StringEncoder());
                            ch.pipeline().addLast(new LoggingHandler());
                            ch.pipeline().addLast(new TimeServerHandler());
                        }
                    });

            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
            logger.info("...服务端资源释放...");

        }
    }

    public static void main(String[] args) throws Exception {
        NettyServer nettyServer = new NettyServer();
        nettyServer.bind(9000);
    }

}
