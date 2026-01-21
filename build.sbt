lazy val root = (project in file("."))
  .enablePlugins(PlayJava, PlayEbean)
  .settings(
    name := "ticket-booking"
  )

scalaVersion := "2.11.12"

libraryDependencies ++= Seq(
  javaJdbc,
  evolutions,
  javaWs,
  guice,
  "com.typesafe.play" %% "play-ebean" % "4.1.3",
  "mysql" % "mysql-connector-java" % "8.0.22",
  "org.redisson" % "redisson" % "3.17.7"
)
libraryDependencies += "net.java.dev.jna" % "jna" % "5.13.0"

dependencyOverrides += "net.java.dev.jna" % "jna" % "5.13.0"

playEnhancerEnabled := false


PlayKeys.fileWatchService := play.dev.filewatch.FileWatchService.polling(2000)
watchSources := Seq()