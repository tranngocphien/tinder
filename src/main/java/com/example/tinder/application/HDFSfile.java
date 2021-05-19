package com.example.tinder.application;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.ipc.RPC;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class HDFSfile {
    public static void write() throws URISyntaxException, IOException {
        Configuration config = new Configuration();
        config.set("fs.defaultFS", "hdfs://10.3.104.93:8020");

        try {
            FileSystem fs = FileSystem.get(new URI("hdfs://10.3.105.51:8020"),config, "tinder");
            DistributedFileSystem dfs = (DistributedFileSystem) fs;
            DFSClient client = dfs.getClient();
            InetSocketAddress serverAddress = RPC.getServerAddress(client.getNamenode());
            System.out.println(serverAddress.getHostName());
            fs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void delete() throws IOException, InterruptedException {
        Configuration conf=new Configuration(false);
        String nameservices = "lotusdev";
        String[] namenodesAddr = {"adt-ml-hbase-dev-104-93:8020","adt-ml-hbase-dev-105-51:8020"};
        String[] namenodes = {"nn1","nn2"};
        conf.set("fs.defaultFS", "hdfs://" + nameservices);
        conf.set("dfs.nameservices",nameservices);
        conf.set("dfs.ha.namenodes." + nameservices, namenodes[0]+","+namenodes[1]);
        conf.set("dfs.namenode.rpc-address." + nameservices + "." + namenodes[0], namenodesAddr[0]);
        conf.set("dfs.namenode.rpc-address." + nameservices + "." + namenodes[1], namenodesAddr[1]);
        conf.set("dfs.client.failover.proxy.provider." + nameservices,"org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider");


        String hdfsRPCUrl = "hdfs://" + nameservices + ":" + 8020;

        DistributedFileSystem dfs = (DistributedFileSystem) DistributedFileSystem.get(URI.create(hdfsRPCUrl), conf, "tinder");

        dfs.delete(new Path("/tinder/follow/follow2021-05-17.txt"), true);

    }

    public static void getActiveNamenode() throws IOException, InterruptedException {
        Configuration conf=new Configuration(false);
        String nameservices = "lotusdev";
        String[] namenodesAddr = {"adt-ml-hbase-dev-104-93:8020","adt-ml-hbase-dev-105-51:8020"};
        String[] namenodes = {"nn1","nn2"};
        conf.set("fs.defaultFS", "hdfs://" + nameservices);
        conf.set("dfs.nameservices",nameservices);
        conf.set("dfs.ha.namenodes." + nameservices, namenodes[0]+","+namenodes[1]);
        conf.set("dfs.namenode.rpc-address." + nameservices + "." + namenodes[0], namenodesAddr[0]);
        conf.set("dfs.namenode.rpc-address." + nameservices + "." + namenodes[1], namenodesAddr[1]);
        conf.set("dfs.client.failover.proxy.provider." + nameservices,"org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider");


        String hdfsRPCUrl = "hdfs://" + nameservices + ":" + 8020;

        DistributedFileSystem dfs = (DistributedFileSystem) DistributedFileSystem.get(URI.create(hdfsRPCUrl), conf, "tinder");

        Path path = new Path("/tinder/like/testt.txt");
        if(dfs.exists(path)){
            System.out.println("EXIST");
        }
        else {
            FSDataOutputStream out = dfs.create(path);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
            bufferedWriter.write("content");
            bufferedWriter.newLine();
            bufferedWriter.close();
            dfs.close();
        }

    }

    public static void writeFile(String directory, String content) throws IOException, InterruptedException {
        Configuration conf=new Configuration(false);
        String nameservices = "lotusdev";
        String[] namenodesAddr = {"adt-ml-hbase-dev-104-93:8020","adt-ml-hbase-dev-105-51:8020"};
        String[] namenodes = {"nn1","nn2"};
        conf.set("fs.defaultFS", "hdfs://" + nameservices);
        conf.set("dfs.nameservices",nameservices);
        conf.set("dfs.ha.namenodes." + nameservices, namenodes[0]+","+namenodes[1]);
        conf.set("dfs.namenode.rpc-address." + nameservices + "." + namenodes[0], namenodesAddr[0]);
        conf.set("dfs.namenode.rpc-address." + nameservices + "." + namenodes[1], namenodesAddr[1]);
        conf.set("dfs.client.failover.proxy.provider." + nameservices,"org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider");


        String hdfsRPCUrl = "hdfs://" + nameservices + ":" + 8020;


        try {

            FileSystem dfs =  FileSystem.get(URI.create(hdfsRPCUrl), conf, "tinder");
            Path path = new Path("/tinder/" + directory);
            System.out.println(directory);

            if (dfs.exists(path)) {
                FSDataOutputStream fsDataOutputStream = dfs.append(path);
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fsDataOutputStream,StandardCharsets.UTF_8));
                bufferedWriter.write(content);
                bufferedWriter.newLine();
                bufferedWriter.close();
                dfs.close();
            }
            else {

                FSDataOutputStream out = dfs.create(path);
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
                bufferedWriter.write(content);
                bufferedWriter.newLine();
                bufferedWriter.close();
                dfs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {

    }
}
