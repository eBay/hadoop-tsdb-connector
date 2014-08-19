The aim of the  TSDB Connector for Hadoop project is 

Developing a solution to push metrics to TSDB which is highly reliable and a built-in hadoop solution. 

Every machine in hadoop cluster has a set of metrics associated with it which convey the state of the Hadoop process / system at that specific time. Currently there are external agents which are used to push these "metrics" to the database (TSDB ) . 

We propose a solution to push these metrics from within the hadoop process itself providing more reliability inc ase the external agent stops working and 


# Compatible Versions:-
This system was tested with Hadoop versions :-
1. Hadoop 1.2.1 
2. Hadoop 2.4.1

and OpenTSDB 2.0

The 2 files here are  TSDBSink-1.2.1.java and TSDBSink-2.4.1.java for Hadoop 1.2.1 and Hadoop 2.4.1 Version respectively 


# Instructions :-

To use this Sink you need to place the files in the following folder 

For Hadoop 1.2.1
<hadoop-1.2.1 source folder>/src/core/org/apache/hadoop/metrics2/sink/TSDBSink.java

For Hadoop 2.4.1
<hadoop-2.4.1 source folder >/hadoop-common-project/hadoop-common/src/main/java/org/apache/hadoop/metrics2/sink/TSDBSink.java

and compile hadoop. 


Change the hadoop-metrics2.properties file and add the following in the sink 


*.sink.tsdb.class=org.apache.hadoop.metrics2.sink.TSDBSink

*.period=10

namenode.sink.tsdb.servers="TSDB Server hostname":4242

datanode.sink.tsdb.servers="TSDB Server hostname":4242



# Limitations:-

There are some limitations to this project. In case the TSDB server is restarted , the metrics are stopped . 


# Roadmap:-

1. We are currently working on adding code to reset the connection in such a case upto a certain number of retires 

2. We are also adding the option for resetting connection in the hadoop dfsadmin utility. 




