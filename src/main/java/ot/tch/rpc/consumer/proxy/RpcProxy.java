package ot.tch.rpc.consumer.proxy;

import org.springframework.util.StringUtils;
import ot.tch.rpc.consumer.RpcClient;
import ot.tch.rpc.consumer.serviceDiscovery.ServiceDiscovery;
import ot.tch.rpc.provider.model.RpcRequest;
import ot.tch.rpc.provider.model.RpcResponse;
import ot.tch.rpc.provider.service.HelloService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.UUID;

public class RpcProxy {

    private ServiceDiscovery serviceDiscovery;

    public <T>T createByJdk(Class<?> interfaceClass){
        return (T)Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //构建请求参数
                RpcRequest request = new RpcRequest();
                request.setRequestId(UUID.randomUUID().toString());
                request.setClassName(interfaceClass.getSimpleName());
                request.setMethodName(method.getName());
                request.setParameterType(method.getParameterTypes());
                request.setParameter(args);

                String address = serviceDiscovery.discovery(); //查找可用服务节点
                if(StringUtils.isEmpty(address)){
                    throw new Exception("无可用服务");
                }
                String[] serviceAddress = address.split(":");
                RpcClient client = new RpcClient(serviceAddress[0],serviceAddress[1]);
                RpcResponse response = client.send(request);
                if(response.hasError()){
                    throw response.getException();
                }else{
                    return response.getResult();
                }
            }
        });
    }

    public void setServiceDiscovery(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

}
