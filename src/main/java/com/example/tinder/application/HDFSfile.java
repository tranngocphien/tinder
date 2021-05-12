package com.example.tinder.application;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class HDFSfile {
    public static void write() throws URISyntaxException, IOException {
        Configuration config = new Configuration();
        config.set("fs.defaultFS", "hdfs://10.3.104.93:8020");

        try {

            FileSystem fs = FileSystem.get(new URI("hdfs://10.3.105.51:8020"),config, "tinder");
            String fileName = "/test.txt";
            Path path = new Path("/tinder/like" + fileName);

            if (fs.exists(path)) {
                return;
            }

            FSDataOutputStream out = fs.create(path);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));

            bufferedWriter.write("Java API to write data in HDFS");
            bufferedWriter.newLine();
            bufferedWriter.close();
            fs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeFile(String directory, String content){
        Configuration config = new Configuration();
        config.set("fs.defaultFS", "hdfs://10.3.105.51:8020");

        try {

            FileSystem fs = FileSystem.get(new URI("hdfs://10.3.105.51:8020"),config, "tinder");
            Path path = new Path("/tinder/" + directory);
            System.out.println(directory);

            if (fs.exists(path)) {
                FSDataOutputStream fsDataOutputStream = fs.append(path);
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fsDataOutputStream,StandardCharsets.UTF_8));
                bufferedWriter.write(content);
                bufferedWriter.newLine();
                bufferedWriter.close();
                fs.close();
            }
            else {

                FSDataOutputStream out = fs.create(path);
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
                bufferedWriter.write(content);
                bufferedWriter.newLine();
                bufferedWriter.close();
                fs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void read(){

    }


    public static void main(String[] args) throws URISyntaxException, IOException {
        write();
    }
}
