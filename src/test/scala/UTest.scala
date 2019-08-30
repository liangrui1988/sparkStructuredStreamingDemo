import org.junit.Test

import scala.util.Try

class UTest {
  /**@author keon
    * 字符串变数值，空值问题
    */
  @Test def testz(): Unit = {
    val yesterday_order_Str:String =   null
    println( Try(yesterday_order_Str.toDouble).toOption.getOrElse[Double](0).toInt )
    println( Try("null".toDouble).toOption.getOrElse[Double](0).toInt )
    println( Try("12.23".toDouble).toOption.getOrElse[Double](0).toInt )

  }



}
