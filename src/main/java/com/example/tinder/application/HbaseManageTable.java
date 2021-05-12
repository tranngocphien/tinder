package com.example.tinder.application;

import com.example.tinder.domain.HbaseAdapter;
import com.google.common.hash.Hashing;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.io.compress.Compression;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class HbaseManageTable {


    public static byte[] generateRowkey(int commentId) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        String rowkey = "comment" + commentId;
        byte[] hashCmtID = md5.digest(rowkey.getBytes());
        rowkey = Bytes.toString(hashCmtID).substring(0,6) + rowkey;
        return Bytes.toBytes(rowkey);
    }

    public static byte[] generateRowkey(long id){
        Hashings.sha1AsHex(Bytes.toBytes(id));
        byte[] key = Bytes.toBytes(id);
        String keyStr = Bytes.toString(key);
        String hash = Hashings.sha1AsHex(key);
        keyStr = hash + "|" + keyStr;
        return Bytes.toBytes(keyStr);
    }
    public static void createTable(Connection connection, TableName name, byte[] columnFamily) throws IOException {
        Admin admin = connection.getAdmin();
        if (admin.tableExists(name)) {
            return;
        }
        TableDescriptorBuilder tableDescriptorBuilder = TableDescriptorBuilder.newBuilder(name);
        ColumnFamilyDescriptorBuilder columnFamilyDescriptorBuilder = ColumnFamilyDescriptorBuilder.newBuilder(columnFamily).setMaxVersions(3)
                .setInMemory(true).setCompressionType(Compression.Algorithm.SNAPPY);
        tableDescriptorBuilder.setColumnFamily(columnFamilyDescriptorBuilder.build());
        tableDescriptorBuilder.setCompactionEnabled(true);
        admin.createTable(tableDescriptorBuilder.build());
    }

    public static void deleteTable(Connection connection, TableName name) throws IOException {
        Admin admin = connection.getAdmin();
        if (admin.tableExists(name)) {
            admin.deleteTable(name);
        } else {
            System.out.println("Delete failed");
        }
    }


    public static void insertData(Connection connection, TableName tableName) throws IOException {
        //Admin admin = connection.getAdmin();
        //TableDescriptor tableDescriptor = admin.getDescriptor(tableName);
        Table table = connection.getTable(tableName);
        Put put1 = new Put(Bytes.toBytes("row1"));
        Put put2 = new Put(Bytes.toBytes("row2"));
        Put put3 = new Put(Bytes.toBytes("row3"));
        put1.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("statusId"), Bytes.toBytes("1"));
        put2.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("statusId"), Bytes.toBytes("2"));
        put3.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("statusId"), Bytes.toBytes("3"));
        table.put(put1);
        table.put(put2);
        table.put(put3);
    }


    public static void insertData(Connection connection, TableName tableName, byte[] columnFamily, byte[] qualifier, byte[] value) throws IOException {
        Table table = connection.getTable(tableName);

        Put put1 = new Put(Bytes.toBytes("row1"));

        put1.addColumn(columnFamily, qualifier, value);
        table.put(put1);

    }

    public static void insertData(Connection connection, TableName tableName, byte[] columnFamily, byte[] qualifier, byte[] value, byte[] rowkey) throws IOException {
        Table table = connection.getTable(tableName);
        Put put = new Put(rowkey);
        put.addColumn(columnFamily, qualifier, value);
        table.put(put);
    }

    public static void getData(Connection connection, TableName tableName) throws IOException {
        Table table = connection.getTable(tableName);
        Get get = new Get(Bytes.toBytes("row1"));
        get.addFamily(Bytes.toBytes("cf"));

        Result result = table.get(get);
        byte[] byte1 = result.getValue(Bytes.toBytes("cf"), Bytes.toBytes("content"));
        System.out.println(Bytes.toString(byte1));

    }

    public static void deleteDate(Connection connection, TableName tableName) throws IOException {
        Table table = connection.getTable(tableName);
        Get get = new Get(Bytes.toBytes("row1"));
        get.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("statusId"));
        if (table.exists(get)) {
            Delete delete = new Delete(Bytes.toBytes("row1"));
            delete.addColumns(Bytes.toBytes("cf"), Bytes.toBytes("statusId"));
            table.delete(delete);
        }

    }

    public static void scanTable(Connection connection, TableName tableName) throws IOException {
        Table table = connection.getTable(tableName);

        Scan scan = new Scan();
        //scan.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("statusId"));
        scan.addFamily(Bytes.toBytes("cf"));

        ResultScanner results = table.getScanner(scan);
        for (Result result = results.next(); result != null; result = results.next()) {
            System.out.println(Bytes.toString(result.getRow()));
            //System.out.println(result);
        }
        results.close();
    }

    public static ResultScanner scanValueColumn(Connection connection, TableName tableName, byte[] columnFamily, byte[] qualifier, byte[] value) throws IOException {
        Table table = connection.getTable(tableName);

        Scan scan = new Scan();
        SingleColumnValueFilter filter = new SingleColumnValueFilter(columnFamily, qualifier, CompareOperator.EQUAL, value);
        scan.setFilter(filter);

        ResultScanner results = table.getScanner(scan);

        return results;
    }
    public static Connection getConnection() throws IOException {
        String zkHosts = "adt-ml-hbase-dev-104-93";
        int zkPort = 2181;
        String zkBasePath = "/hbase-unsecure";
        HbaseAdapter hbaseAdapter = new HbaseAdapter(zkHosts, zkPort, zkBasePath);
        return hbaseAdapter.getConnection();
    }
    public static void main(String[] args) throws IOException {
        String zkHosts = "adt-ml-hbase-dev-104-93";
        int zkPort = 2181;
        String zkBasePath = "/hbase-unsecure";
        HbaseAdapter hbaseAdapter = new HbaseAdapter(zkHosts, zkPort, zkBasePath);
        Admin admin = hbaseAdapter.getConnection().getAdmin();
        List<TableDescriptor> list = hbaseAdapter.getConnection().getAdmin().listTableDescriptorsByNamespace(Bytes.toBytes("tinder"));
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).getTableName().toString());
        }
        //createTable(hbaseAdapter.getConnection(),TableName.valueOf("tinder","comment"), Bytes.toBytes("cf"));
        //insertData(hbaseAdapter.getConnection(), TableName.valueOf("tinder", "comment"));
        //insertData(hbaseAdapter.getConnection(), TableName.valueOf("tinder", "comment"),Bytes.toBytes("cf"), Bytes.toBytes("userId"), Bytes.toBytes("2"));
        //insertData(hbaseAdapter.getConnection(), TableName.valueOf("tinder", "comment"),Bytes.toBytes("cf"), Bytes.toBytes("content"), Bytes.toBytes("2"));
        //getData(hbaseAdapter.getConnection(), TableName.valueOf("tinder", "comment"));
        //deleteDate(hbaseAdapter.getConnection(), TableName.valueOf("tinder", "comment"));
        //scanTable(hbaseAdapter.getConnection(), TableName.valueOf("kinghub", "user_follow_news_user_stat_dev"));
        scanValueColumn(hbaseAdapter.getConnection(), TableName.valueOf("tinder", "comment"), Bytes.toBytes("cf"), Bytes.toBytes("statusId"), Bytes.toBytes("3"));
    }
}
