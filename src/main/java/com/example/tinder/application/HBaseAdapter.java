//package com.example.tinder.application;
//
//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.hbase.HBaseConfiguration;
//import org.apache.hadoop.hbase.HConstants;
//import org.apache.hadoop.hbase.client.Connection;
//import org.apache.hadoop.hbase.client.ConnectionFactory;
//import org.apache.hadoop.hbase.client.HBaseAdmin;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.IOException;
//import java.util.Arrays;
//
//public class HBaseAdapter {
//    protected final Logger logger = LoggerFactory.getLogger(getClass());
//    private final Connection connection;
//
//    public HBaseAdapter(String zkHosts, int zkPort, String zkBasePath) throws IOException {
//        Configuration config = HBaseConfiguration.create();
//        config.set(HConstants.ZOOKEEPER_QUORUM, zkHosts);
//        config.setInt(HConstants.ZOOKEEPER_CLIENT_PORT, zkPort);
//        config.set(HConstants.ZOOKEEPER_ZNODE_PARENT, zkBasePath);
//        config.setInt(HConstants.HBASE_RPC_TIMEOUT_KEY, 10000);
//        HBaseAdmin.available(config);
//        this.connection = ConnectionFactory.createConnection(config);
//    }
//
//    private static byte[] buildRowKey(String seed, byte[]... components) {
//        return HBaseUtils.buildCompositeKeyWithBucket(seed, components);
//    }
//
//    public static byte[] buildCommonRowKey(Object... components) {
//        byte[][] keyComponents = Arrays.stream(components).map(m -> m.toString().getBytes()).toArray(byte[][]::new);
//        return buildRowKey(components[0].toString(), keyComponents);
//    }
//
//    public Connection getConnection() {
//        return this.connection;
//    }
//}
