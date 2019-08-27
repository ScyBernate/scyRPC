package ot.tch.rpc.provider.rpcServer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import ot.tch.rpc.provider.utils.SerializationUtil;

import java.util.List;

public class RpcDecoder extends ByteToMessageDecoder {

    private Class<?> generic;

    public RpcDecoder(Class<?> generic){
        this.generic = generic;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int readable = in.readableBytes(); //可读字节大小
        byte[] data = new byte[readable];
        in.readBytes(data);
        Object request = SerializationUtil.deserialize(data,generic);
        out.add(request);
    }
}
