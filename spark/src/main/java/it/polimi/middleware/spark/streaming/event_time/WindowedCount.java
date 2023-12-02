package it.polimi.middleware.spark.streaming.event_time;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;

import java.util.concurrent.TimeoutException;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.window;

public class WindowedCount {
    public static void main(String[] args) throws TimeoutException {
        final String master = args.length > 0 ? args[0] : "local[4]";

        final SparkSession spark = SparkSession
                .builder()
                .master(master)
                .appName("WindowedCount")
                .getOrCreate();

        // Create DataFrame from a rate source.
        // A rate source generates records with a timestamp and a value at a fixed rate.
        // It is used for testing and benchmarking.
        final Dataset<Row> inputRecords = spark
                .readStream()
                .format("rate")
                .option("rowsPerSecond", 10)
                .load();
        spark.sparkContext().setLogLevel("ERROR");

        inputRecords.withWatermark("timestamp", "1 hour");

        // Q1: group by window (size 30 seconds, slide 10 seconds)
/*
        final StreamingQuery query1 = inputRecords
                .groupBy(window(col("timestamp"), "30 seconds", "10 seconds"))
                .count()
                .writeStream()
                .outputMode("update").format("console")
                .option("truncate", false)
                .start();
*/
        // TODO

        // Q2: group by window (size 30 seconds, slide 10 seconds) and value
/*
        final StreamingQuery query2 = inputRecords
                .groupBy(
                        window(col("timestamp"), "30 seconds", "10 seconds"),
                        col("value"))
                .count()
                .writeStream()
                .outputMode("update").format("console")
                .option("truncate", false)
                .start();*/
        // TODO

        // Q3: group only by value

        final StreamingQuery query = inputRecords
                .groupBy(
                        col("value"))
                .count()
                .writeStream()
                .outputMode("update").format("console")
                .option("truncate", false)
                .start();
        // TODO

        try {
            query.awaitTermination();
        } catch (final StreamingQueryException e) {
            e.printStackTrace();
        }
        spark.close();
    }

}