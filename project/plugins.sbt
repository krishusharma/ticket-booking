// Use dependencyOverrides instead of libraryDependencySchemes for sbt 1.3.13
dependencyOverrides += "org.scala-lang.modules" %% "scala-xml" % "1.2.0"

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.25")
addSbtPlugin("com.typesafe.sbt" % "sbt-play-ebean" % "4.1.3")
addSbtPlugin("org.foundweekends.giter8" % "sbt-giter8-scaffold" % "0.11.0")

// Keep JNA for Apple Silicon compatibility
libraryDependencies += "net.java.dev.jna" % "jna" % "5.13.0"
