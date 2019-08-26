package ot.tch.rpc.serviceRegistry;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ServiceRegistry {

    private static Logger logger = LoggerFactory.getLogger(ServiceRegistry.class);

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    private String connectUrl;

    public ZooKeeper connectZk(){
        try {
            ZooKeeper zk = new ZooKeeper(connectUrl, 5000, new Watcher() {
                public void process(WatchedEvent event) {
                    countDownLatch.countDown();
                    logger.info("zookeeper 连接成功...");
                }
            });
            countDownLatch.await();
            return zk;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void setConnectUrl(String connectUrl) {
        this.connectUrl = connectUrl;
    }
}
