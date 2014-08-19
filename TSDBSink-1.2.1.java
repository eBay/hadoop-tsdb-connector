/*
Copyright [2014] eBay Software Foundation
 
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
 
    http://www.apache.org/licenses/LICENSE-2.0
 
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
 
 package org.apache.hadoop.metrics2.sink;
 
 import java.io.DataOutputStream;
 import java.io.IOException;
 import java.net.InetSocketAddress;
 import java.net.Socket;
 import java.net.SocketAddress;
 import java.net.UnknownHostException;
 import java.util.List;
 import org.apache.commons.configuration.SubsetConfiguration;
 import org.apache.commons.logging.Log;
 import org.apache.commons.logging.LogFactory;
 import org.apache.hadoop.metrics2.Metric;
 import org.apache.hadoop.metrics2.MetricsRecord;
 import org.apache.hadoop.metrics2.MetricsSink;
 import org.apache.hadoop.metrics2.MetricsTag;
 import org.apache.hadoop.metrics2.util.Servers;
 import org.apache.hadoop.net.DNS;
 
 public class TSDBSink
   implements MetricsSink
 {
   private static final String TSDB_SERVERS = "servers";
   private static final int DEFAULT_PORT = 4242;
   private DataOutputStream TSDBEndPoint;
   private List<? extends SocketAddress> TSDBServers;
   private Socket collector;
   private String fqdn = "";
   private String TSDBEntry = "put %s %d %s %s\n";
 
   public final Log LOG = LogFactory.getLog(getClass());
 
   public void init(SubsetConfiguration conf)
   {
     this.LOG.info("Initializing TSDBSink");
 
     if (conf.getString("slave.host.name") != null)
       this.fqdn = conf.getString("slave.host.name");
     else {
       try
       {
         this.fqdn = DNS.getDefaultHost(conf.getString("dfs.datanode.dns.interface", "default"), conf.getString("dfs.datanode.dns.nameserver", "default"));
       }
       catch (UnknownHostException exception)
       {
         this.LOG.error(exception);
       }
     }
 
     this.TSDBServers = Servers.parse(conf.getString("servers"), 4242);
     try
     {
       InetSocketAddress address = (InetSocketAddress)this.TSDBServers.get(0);
       this.collector = new Socket(address.getHostName(), address.getPort());
       this.TSDBEndPoint = new DataOutputStream(this.collector.getOutputStream());
     }
     catch (IOException exception) {
       this.LOG.error(exception);
     }
   }
 
   public void putMetrics(MetricsRecord record)
   {
     long timestamp = record.timestamp();
     String recordName = record.context() + "." + record.name();
     String tagFormat = "%s=%s";
     String tagValue = "";
     String dataPoint = "";
     String metricName = "";
 
     timestamp /= 1000L;
 
     for (MetricsTag tag : record.tags()) {
       tagValue = tagValue + String.format(tagFormat, new Object[] { tag.name(), String.valueOf(tag.value()) });
       tagValue = tagValue + " ";
     }
		
     for (Metric metric : record.metrics()) {
       
       metricName = recordName + "." + metric.name();
       dataPoint = String.valueOf(metric.value());
       String entry = String.format(this.TSDBEntry, new Object[] { metricName, Long.valueOf(timestamp), dataPoint, tagValue });
 
      // this.LOG.info("Msg to TSDB: " + entry);
       try {
         this.TSDBEndPoint.writeBytes(entry);
       }
       catch (IOException exception) {
         this.LOG.error(exception);
       }
     }
   }
 
   public void flush()
   {
   }
 }

