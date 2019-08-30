name := "sparkStructuredStreamingDemo"

version := "0.1"

scalaVersion := "2.11.12"

dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-core" % "2.6.7"
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.7"

// spark
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.3.0"
libraryDependencies += "org.apache.spark" %% "spark-core" % "2.3.0"
libraryDependencies += "org.apache.spark" %% "spark-hive-thriftserver" % "2.3.0"
libraryDependencies += "org.apache.spark" % "spark-streaming_2.11" % "2.3.0"

// mongo
libraryDependencies += "org.mongodb.spark" %% "mongo-spark-connector" % "2.3.0"

libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.25"

libraryDependencies ++= Seq("junit" % "junit" % "4.8.1" % "test")

libraryDependencies += "io.netty" % "netty-all" % "4.1.18.Final"
libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.47"


// 打包过滤 site.xml结尾的文件
excludeFilter in Runtime in unmanagedResources := "*site.xml"
mappings in(Compile, packageBin) ~= {
  _.filter(x =>{ !x._1.getName.endsWith("site.xml")})
}
