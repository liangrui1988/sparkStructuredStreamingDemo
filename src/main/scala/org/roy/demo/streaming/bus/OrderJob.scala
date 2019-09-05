package org.roy.demo.streaming.bus

import java.sql.Timestamp

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.functions.current_timestamp
import org.apache.spark.sql.{DataFrame, SparkSession}

object OrderJob {

  Logger.getLogger("org.apache.spark").setLevel(Level.WARN)


  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .master("local[*]")
      .appName("OrderJob")
      .getOrCreate()
    import spark.implicits._
    // Create DataFrame representing the stream of input lines from connection to localhost:9999
    val lines: DataFrame = spark.readStream
      .format("socket")
      .option("host", "10.200.102.192")
      .option("port", 9998)
      .load().withColumn("current_timestamp", current_timestamp)
    // Split the lines into words
    //    val words = lines.as[String].flatMap(_.split(" ")) (String, Timestamp)
    val words = lines.as[(String, Timestamp)].map(row => {
      val orderInfo = row._1.split(",")
      if (orderInfo != null && orderInfo.size > 4) {
        orderEvent(orderInfo(0), orderInfo(1).toInt, orderInfo(2), orderInfo(3).toDouble, orderInfo(4), row._2)
      } else {
        null
      }
    }).filter(row => row != null)
    words.printSchema()
    // Without watermark using orderId column,?? I want to overwrite the old data here
     words.dropDuplicates("orderId").createOrReplaceTempView("tmp_table")
    //    val sql = "select storeId,otype, count(1) num,sum(money) as moneys  from tmp_table group by storeId,otype"
    words.createOrReplaceTempView("tmp_table")
    //But it's not allowed. Is there any good suggestion?
    val sql =
      """
        |select tt1.storeId,tt1.otype, count(1) num,sum(money) as moneys  from (
        |select ROW_NUMBER() OVER (PARTITION BY orderId ORDER BY orderDate desc) as rn ,
        | t1.*  from  tmp_table as t1
        |) as tt1 where tt1.rn=1 group by storeId,otype
      """.stripMargin
    val resultDF = spark.sql(sql)
    // Start running the query that prints the running counts to the console
    val query = resultDF.writeStream
      .outputMode("complete")
      .format("console")
      .start()
    query.awaitTermination()

  }

}

case class orderEvent(orderId: String, otype: Int, storeId: String, money: Double, orderDate: String, timestamp: Timestamp)
