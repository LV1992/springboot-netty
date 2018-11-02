package com.springboot.netty.handler;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 多线程共享,接收消息处理类（服务端）
 */
@Slf4j
@Component
////需要加@Sharable注解，如果不加的话会报错；
@ChannelHandler.Sharable
public class ClientChannelHandler extends ChannelHandlerAdapter {

    private String HEART_BEAT = "1";


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("有新的连接激活 : " + ctx.channel().remoteAddress() + " !");
//        ByteBuf byteBuf = Unpooled.copiedBuffer("send|||".getBytes());
//        ctx.channel().writeAndFlush(byteBuf);
//        ctx.channel().writeAndFlush("test send");
        super.channelActive(ctx);
    }


    /**
     * 监听服务端的事件
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if(idleStateEvent.state() == IdleState.WRITER_IDLE){
                log.info("已经十秒没有发送消息");
                //向服务端发送消息
                ctx.channel().writeAndFlush(HEART_BEAT).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("read msg : "+ JSON.toJSONString(msg));
        super.channelRead(ctx, msg);
    }


}
