package com.springboot.netty.client;


import com.springboot.netty.config.NettyConfig;
import com.springboot.netty.handler.ByteBufEncoderHandler;
import com.springboot.netty.handler.ClientChannelHandler;
import com.springboot.netty.model.Message;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NettyClient {


    @Autowired
    private ClientChannelHandler clientChannelHandler;

    @Autowired
    private NettyConfig nettyConfig;

    /**
     * Boss
     */
    EventLoopGroup boss = new NioEventLoopGroup();


    Bootstrap bootstrap = new Bootstrap();

    public void startClient(Message message) {
        try {
            bootstrap.group(boss).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) {
                            channel.pipeline()
                                    .addLast(new ByteBufEncoderHandler())
                                    .addLast(new IdleStateHandler(0, 10, 0))
                                    .addLast(clientChannelHandler);
                        }
                    });
            ChannelFuture future = bootstrap.connect("127.0.0.1", nettyConfig.getPort()).sync();
            if (future.isSuccess()) {
                log.info("启动 Netty 客户端 成功");
            }
            //发送消息
            SocketChannel channel = (SocketChannel) future.channel();
            channel.writeAndFlush(message);
            //等待关闭channel
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
