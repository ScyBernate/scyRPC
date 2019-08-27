package ot.tch.rpc.consumer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import ot.tch.rpc.consumer.proxy.RpcProxy;
import ot.tch.rpc.consumer.serviceDiscovery.ServiceDiscovery;
import ot.tch.rpc.provider.model.RpcRequest;
import ot.tch.rpc.provider.model.RpcResponse;
import ot.tch.rpc.provider.rpcServer.RpcDecoder;
import ot.tch.rpc.provider.rpcServer.RpcEncoder;
import ot.tch.rpc.provider.service.CountService;

public class RpcClient extends SimpleChannelInboundHandler<RpcResponse>{

    private String host;

    private int port;

    private RpcResponse response;

    private Object lock = new Object();

    private ChannelHandlerContext ctx;

    public RpcClient(String host, String port){
        this.host = host;
        this.port = Integer.parseInt(port);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        response = msg;
        synchronized (lock){
            lock.notifyAll();  //唤醒所有等待的线程
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }


    //发送
    public RpcResponse send(RpcRequest request){
        EventLoopGroup worker = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(worker);
        b.channel(NioSocketChannel.class);
        b.remoteAddress(host,port);
        b.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline().addLast(new RpcEncoder(RpcRequest.class));
                ch.pipeline().addLast(new RpcDecoder(RpcResponse.class));
                ch.pipeline().addLast(RpcClient.this);
            }
        });
        try {
            ChannelFuture f = b.connect(host, port).sync();
            f.channel().writeAndFlush(request).sync();
            synchronized (lock){ //锁住当前线程，等待返回结果
                lock.wait();
            }
            if(response!=null){
               f.channel().close();
            }
            return response;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            worker.shutdownGracefully();
        }
        return null;
    }

    public static void main(String[] args) {
        ServiceDiscovery serviceDiscovery = new ServiceDiscovery();
        serviceDiscovery.setAddress("106.12.78.128:2181");
        RpcProxy proxy = new RpcProxy();
        proxy.setServiceDiscovery(serviceDiscovery);
        CountService service = proxy.createByJdk(CountService.class);
        System.out.println(service.count(3,4));

    }

}
