package org.myorg;
 	
 	import java.io.IOException;
 	import java.util.*;
 	
 	import org.apache.hadoop.fs.Path;
 	import org.apache.hadoop.conf.*;
 	import org.apache.hadoop.io.*;
 	import org.apache.hadoop.mapred.*;
 	import org.apache.hadoop.util.*;
 	
public class CookieJob {
 	
    public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {
	
 	
	public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
	    String line[] = value.toString().split(",");
	    String date = line[1];
	    String cookie = line[0];

	    output.collect(new Text(date), new Text(cookie));
	
	}
    }
 	
    public static class Reduce extends MapReduceBase implements Reducer<Text, Text, Text, Text> {
	public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text > output, Reporter reporter) throws IOException {
	    int count = 0;
	    int sum = 0;
	    List<String> cookies = new ArrayList<String>();
	    while (values.hasNext()) {
		String cookie = values.next().toString();
		if (!(cookies.contains(cookie)))
		    {
			cookies.add(cookie);
			count++;
		    }
		sum ++; 
	
	    }
	    output.collect(key, new Text(count + " " + sum));
	}
    }
    
    public static void main(String[] args) throws Exception {
	JobConf conf = new JobConf(CookieJob.class);
	conf.setJobName("cookiejob");
 	
	conf.setOutputKeyClass(Text.class);
	conf.setOutputValueClass(Text.class);
 	
	conf.setMapperClass(Map.class);
	//  conf.setCombinerClass(Reduce.class);
	conf.setReducerClass(Reduce.class);
 	
	conf.setInputFormat(TextInputFormat.class);
	conf.setOutputFormat(TextOutputFormat.class);
 	
	FileInputFormat.setInputPaths(conf, new Path(args[0]));
	FileOutputFormat.setOutputPath(conf, new Path(args[1]));
 	
	JobClient.runJob(conf);
    }
}