package it.polimi.middleware.spark.batch.wordcount;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaDoubleRDD;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.util.Arrays;

public class WordCountModified {

    public static void main(String[] args) {
        final String master = args.length > 0 ? args[0] : "local[4]";
        final String filePath = args.length > 1 ? args[1] : "./";

        final SparkConf conf = new SparkConf().setMaster(master).setAppName("WordCount");
        final JavaSparkContext sc = new JavaSparkContext(conf);
        sc.setLogLevel("ERROR");

        final JavaRDD<String> lines = sc.textFile(filePath + "/files/wordcount/in.txt");

        //final JavaRDD<String> words = lines.flatMap(line -> Arrays.asList(line.split(" ")).iterator());
        //final JavaPairRDD<String, Integer> pairs = words.mapToPair(s -> new Tuple2<>(s, 1));

        // Q1. For each character, compute the number of words starting with that character

        /*
        final JavaRDD<String> words = lines.flatMap(line -> Arrays.asList(line.split(" ")).iterator());
        final JavaPairRDD<Character, Integer> pairs = words.mapToPair(s -> new Tuple2<>(s.toLowerCase().charAt(0), 1));
        final JavaPairRDD<Character, Integer> counts = pairs.reduceByKey((a, b) -> a + b);
        System.out.println(counts.collect());
*/
        // Q2. For each character, compute the number of lines starting with that character
/*
        final JavaPairRDD<Character, Integer> pairs = lines.mapToPair(s -> new Tuple2<>(s.toLowerCase().charAt(0), 1));
        final JavaPairRDD<Character, Integer> counts = pairs.reduceByKey((a, b) -> a + b);
        System.out.println(counts.collect());
*/

        // Q3. Compute the average number of characters in each line

        final JavaDoubleRDD lineLength = lines.mapToDouble(String::length);
        final Double mean = lineLength.mean();
        System.out.println(mean);

        sc.close();
    }

}