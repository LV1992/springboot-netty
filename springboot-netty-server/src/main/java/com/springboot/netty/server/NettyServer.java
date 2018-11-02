package com.springboot.netty.server;

import com.springboot.netty.config.NettyConfig;
import com.springboot.netty.handler.ByteBufDecoderHandler;
import com.springboot.netty.handler.ServerChannelHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.nio.charset.Charset;

@Slf4j
@Component
public class NettyServer {

    /**
     * 创建bootstrap
     */
    ServerBootstrap serverBootstrap = new ServerBootstrap();

    /**
     * Boss
     */
    EventLoopGroup boss = new NioEventLoopGroup();

    /**
     * Worker
     */
    EventLoopGroup work = new NioEventLoopGroup();

    @Autowired
    private ServerChannelHandler serverChannelHandler;

    @Autowired
    private NettyConfig nettyConfig;

    /**
     * 关闭服务器的方法
     */
    @PreDestroy
    public void close() {
        log.info("关闭服务器...");
        //优雅的退出
        boss.shutdownGracefully();
        work.shutdownGracefully();
    }

    @PostConstruct
    public void start() {
        int port = nettyConfig.getPort();
        try {
            serverBootstrap.group(boss, work).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100).handler(new LoggingHandler(LogLevel.INFO))
                    //保持长连接
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline()
//                            pipeline.addLast(
//                                    new LengthFieldBasedFrameDecoder(nettyConfig.getMaxFrameLength(), 0, 2, 0, 2));
//                            pipeline.addLast(new LengthFieldPrepender(2));
                            .addLast(new ByteBufDecoderHandler())
                            //10秒没收到消息 则将IdleStateHandler 添加到 ChannelPipeline 中
                            .addLast(new IdleStateHandler(10, 0, 0))
                            //业务处理类
                            .addLast(serverChannelHandler);
                        }
                    });

            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            if (channelFuture.isSuccess()) {
                log.info("netty 服务器在[{}]端口启动监听", port);
            }
            //阻塞防止退出
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("出现异常释放资源");
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }
    }

}
