package it.polimi.middleware.spark.lab.cities;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.apache.spark.sql.functions.*;

public class Cities {
    public static void main(String[] args) throws TimeoutException {
        final String master = args.length > 0 ? args[0] : "local[4]";
        final String filePath = args.length > 1 ? args[1] : "./";

        final SparkSession spark = SparkSession
                .builder()
                .master(master)
                .appName("SparkEval")
                .getOrCreate();
        spark.sparkContext().setLogLevel("ERROR");

        final List<StructField> citiesRegionsFields = new ArrayList<>();
        citiesRegionsFields.add(DataTypes.createStructField("city", DataTypes.StringType, false));
        citiesRegionsFields.add(DataTypes.createStructField("region", DataTypes.StringType, false));
        final StructType citiesRegionsSchema = DataTypes.createStructType(citiesRegionsFields);

        final List<StructField> citiesPopulationFields = new ArrayList<>();
        citiesPopulationFields.add(DataTypes.createStructField("id", DataTypes.IntegerType, false));
        citiesPopulationFields.add(DataTypes.createStructField("city", DataTypes.StringType, false));
        citiesPopulationFields.add(DataTypes.createStructField("population", DataTypes.IntegerType, false));
        final StructType citiesPopulationSchema = DataTypes.createStructType(citiesPopulationFields);

        final Dataset<Row> citiesPopulation = spark
                .read()
                .option("header", "true")
                .option("delimiter", ";")
                .schema(citiesPopulationSchema)
                .csv(filePath + "files/cities/cities_population.csv");

        final Dataset<Row> citiesRegions = spark
                .read()
                .option("header", "true")
                .option("delimiter", ";")
                .schema(citiesRegionsSchema)
                .csv(filePath + "files/cities/cities_regions.csv");


        final Dataset<Row> qJoin = citiesRegions
                .join(citiesPopulation, citiesRegions.col("city").equalTo(citiesPopulation.col("city")))
                .drop(citiesRegions.col("city"));

        qJoin.cache();


        final Dataset<Row> q1 = qJoin.groupBy(citiesRegions.col("region"))
                .sum("population")
                .select(citiesRegions.col("region"), col("sum(population)").as("population"));

        q1.show();

        final Dataset<Row> q2 = qJoin.groupBy(citiesRegions.col("region"))
                .agg(max("population"), count("city"));

        q2.show();

        // JavaRDD where each element is an integer and represents the population of a city
        JavaRDD<Integer> population = citiesPopulation.toJavaRDD().map(r -> r.getInt(2));

        JavaRDD<Integer> oldPopulation = population;
        population.cache();

        long sum = sumPopulation(population);

        int year = 0;
        while(sum< 100000000){

            year++;
            population = population.map(r -> {
                if (r > 1000) {
                    return (int)( r * 1.01);
                } else {
                    return (int) (r * 0.99);
                }
            });

            population.cache();

            sum = sumPopulation(population);

            oldPopulation.unpersist();
            oldPopulation = population;

            System.out.println("Year: " + year + ", total population: " + sum);

        }

        // Bookings: the value represents the city of the booking
        final Dataset<Row> bookings = spark
                .readStream()
                .format("rate")
                .option("rowsPerSecond", 100)
                .load();

        final StreamingQuery q4 = bookings.join(
              qJoin, qJoin.col("id").equalTo(bookings.col("value")))
                .groupBy(window(col("timestamp"), "30 seconds", "5 seconds"), col("region"))
                .count()
                .writeStream()
                .outputMode("update").format("console")
                .option("truncate", false)
                .start();

        try {
            q4.awaitTermination();
        } catch (final StreamingQueryException e) {
            e.printStackTrace();
        }

        spark.close();
    }

    private static final long sumPopulation(JavaRDD<Integer> investments) {
        return investments.reduce((a, b) -> a+b);
    }
}