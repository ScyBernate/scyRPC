package ot.tch.rpc.provider.serviceRegistry;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import ot.tch.rpc.provider.Global;

import java.io.IOException;
import java.nio.file.WatchEvent;
import java.util.concurrent.CountDownLatch;

/**
 * 服务通过zookeeper注册临时节点实现
 */
@Slf4j
public class ServiceRegistry {

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    private String address;

    //注册服务
    public void register(String path){
        ZooKeeper zooKeeper = connectZk();
        if (zooKeeper != null) {
            createNode(zooKeeper, path);
        }
    }

    //连接zk
    public ZooKeeper connectZk(){
        try {
            ZooKeeper zk = new ZooKeeper(address, 10000, new Watcher() {
                public void process(WatchedEvent event) {
                    if(event.getState()== Event.KeeperState.SyncConnected){
                        countDownLatch.countDown();
                        log.info("zookeeper 连接成功...");
                    }
                }
            });
            countDownLatch.await();
            return zk;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void createNode(ZooKeeper zooKeeper, String url){
        try {
            if(zooKeeper!=null){
                byte[] bytes = url.getBytes();
                //创建临时有序的节点
                String path = zooKeeper.create(Global.REGISTER_PATH, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
                log.info("create zookeeper node ({} => {})", path, url);
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public static void main(String[] args) {
        ServiceRegistry sr = new ServiceRegistry();
        sr.setAddress("106.12.78.128:2181");
        ZooKeeper zooo = sr.connectZk();
        sr.createNode(zooo,"firstRegister");
    }
}
