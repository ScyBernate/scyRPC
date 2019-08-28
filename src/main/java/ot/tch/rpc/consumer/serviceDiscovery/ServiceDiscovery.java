package ot.tch.rpc.consumer.serviceDiscovery;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import ot.tch.rpc.provider.Global;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 监听zk服务节点
 */
@Slf4j
public class ServiceDiscovery {

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    //服务节点
    private volatile List<String> serverNodes = new ArrayList<>();

    private String address;

    public ServiceDiscovery(String address) {
        this.address = address;
        ZooKeeper zk = connectZk();
        if (zk != null) {
            queryNodes(zk);
        }
    }

    //查询可用节点的第一个
    public String discovery() {
        if (serverNodes.size() == 1) {
            return serverNodes.get(0);
        } else {
            return serverNodes.get(ThreadLocalRandom.current().nextInt(serverNodes.size()));
        }
    }

    //连接zk
    public ZooKeeper connectZk() {
        try {
            ZooKeeper zk = new ZooKeeper(address, 10000, new Watcher() {
                public void process(WatchedEvent event) {
                    if (event.getState() == Event.KeeperState.SyncConnected) {
                        countDownLatch.countDown();
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

    //查找服务根节点下的子节点列表
    private void queryNodes(ZooKeeper zooKeeper) {
        try {
            if (zooKeeper != null) {
                //获取子节点的名称
                List<String> nodelist = zooKeeper.getChildren(Global.REGISTER_ROOT_PATH, new Watcher() {
                    @Override
                    public void process(WatchedEvent event) {
                        if (event.getState().equals(Event.EventType.NodeChildrenChanged)) { //子节点列表发生修改的话
                            queryNodes(zooKeeper);
                        }
                    }
                });
                for (String node : nodelist) {
                    byte[] nodeInfo = zooKeeper.getData(Global.REGISTER_ROOT_PATH + "/" + node, false, null);
                    serverNodes.add(new String(nodeInfo));
                }
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("查找的可用服务列表：" + serverNodes);
    }

    public static void main(String[] args) {
        ServiceDiscovery sr = new ServiceDiscovery("106.12.78.128:2181");
        ZooKeeper zooo = sr.connectZk();
        sr.queryNodes(zooo);
        System.out.println(sr.serverNodes);
    }
}
