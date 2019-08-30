package streaming.structured

import java.sql.Timestamp

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._
import streaming.StreamingExamples
import org.apache.log4j.{Level, Logger}

object WindowOperationsTest {
  Logger.getLogger("org.apache.spark").setLevel(Level.WARN)

  def main(args: Array[String]): Unit = {

    StreamingExamples.setStreamingLogLevels()

    val spark = SparkSession
      .builder
      .master("local[*]")
      .appName("WindowOperationsTest")
      .getOrCreate()

    // Create DataFrame representing the stream of input lines from connection to localhost:9999
    val lines: DataFrame = spark.readStream
      .format("socket")
      .option("host", "10.200.102.192")
      .option("port", 9998)
      .option("includeTimestamp", true)
      .load()
    import spark.implicits._
    // Split the lines into words
    //    val words = lines.as[String].flatMap(_.split(" "))
    // Split the lines into words, retaining timestamps
    val words = lines.as[(String, Timestamp)].flatMap(line =>
      line._1.split(" ").map(word => (word, line._2))
    ).toDF("word", "timestamp")

    // Group the data by window and word and compute the count of each group
    val windowedCounts = words.groupBy(
      window($"timestamp", "5 minutes", "1 minutes"),
      $"word"
    ).count()

    val query = windowedCounts.writeStream
      .outputMode("complete")
      .format("console")
      .start()

    query.awaitTermination()
  }

}
