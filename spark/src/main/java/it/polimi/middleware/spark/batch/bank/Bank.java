package it.polimi.middleware.spark.batch.bank;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import static org.apache.spark.sql.functions.*;

/**
 * Bank example
 *
 * Input: csv files with list of deposits and withdrawals, having the following
 * schema ("person: String, account: String, amount: Int)
 *
 * Queries
 * Q1. Print the total amount of withdrawals for each person.
 * Q2. Print the person with the maximum total amount of withdrawals
 * Q3. Print all the accounts with a negative balance
 */
public class Bank {
    private static final boolean useCache = true;

    public static void main(String[] args) {
        final String master = args.length > 0 ? args[0] : "local[4]";
        final String filePath = args.length > 1 ? args[1] : "./";
        final String appName = useCache ? "BankWithCache" : "BankNoCache";

        final SparkSession spark = SparkSession
                .builder()
                .master(master)
                .appName(appName)
                .getOrCreate();
        spark.sparkContext().setLogLevel("ERROR");

        final List<StructField> mySchemaFields = new ArrayList<>();
        mySchemaFields.add(DataTypes.createStructField("person", DataTypes.StringType, true));
        mySchemaFields.add(DataTypes.createStructField("account", DataTypes.StringType, true));
        mySchemaFields.add(DataTypes.createStructField("amount", DataTypes.IntegerType, true));
        final StructType mySchema = DataTypes.createStructType(mySchemaFields);

        final Dataset<Row> deposits = spark
                .read()
                .option("header", "false")
                .option("delimiter", ",")
                .schema(mySchema)
                .csv(filePath + "files/bank/deposits.csv");

        final Dataset<Row> withdrawals = spark
                .read()
                .option("header", "false")
                .option("delimiter", ",")
                .schema(mySchema)
                .csv(filePath + "files/bank/withdrawals.csv");

        // Q1. Total amount of withdrawals for each person
        final Dataset<Row> sumWithdrawals = withdrawals.groupBy("person").sum("amount");

        sumWithdrawals.cache();

        // Q2. Person with the maximum total amount of withdrawals
        /*sumWithdrawals
                .orderBy(col("sum(amount)").desc())
                .show(1);

        final long maxTotal = sumWithdrawals
                .agg(max("sum(amount)"))
                .first()
                .getLong(0);

        final Dataset<Row> maxWithdrawals = sumWithdrawals
                .filter(sumWithdrawals.col("sum(amount)").equalTo(maxTotal));
*/
        //maxWithdrawals.show();

        // Q3 Accounts with negative balance
        final Dataset<Row> sumDeposit = deposits.groupBy("person").sum("amount");
        sumDeposit.cache();

        /*sumDeposit.join(sumWithdrawals, sumWithdrawals.col("person").equalTo(sumDeposit.col("person")), "fullouter")
                .where(sumWithdrawals.col("sum(amount)").gt(sumDeposit.col("sum(amount)")).or(sumDeposit.col("sum(amount)").isNull().and(sumWithdrawals.col("sum(amount)").isNotNull())))
                .select(sumWithdrawals.col("person")).show();
*/
        // Q4 Accounts in descending order of balance
        System.out.println("Accounts in descending order of balance");

        final Dataset<Row> balance = sumDeposit.join(sumWithdrawals, sumWithdrawals.col("person").equalTo(sumDeposit.col("person")), "fullouter")
                        .select(sumWithdrawals.col("person"), coalesce(sumWithdrawals.col("sum(amount)"), lit(0)).as("withdrawal"), coalesce(sumDeposit.col("sum(amount)"), lit(0)).as("deposit"));
        balance.cache();
        //balance.show();
        final Dataset<Row> filteredBalance = balance.select(balance.col("person"), balance.col("deposit").$minus(balance.col("withdrawal")).as("total"));

        filteredBalance.cache();
        //filteredBalance.show();

        final Dataset<Row> orderedFilteredBalance = filteredBalance.orderBy(filteredBalance.col("total").desc());
        orderedFilteredBalance.cache();
        orderedFilteredBalance.show();


        spark.close();

    }
}