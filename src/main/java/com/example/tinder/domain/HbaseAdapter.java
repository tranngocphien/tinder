package com.example.tinder.domain;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.HBaseAdmin;

import java.io.IOException;

public class HbaseAdapter {

    private Connection connection;

    public HbaseAdapter(String zkHosts, int zkPort, String zkBasePath) throws IOException {
        Configuration config = HBaseConfiguration.create();
        config.set(HConstants.ZOOKEEPER_QUORUM, zkHosts);
        config.setInt(HConstants.ZOOKEEPER_CLIENT_PORT, zkPort);
        config.set(HConstants.ZOOKEEPER_ZNODE_PARENT, zkBasePath);
        config.setInt(HConstants.HBASE_RPC_TIMEOUT_KEY, 10000);
        HBaseAdmin.available(config);
        this.connection = ConnectionFactory.createConnection(config);
    }

    public Connection getConnection(){
        return this.connection;
    }
}
