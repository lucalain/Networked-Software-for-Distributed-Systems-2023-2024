package it.polimi.nsds.spark.lab.friends;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.ArrayList;
import java.util.List;

/**
 * Implement an iterative algorithm that implements the transitive closure of friends
 * (people that are friends of friends of ... of my friends).
 *
 * Set the value of the flag useCache to see the effects of caching.
 */
public class FriendsComputation {
    private static final boolean useCache = true;

    public static void main(String[] args) {
        final String master = args.length > 0 ? args[0] : "local[4]";
        final String filePath = args.length > 1 ? args[1] : "./";
        final String appName = useCache ? "FriendsCache" : "FriendsNoCache";

        final SparkSession spark = SparkSession
                .builder()
                .master(master)
                .appName(appName)
                .getOrCreate();
        spark.sparkContext().setLogLevel("ERROR");

        final List<StructField> fields = new ArrayList<>();
        fields.add(DataTypes.createStructField("person", DataTypes.StringType, false));
        fields.add(DataTypes.createStructField("friend", DataTypes.StringType, false));
        final StructType schema = DataTypes.createStructType(fields);

        Dataset<Row> input = spark
                .read()
                .option("header", "false")
                .option("delimiter", ",")
                .schema(schema)
                .csv(filePath + "files/friends/friends.csv");

        Dataset<Row> oldInput = input;
        input.cache();
        Dataset<Row> input2;
        Dataset<Row> inputJoin;

        long numberOfRows = 0;

        while (numberOfRows != input.count())
        {
            System.out.println("aaa");
            numberOfRows = input.count();
            input2 = input.select(input.col("person").as("friend1"), input.col("friend").as("friend2"));

            input2.cache();

            inputJoin = input
                .join(input2, input2.col("friend1")
                        .equalTo(input.col("friend")), "inner")
                .select(input.col("person"), input2.col("friend2").as("friend"));

            input2.unpersist();

            inputJoin.cache();

            input = inputJoin.unionByName(input).distinct();

            inputJoin.unpersist();
            input.cache();

            oldInput.unpersist();
            oldInput = input;

        }
        input.orderBy("person", "friend").show();
        input.unpersist();
        spark.close();
    }
}
