package com.kuaishou.netty;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.kuaishou.netty.handler.TimeClientHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author zhengpanlong <zhengpanlong@kuaishou.com>
 * Created on 2020-07-09
 */
public class NettyClient {
    private static final Logger logger = getLogger(NettyClient.class);
    private Channel channel = null;

    public void connect(int port, String host) throws Exception {

        // 配置客户端的nio 线程组
        EventLoopGroup group = new NioEventLoopGroup();
        try {

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)  //  指定nio 的模式
                    .option(ChannelOption.TCP_NODELAY, true)//设置tcp  参数
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new LineBasedFrameDecoder(1024));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new StringEncoder());
                            ch.pipeline().addLast(new LoggingHandler());
                            ch.pipeline().addLast(new TimeClientHandler());
                        }
                    });
            //发起异步连接操作
            ChannelFuture connect = bootstrap.connect(host, port).sync();
            channel = connect.channel();
            //  等待客户端链路关闭
            channel.closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //  优雅的退出
            group.shutdownGracefully();
            logger.info(".... 优雅的退出....");
        }

    }

    public static void main(String[] args) throws Exception {
        NettyClient nettyClient = new NettyClient();
        nettyClient.connect(9000, "127.0.0.1");
    }
}
