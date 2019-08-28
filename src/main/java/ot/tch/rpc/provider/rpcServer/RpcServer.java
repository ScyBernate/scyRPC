package ot.tch.rpc.provider.rpcServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import ot.tch.rpc.common.netty.RpcDecoder;
import ot.tch.rpc.common.netty.RpcEncoder;
import ot.tch.rpc.provider.annotation.RPC;
import ot.tch.rpc.common.model.RpcRequest;
import ot.tch.rpc.common.model.RpcResponse;
import ot.tch.rpc.provider.serviceRegistry.ServiceRegistry;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RpcServer implements ApplicationContextAware, InitializingBean {

    //存放所有提供服务的实体类
    public static final Map<String, Class<?>> serviceMap = new HashMap<>();

    private String address;

    private ServiceRegistry serviceRegistry;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> beanNamesForAnnotation = applicationContext.getBeansWithAnnotation(RPC.class);
        for(Map.Entry<String,Object> index:beanNamesForAnnotation.entrySet()){
            Object val = index.getValue();
            String interfaceName = val.getClass().getAnnotation(RPC.class).name(); //获取被实现的接口
            serviceMap.put(interfaceName,val.getClass());
        }
        if(serviceMap.size()==0){
            log.error("未找到服务方法...");
        }
    }

    /**
     * netty服务启动会堵塞住当前线程的，保证该方法执行在setApplicationContext之后
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler())
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast("RpcDecoder",new RpcDecoder(RpcRequest.class));
                            pipeline.addLast("RpcEncoder",new RpcEncoder(RpcResponse.class));
                            pipeline.addLast(new RpcHandler(serviceMap));
                        }
                    });
            String[] url_port = address.split(":");
            ChannelFuture future = b.bind(Integer.parseInt(url_port[1])).sync();
            if(future.isSuccess()){
                log.info("netty服务已经启动,端口：" + url_port[1] + ".");
            }
            if(serviceRegistry!=null){//向zk注册该服务节点地址
                serviceRegistry.register(address);
            }
            future.channel().closeFuture().sync();
        }catch(InterruptedException e){
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry){
        this.serviceRegistry = serviceRegistry;
    }


}
