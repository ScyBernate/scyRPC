package ot.tch.rpc.common.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import ot.tch.rpc.common.model.RpcRequest;
import ot.tch.rpc.common.utils.SerializationUtil;

public class RpcEncoder extends MessageToByteEncoder {

    private Class<?> generic;

    public RpcEncoder(Class<?> generic){
        this.generic = generic;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) {
        if(generic.isInstance(msg)){
            byte[] bytes = SerializationUtil.serialize(msg);
            out.writeBytes(bytes);
        }
    }


    public static void main(String[] args) {
        Class clazz = RpcRequest.class;
        System.out.println(clazz.isInstance(new RpcRequest()));
    }
}
