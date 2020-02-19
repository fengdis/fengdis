package com.fengdis.component.rpc.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.io.Serializable;

@org.springframework.context.annotation.Configuration
@ConditionalOnExpression("${hbase.enabled:false}")
public class HbaseConfig implements Serializable{

	private static final long serialVersionUID = -7137236230164276653L;

	@Value("${hbase.cluster.distributed:'false'}")
	private String cluster_distributed;

	@Value("${hbase.zookeeper.quorum:'127.0.0.1'}")
	private String zookeeper_quorum;

	@Value("${hbase.zookeeper.property.clientPort:'2181'}")
	private String zookeeper_port;

	@Bean
	public Connection hbaseConnectionFactory() {
		Connection connection = null;
		Configuration conf = HBaseConfiguration.create();
		conf.set("hbase.cluster.distributed", cluster_distributed);
		conf.set("hbase.zookeeper.quorum", zookeeper_quorum);
		conf.set("hbase.zookeeper.property.clientPort",zookeeper_port);
		//conf.set("hbase.rootdir", "hdfs://"+ PropertiesUtils.getProperty("hbase.zookeeper.host","master") + ":9000/hbase");
		try {
			connection = ConnectionFactory.createConnection(conf);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return connection;
	}


	@Value("${hadoop.hdfs:'hdfs://master:9000'}")
	private String defaultFS;

	@Bean
	public FileSystem hdfsFactory() {
		FileSystem fs = null;
		Configuration conf = new Configuration();
		conf.set("fs.defaultFS", defaultFS);
		try {
			fs = FileSystem.get(conf);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fs;
	}
	
}
