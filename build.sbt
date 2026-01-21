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

// Force the runtime to use this version to override Play's internal old version
dependencyOverrides += "net.java.dev.jna" % "jna" % "5.13.0"

// Disable the old Play enhancer to avoid API conflicts
playEnhancerEnabled := false

// This uses a different syntax that should be compatible with Play 2.6
// Change the polling line to this (2000ms = 2 seconds)
PlayKeys.fileWatchService := play.dev.filewatch.FileWatchService.polling(2000)// OR the "nuclear" option to stop it from even trying:
watchSources := Seq()