package ot.tch.rpc.provider.rpcServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import ot.tch.rpc.provider.model.RpcRequest;
import ot.tch.rpc.provider.model.RpcResponse;

import java.lang.reflect.Method;
import java.util.Map;

@Slf4j
public class RpcHandler extends ChannelInboundHandlerAdapter {

    private Map<String,Class<?>> serviceMap; //接口名称  ->  接口实现类的class

    public RpcHandler(Map<String,Class<?>> serviceMap){
        this.serviceMap = serviceMap;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        RpcResponse response = new RpcResponse();
        try {
            if(msg instanceof RpcRequest){
                RpcRequest request = (RpcRequest)msg;
                Object result = handleRequest(request);
                response.setResult(result);
                response.setRequestId(request.getRequestId());
            }
        } catch (Exception e) {
            //e.printStackTrace();
            response.setException(e);
        }
        ctx.writeAndFlush(response);
    }

    private Object handleRequest(RpcRequest request) throws Exception{
        String className = request.getClassName();
        String methodName = request.getMethodName();
        Class<?>[] parameterType = request.getParameterType();
        Object[] parameter = request.getParameter();
        Class<?> targetClass = serviceMap.get(className);
        if(targetClass==null){
            throw new ClassNotFoundException("找不到"+className);
        }
        Object obj = targetClass.newInstance();
        Method method = targetClass.getMethod(methodName,parameterType);
        if(method!=null){
            return method.invoke(obj,parameter);
        }else{
            throw new NoSuchMethodException("找不到"+methodName);
        }
    }
}
