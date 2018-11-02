package com.springboot.netty.handler;

import com.alibaba.fastjson.JSON;
import com.springboot.netty.model.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 字节编码器,将 Message -> ByteBuf
 */
public class ByteBufEncoderHandler extends MessageToByteEncoder<Message> {


    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, ByteBuf byteBuf) throws Exception {
        if(message == null){
            throw new  Exception("the encode message is null");
        }
        ByteBuf buf = Unpooled.copiedBuffer(JSON.toJSONString(message).getBytes());
        byteBuf.writeBytes(buf);
    }
}
