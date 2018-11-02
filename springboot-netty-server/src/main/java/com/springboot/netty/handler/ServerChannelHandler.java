package com.springboot.netty.handler;

import com.alibaba.fastjson.JSON;
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
//需要加@Sharable注解，如果不加的话会报错；
@ChannelHandler.Sharable
public class ServerChannelHandler extends ChannelHandlerAdapter {

    private String HEART_BEAT = "1";


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("RemoteAddress : " + ctx.channel().remoteAddress() + " active !");
//        ctx.channel().writeAndFlush("connect success");
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
            if(idleStateEvent.state() == IdleState.READER_IDLE){
                log.info("已经十秒没有收到消息");
                //向客户端发送消息
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
//        ctx.channel().writeAndFlush(Message.build().setId(2).setMsg("response"));
        super.channelRead(ctx, msg);
    }


}
