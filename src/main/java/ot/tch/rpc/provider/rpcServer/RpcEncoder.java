package ot.tch.rpc.provider.rpcServer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import ot.tch.rpc.provider.model.RpcRequest;
import ot.tch.rpc.provider.utils.SerializationUtil;

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
