package org.roy.demo.streaming.state

import java.sql.Timestamp

object TmpMain {
  case class orderInfoStoretest(orderId: String, otype: Int, storeId: String, money: Double, orderDate: String)

  def main(args: Array[String]): Unit = {

    val s=orderInfoStoretest("1",1,"200",100.0,"2019")
    val events: Iterator[orderInfoStoretest]=Iterator(s)
    val order_moneys = events.map(_.money).reduce(_+_)//其实只会一个

    println(order_moneys)
  }
}
