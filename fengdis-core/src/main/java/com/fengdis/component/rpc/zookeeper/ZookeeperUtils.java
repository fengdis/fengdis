package com.fengdis.component.rpc.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;

/**
 * @version 1.0
 * @Descrittion: zookeeper分布式锁（单例模式）
 * @author: fengdi
 * @since: 2019/08/28 17:26
 */
@Component
@ConditionalOnBean(ZooKeeper.class)
public class ZookeeperUtils {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ZooKeeper zookeeper;

    //zk是一个目录结构，root为最外层目录
    private String root = "/locks";
    //锁的名称
    private String lockName;
    //当前线程创建的序列node
    private ThreadLocal<String> nodeId = new ThreadLocal<>();
    //用来同步等待zkclient链接到了服务端
    private CountDownLatch connectedSignal = new CountDownLatch(1);

    private final static byte[] data = new byte[0];

    class LockWatcher implements Watcher {
        private CountDownLatch latch = null;

        public LockWatcher(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void process(WatchedEvent event) {

            if (event.getType() == Event.EventType.NodeDeleted)
                latch.countDown();
        }
    }

    public void lock() {
        try {
            this.lockName = "locks";

            try {
                connectedSignal.await();
                Stat stat = zookeeper.exists(root, false);
                if (null == stat) {
                    // 创建根节点
                    zookeeper.create(root, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // 创建临时子节点
            String myNode = zookeeper.create(root + "/" + lockName, data, ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL_SEQUENTIAL);

            logger.info(Thread.currentThread().getName() + " " + myNode + " is created");

            // 取出所有子节点
            List<String> subNodes = zookeeper.getChildren(root, false);
            TreeSet<String> sortedNodes = new TreeSet<>();
            for (String node : subNodes) {
                sortedNodes.add(root + "/" + node);
            }

            String smallNode = sortedNodes.first();
            String preNode = sortedNodes.lower(myNode);

            if (myNode.equals(smallNode)) {
                // 如果是最小的节点,则表示取得锁
                logger.info(Thread.currentThread().getName() + " " + myNode + " get lock");
                this.nodeId.set(myNode);
                return;
            }

            CountDownLatch latch = new CountDownLatch(1);
            Stat stat = zookeeper.exists(preNode, new LockWatcher(latch));// 同时注册监听。
            // 判断比自己小一个数的节点是否存在,如果不存在则无需等待锁,同时注册监听
            if (stat != null) {
                logger.info(Thread.currentThread().getName() + " " + myNode + " waiting for " + root + "/" + preNode + " released lock");
                latch.await();// 等待，这里应该一直等待其他线程释放锁
                nodeId.set(myNode);
                latch = null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void unlock() {
        try {
            logger.info(Thread.currentThread().getName() + " " + nodeId.get() + " unlock ");
            if (null != nodeId) {
                zookeeper.delete(nodeId.get(), -1);
            }
            nodeId.remove();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建znode结点
     * @param path 结点路径
     * @param data 结点数据
     * @return true 创建结点成功 false表示结点存在
     * @throws Exception
     */
    public boolean addZnodeData(String path,String data,CreateMode mode) {
        try {
            if(zookeeper.exists(path, true) == null){
                zookeeper.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, mode);
                return true;
            }
        } catch (KeeperException | InterruptedException e) {
            throw new RuntimeException(String.format("创建znode:%s出现问题",path),e);
        }
        logger.info(String.format("znode:%s节点已存在",path));
        return false;
    }

    /**
     * 创建永久znode结点
     * @param path 结点路径
     * @param data 结点数据
     * @return true 创建结点成功 false表示结点存在
     * @throws Exception
     */
    public boolean addPZnode(String path,String data) {
        return addZnodeData(path,data,CreateMode.PERSISTENT);
    }

    /**
     * 创建临时znode结点
     * @param path 结点路径
     * @param data 结点数据
     * @return true 创建结点成功 false表示结点存在
     * @throws Exception
     */
    public boolean addZEnode(String path,String data) {
        return addZnodeData(path,data,CreateMode.EPHEMERAL);
    }

    /**
     * 修改znode
     * @param path 结点路径
     * @param data 结点数据
     * @return  修改结点成功   false表示结点不存在
     */
    public boolean updateZnode(String path,String data){
        try {
            Stat stat=null;
            if((stat=zookeeper.exists(path, true))!=null){
                zookeeper.setData(path, data.getBytes(), stat.getVersion());
                return true;
            }
        } catch (KeeperException | InterruptedException e) {
            throw new RuntimeException(String.format("修改znode:%s出现问题",path),e);
        }
        logger.info(String.format("znode:%s节点不存在",path));
        return false;
    }
    /**
     *
     * 删除结点
     * @param path 结点
     * @return true 删除键结点成功  false表示结点不存在
     */
    public boolean deleteZnode(String path){
        try {
            Stat stat=null;
            if((stat=zookeeper.exists(path, true))!=null){
                List<String> subPaths=zookeeper.getChildren(path, false);
                if(subPaths.isEmpty()){
                    zookeeper.delete(path, stat.getVersion());
                    return true;
                }else{
                    for (String subPath : subPaths) {
                        deleteZnode(path+"/"+subPath);
                    }
                }
            }
        } catch (InterruptedException | KeeperException e) {
            throw new RuntimeException(String.format("删除znode:%s出现问题",path),e);
        }
        logger.info(String.format("znode:%s节点不存在",path));
        return false;
    }
    /**
     * 取到结点数据
     * @param path 结点路径
     * @return null表示结点不存在 否则返回结点数据
     */
    public String getZnodeData(String path){
        String data=null;
        try {
            Stat stat=null;
            if((stat=zookeeper.exists(path, true))!=null){
                data=new String(zookeeper.getData(path, true, stat));
            }else{
                logger.info(String.format("znode:%s不存在",path));
            }
        } catch (KeeperException | InterruptedException e) {
            throw new RuntimeException(String.format("取到znode:%s出现问题",path),e);
        }
        logger.info(String.format("znode:%s节点不存在",path));
        return data;
    }

}
