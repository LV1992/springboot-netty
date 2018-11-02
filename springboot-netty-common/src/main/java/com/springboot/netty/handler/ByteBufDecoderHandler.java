package com.springboot.netty.handler;

import com.alibaba.fastjson.JSON;
import com.springboot.netty.model.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.nio.charset.Charset;
import java.util.List;

/**
 * 字节解码器,将 ByteBuf -> Message
 */
public class ByteBufDecoderHandler extends MessageToMessageDecoder<ByteBuf> {

    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List list) throws Exception {
        String string = byteBuf.toString(Charset.forName("utf-8"));
        Message message = JSON.parseObject(string, Message.class);
        list.add(message);
    }
}
